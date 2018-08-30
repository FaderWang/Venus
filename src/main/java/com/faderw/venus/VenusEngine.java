package com.faderw.venus;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.faderw.venus.config.Config;
import com.faderw.venus.download.Downloader;
import com.faderw.venus.download.HttpClientDownloader;
import com.faderw.venus.event.EventManager;
import com.faderw.venus.pipeline.Pipeline;
import com.faderw.venus.request.Parser;
import com.faderw.venus.request.Request;
import com.faderw.venus.scheduler.Scheduler;
import com.faderw.venus.spider.Spider;
import com.faderw.venus.thread.CountableThreadPool;
import com.faderw.venus.util.VenusUtils;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FaderW
 * 核心控制器，全局流转引擎
 */
@Slf4j
public class VenusEngine {

    private Spider spider;
    private Config config;
    private Scheduler scheduler;
    private Downloader downloader;
    private List<Pipeline> pipelines = Lists.newArrayList();
    private CountableThreadPool threadPool;
    private volatile boolean isRunning;

    public VenusEngine(Spider spider, Config config) {
        this.downloader = new HttpClientDownloader();
        this.config = config;
        this.spider = spider;
        this.scheduler = new Scheduler();
        ExecutorService executorService = new ThreadPoolExecutor(config.parallelThreads(), config.parallelThreads(),
                60L, TimeUnit.MILLISECONDS, config.queueSize() == 0 ? new SynchronousQueue<>()
                : (config.queueSize() < 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(config.queueSize())),
                new ThreadFactoryBuilder().setNameFormat("task-thread-%d").build());

        this.threadPool = new CountableThreadPool(config.parallelThreads(), executorService);
    }

    public static VenusEngine create(Spider spider, Config config) {
        return new VenusEngine(spider, config);
    }

    public VenusEngine Spider(Spider spider) {
        if (spider != null) {
            throw new RuntimeException("已存在Spider!");
        }
        this.spider = spider;
        return this;
    }

    public VenusEngine scheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public VenusEngine downloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public VenusEngine pipeline(Pipeline pipeline) {
        this.pipelines.add(pipeline);
        return this;
    }

    public VenusEngine pipelines(List<Pipeline> pipelines) {
        this.pipelines.addAll(pipelines);
        return this;
    }

    public void start() {
        if (isRunning) {
            throw new RuntimeException("Venus 已经启动");
        }

        isRunning = true;
        log.info("爬虫程序启动");
        EventManager.fireEvent(EventManager.VenusEvent.SPIDER_STARTED, config);

        spider.setConfig(config.clone());
        List<Request> requestList = spider.getStartUrls().stream()
                .map(spider::makeRequest)
                .collect(Collectors.toList());
        scheduler.addRequest(requestList);

        while (!Thread.currentThread().isInterrupted() && isRunning) {
            if (!scheduler.hasRequest()) {
                if (threadPool.getThreadAlive().get() == 0) {
                    break;
                }
                VenusUtils.sleep(100);
                continue;
            }
            Request request = scheduler.nextRequest();
            threadPool.execute(() ->
                processRequest(request)
            );
        }

        stop();
        close();
    }

    private void processRequest(Request request) {
        Page page = downloader.download(request);
        if (page.isDownloadSuccess() && page.getHttpStatus() == 200) {
            Parser parser = request.getParser();
            parser.parse(page);

            if (!page.isSkip()) {
                //skip为true跳过pipeline处理
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(page.getItem(), request);
                }
            }
            extractAndAddRequests(page);
        } else {
            log.info("page status error url {},code {}", request.getUrl(), page.getHttpStatus());
        }
    }

    private void extractAndAddRequests(Page page) {
        for (Request request : page.getTargetRequest()) {
            scheduler.addRequest(request);
        }
    }

    public void stop(){
        isRunning = false;
        scheduler.clear();
        log.info("爬虫已经停止.");
    }

    public void close() {
        destroyObject(scheduler);
        destroyObject(spider);
    }

    private void destroyObject(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}