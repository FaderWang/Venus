package com.faderw.venus.request;

import java.util.Map;

import com.faderw.venus.spider.Spider;
import com.google.common.collect.Maps;

/**
 * @author FaderW
 */
public class Request<T> {
    private Spider spider;
    private String url;
    private String method = "GET";
    private String body;
    private Map<String, String> headers = Maps.newHashMap();
    private Map<String, String> cookies = Maps.newHashMap();
    private String contentType = "text/html;charset=UTF-8";
    private String charset = "UTF-8";
    private Parser parser;

    public Request(Spider spider, String url, Parser parser) {
        this.spider = spider;
        this.url = url;
        this.parser = parser;
    }

    public Spider getSpider() {
        return this.spider;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Parser getParser() {
        return this.parser;
    }

    public Map<String, String> headers() {
        return this.headers;
    }

    public Map<String, String> cookies() {
        return this.cookies;
    }

    public Request header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Request cookie(String key, String value) {
        this.cookies.put(key, value);
        return this;
    }

    public String header(String key) {
        return this.headers.get(key);
    }

    public String cookie(String key) {
        return this.cookies.get(key);
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public String contentType() {
        return contentType;
    }

    public Request contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String charset() {
        return charset;
    }

    public Request charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Request method(String method) {
        this.method = method;
        return this;
    }

    public String method() {
        return this.method;
    }

    public Request body(String body) {
        this.body = body;
        return this;
    }

    public String body() {
        return body;
    }
}