package com.faderw.venus.spider;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.faderw.venus.config.Config;
import com.faderw.venus.event.EventManager;
import com.faderw.venus.pipeline.Pipeline;
import com.faderw.venus.request.Parser;
import com.faderw.venus.request.Request;
import com.faderw.venus.response.Response;
import com.faderw.venus.response.Result;
import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @author FaderW
 * 爬虫处理器
 */
@Data
public abstract class Spider {

    protected Config config;
    protected String name;
    protected List<String> startUrls = Lists.newArrayList();
    protected List<Request> requests = Lists.newArrayList();
    protected List<Pipeline> pipelines = Lists.newArrayList();

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

    public List<Pipeline> getPipelines() {
        return this.pipelines;
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
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

    protected <T> Spider addPipline(Pipeline<T> pipeline) {
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * 构建一个request
     */
    public <T> Request<T> makeRequest(String url) {
        return makeRequest(url, this::parse);
    }

    public <T> Request<T> makeRequest(String url, Parser<T> parser) {
        return new Request<>(this, url, parser);
    }

    /**
     * 解析DOM
     * 子类需要实现此方法
     */
    protected abstract <T> Result<T> parse(Response response);

    protected void resetRequest(Consumer<Request> consumer) {
        this.resetRequest(this.requests, consumer);
    }

    protected void resetRequest(List<Request> requests, Consumer<Request> consumer) {
        requests.forEach(consumer::accept);
    }
}