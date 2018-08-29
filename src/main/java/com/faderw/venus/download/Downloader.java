package com.faderw.venus.download;

import java.io.IOException;
import java.io.InputStream;

import com.faderw.venus.request.Request;
import com.faderw.venus.response.Response;
import com.faderw.venus.scheduler.Scheduler;
import com.faderw.venus.spider.Spider;
import com.github.kevinsawicki.http.HttpRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
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
    private final HttpClientGenerator httpClientGenerator;

    public Downloader(Scheduler scheduler, Request request) {
        this.Scheduler = scheduler;
        this.request = request;
        this.httpClientGenerator = new HttpClientGenerator();
    }

	@Override
	public void run() {
        log.info("start request url {}", request.getUrl());

        CloseableHttpClient httpClient = httpClientGenerator.getHttpClient(request.getSpider().getConfig());
        HttpUriRequest httpUriRequest = httpClientGenerator.convertHttpUriRequest(request, request.getSpider().getConfig());

        String bodyString = null;
        CloseableHttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(httpUriRequest);
            bodyString = IOUtils.toString(httpResponse.getEntity().getContent(), request.charset());
        } catch (IOException e) {
            log.info("download request server error", e);
        } finally {
            if (httpResponse != null) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }

        log.info("download has finished url {}", request.getUrl());
        Response response = new Response(request, bodyString);
        Scheduler.addResponse(response);

	}

}