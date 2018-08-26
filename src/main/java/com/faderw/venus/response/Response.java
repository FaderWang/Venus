package com.faderw.venus.response;

import java.io.InputStream;

import com.faderw.venus.request.Request;

import lombok.Getter;

/**
 * @author FaderW
 */
public class Response {

    private Request request;
    private Body body;

    public Response(Request request, InputStream inputStream) {
        this.request = request;
        this.body = new Body(inputStream, request.charset());
    }

    public Body body() {
        return this.body;
    }

    public Request getRequest() {
        return this.request;
    }
}