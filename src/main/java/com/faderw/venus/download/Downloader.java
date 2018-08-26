package com.faderw.venus.download;

import java.io.InputStream;

import com.faderw.venus.request.Request;
import com.faderw.venus.response.Response;
import com.faderw.venus.scheduler.Scheduler;
import com.faderw.venus.spider.Spider;
import com.github.kevinsawicki.http.HttpRequest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FaderW
 * 下载器
 */
@Slf4j
public class Downloader implements Runnable{


    private final Scheduler Scheduler;
    private final Request request;

    public Downloader(Scheduler scheduler, Request request) {
        this.Scheduler = scheduler;
        this.request = request;
    }

	@Override
	public void run() {
        log.info("start request url {}", request.getUrl());
        HttpRequest httpRequest = null;

        if ("get".equalsIgnoreCase(request.getMethod())) {
            httpRequest = HttpRequest.get(request.getUrl());
        } else if ("post".equalsIgnoreCase(request.getMethod())) {
            httpRequest = HttpRequest.post(request.getUrl());
        } else {
            log.error("method {} 无效", request.getMethod());
        }

        InputStream inputStream = httpRequest.contentType(request.contentType())
            .headers(request.headers()).connectTimeout(request.getSpider().getConfig().timeout())
            .readTimeout(request.getSpider().getConfig().timeout()).stream();

        log.info("download has finsihed url {}", request.getUrl());
        Response response = new Response(request, inputStream);
        Scheduler.addResponse(response);

	}

}