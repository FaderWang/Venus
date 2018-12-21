package com.faderw.venus.spider;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.faderw.venus.Page;
import com.faderw.venus.config.Config;
import com.faderw.venus.event.EventManager;
import com.faderw.venus.proxy.Proxy;
import com.faderw.venus.request.Parser;
import com.faderw.venus.request.Request;
import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @author FaderW
 * 爬虫处理器
 */
@Data
public abstract class Spider {

    protected Config config;
    protected Proxy proxy;
    protected String name;
    protected List<String> startUrls = Lists.newArrayList();
    protected List<Request> requests = Lists.newArrayList();

    public void setConfig(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return this.config;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Proxy proxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public Spider() {

    }

    public Spider(String name) {
        this.name = name;
        EventManager.registerEvent(EventManager.VenusEvent.SPIDER_STARTED, this::onStart);

    }

    public List<String> getStartUrls() {
        return this.startUrls;
    }

    public Spider startUrls(String... urls) {
        this.startUrls.addAll(Arrays.asList(urls));
        return this;
    }

    public List<Request> getRequests() {
        return this.requests;
    }

    /**
     * 爬虫启动前执行
     */
    public abstract void onStart(Config config);


    /**
     * 构建一个request
     */
    public <T> Request<T> makeRequest(String url) {
        return makeRequest(url, this::parse);
    }

    public <T> Request<T> makeRequest(String url, Parser parser) {
        return new Request<>(this, url, parser);
    }

    /**
     * 解析DOM
     * 子类需要实现此方法
     */
    protected abstract void parse(Page page);

    protected void resetRequest(Consumer<Request> consumer) {
        this.resetRequest(this.requests, consumer);
    }

    protected void resetRequest(List<Request> requests, Consumer<Request> consumer) {
        requests.forEach(consumer::accept);
    }
}