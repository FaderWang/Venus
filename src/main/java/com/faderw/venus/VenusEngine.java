package com.faderw.venus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.faderw.venus.config.Config;
import com.faderw.venus.download.Downloader;
import com.faderw.venus.event.EventManager;
import com.faderw.venus.event.EventManager.VenusEvent;
import com.faderw.venus.pipeline.Pipeline;
import com.faderw.venus.request.Parser;
import com.faderw.venus.request.Request;
import com.faderw.venus.response.Response;
import com.faderw.venus.response.Result;
import com.faderw.venus.scheduler.Scheduler;
import com.faderw.venus.spider.Spider;
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

    private List<Spider> spiders;
    private Config config;
    private Scheduler scheduler;
    private ExecutorService executorService;
    private boolean isRunning;

    public VenusEngine(Venus venus) {
        this.spiders = venus.spiders;
        this.config = venus.config;
        this.scheduler = new Scheduler();
        this.executorService = new ThreadPoolExecutor(config.parallelThreads(), config.parallelThreads(), 
            60L, TimeUnit.MILLISECONDS, config.queueSize() == 0 ? new SynchronousQueue<>()
            : (config.queueSize() < 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(config.queueSize())),
            new ThreadFactoryBuilder().setNameFormat("task-thread-%d").build());
    }

    public void start() {
        if (isRunning) {
            throw new RuntimeException("Venus 已经启动");
        }

        isRunning = true;
        log.info("全局启动事件");
        EventManager.fireEvent(VenusEvent.GLOBAL_STARTED, this.config);

        spiders.forEach(spider -> {
            // 使用克隆为每个spider对象设置一个config属性
            Config config = this.config.clone();
            spider.setConfig(config);
            
            List<Request> requests = spider.getStartUrls().stream()
                .map(spider::makeRequest).collect(Collectors.toList());
            
            spider.getRequests().addAll(requests);
            scheduler.addRequest(requests);
            EventManager.fireEvent(VenusEvent.SPIDER_STARTED, this.config);

        });

        // 开启一个线程来不断扫描是否有带爬取的Request
        Thread downloadThread = new Thread(() -> {
            while (isRunning) {
                if (!scheduler.hasRequest()) {
                    VenusUtils.sleep(100);
                    continue;
                }

                Request request = scheduler.nextRequest();
                executorService.submit(new Downloader(scheduler, request));
                VenusUtils.sleep(request.getSpider().getConfig().delay());
            }
        });

        downloadThread.setDaemon(true);
        downloadThread.setName("download-thread");
        downloadThread.start();

        //消费
        this.complete();
    }

    private void complete() {
        while (isRunning) {
            if (!scheduler.hasResponse()) {
                VenusUtils.sleep(100);
                continue;
            }
            Response response = scheduler.nextResponse();
            Parser parser   = response.getRequest().getParser();
            if (null != parser) {
                Result<?>     result   = parser.parse(response);
                List<Request> requests = result.getRequests();
                if (!VenusUtils.isEmpty(requests)) {
                    requests.forEach(scheduler::addRequest);
                }
                if (null != result.getItem()) {
                    List<Pipeline> pipelines = response.getRequest().getSpider().getPipelines();
                    pipelines.forEach(pipeline -> pipeline.process(result.getItem(), response.getRequest()));
                }
            }
        }
    }

    public void stop(){
        isRunning = false;
        scheduler.clear();
        log.info("爬虫已经停止.");
    }
}