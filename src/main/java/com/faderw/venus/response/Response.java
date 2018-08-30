//package com.faderw.venus.response;
//
//
//import com.faderw.venus.request.Request;
///**
// * @author FaderW
// */
//public class Response {
//
//    private Request request;
//    private Body body;
//
//    public Response(Request request, String bodyString) {
//        this.request = request;
//        this.body = new Body(bodyString, request.charset());
//    }
//
//    public Body body() {
//        return this.body;
//    }
//
//    public Request getRequest() {
//        return this.request;
//    }
//}