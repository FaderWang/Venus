package com.faderw.venus.download;

import java.io.IOException;

import com.faderw.venus.Page;
import com.faderw.venus.request.Request;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author FaderW
 * 下载器
 */
@Slf4j
@NoArgsConstructor
public class HttpClientDownloader implements Downloader{


    private final HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    @Override
    public Page download(Request request) {
        log.info("start to download for url-->{}", request.getUrl());

        CloseableHttpClient httpClient = httpClientGenerator.getHttpClient(request.getSpider().getConfig());
        HttpUriRequest httpUriRequest = httpClientGenerator.convertHttpUriRequest(request, request.getSpider().getConfig());

        Page page = Page.build();
        page.setRequest(request);
        CloseableHttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(httpUriRequest);
            String bodyString = IOUtils.toString(httpResponse.getEntity().getContent(), request.charset());
            log.info("download success url-->{}", request.getUrl());

            page.setRawText(bodyString);
            page.setHttpStatus(httpResponse.getStatusLine().getStatusCode());
            page.setDownloadSuccess(true);
        } catch (IOException e) {
            log.info("download error", e.getMessage(), e);

            page.setDownloadSuccess(false);
        } finally {
            if (httpResponse != null) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }

        return page;
    }
}