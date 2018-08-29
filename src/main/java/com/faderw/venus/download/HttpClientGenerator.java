package com.faderw.venus.download;

import com.faderw.venus.config.Config;
import com.faderw.venus.constant.HttpConstant;
import com.faderw.venus.request.Request;
import com.faderw.venus.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author FaderW
 * 2018/8/27
 */
@Slf4j
public class HttpClientGenerator {

    private PoolingHttpClientConnectionManager clientConnectionManager;
    private final Map<String, CloseableHttpClient> httpClients = new ConcurrentHashMap<>();

    public HttpClientGenerator() {
        clientConnectionManager = new PoolingHttpClientConnectionManager();
        clientConnectionManager.setMaxTotal(500);
        clientConnectionManager.setDefaultMaxPerRoute(50);
    }

    public void setPoolSize(int poolSize) {
        clientConnectionManager.setDefaultMaxPerRoute(poolSize);
    }


    public CloseableHttpClient getHttpClient(Config config) {
        if (config == null) {
            return generateClient(null);
        }
        String domain = config.domain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            httpClient = generateClient(config);
            httpClients.put(domain, httpClient);
        }

        return httpClient;
    }

    public CloseableHttpClient generateClient(Config config) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(clientConnectionManager);

        //定义keep-alive策略
        httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator iterator = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (iterator.hasNext()) {
                    HeaderElement element = iterator.nextElement();
                    String param = element.getName();
                    String value = element.getValue();

                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException e) {
                            log.error("set keep-alive strategy error", e);
                        }

                    }
                }
                HttpHost target = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
                if ("".equalsIgnoreCase(target.getHostName())) {
                    return 5 * 1000;
                } else {
                    return 30 * 1000;
                }
            }
        });
        //设置重试次数
        httpClientBuilder.setRetryHandler(new VenusRetryHandler(config.retry()));
        return httpClientBuilder.build();
    }

    public HttpUriRequest convertHttpUriRequest(Request request, Config config) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(UrlUtils.fixIllegalCharacterInUrl(request.getUrl()));
        if (request.headers() != null) {
            Map<String ,String> headers = request.headers();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        if (config != null) {
            requestConfigBuilder.setConnectionRequestTimeout(config.timeout())
                    .setSocketTimeout(config.timeout())
                    .setConnectTimeout(config.timeout())
                    .setCookieSpec(CookieSpecs.STANDARD);
        }

        requestBuilder.setConfig(requestConfigBuilder.build());
        HttpUriRequest httpUriRequest = requestBuilder.build();
        if (request.headers() != null && !request.headers().isEmpty()) {
            Map<String, String> headers = request.headers();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpUriRequest.addHeader(header.getKey(), header.getValue());
            }
        }
        return httpUriRequest;
    }

    private RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();

        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            return addFormParams(RequestBuilder.post(),request);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return addFormParams(RequestBuilder.put(), request);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    private RequestBuilder addFormParams(RequestBuilder requestBuilder, Request request) {
        if (request.body() != null) {
            ByteArrayEntity entity = new ByteArrayEntity(request.body().getBytes());
            entity.setContentType(request.contentType());
            requestBuilder.setEntity(entity);
        }
        return requestBuilder;
    }

    static class VenusRetryHandler implements HttpRequestRetryHandler {

        private static final int DEFAULT_RETRY = 5;
        private int retry;

        public VenusRetryHandler(int retry) {
            this.retry = retry;
        }

        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (retry != 0 ? executionCount > retry : executionCount > DEFAULT_RETRY) {
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            if (exception instanceof UnknownHostException) {
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                return false;
            }
            if (exception instanceof SSLException) {
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                return true;
            }
            return false;
        }
    }
}
