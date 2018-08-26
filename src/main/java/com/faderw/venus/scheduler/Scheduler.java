package com.faderw.venus.scheduler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import com.faderw.venus.request.Request;
import com.faderw.venus.response.Response;
import com.google.common.collect.Queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FaderW
 * 调度器
 */
@Slf4j
public class Scheduler {

    private BlockingQueue<Request> pending = Queues.newLinkedBlockingQueue();
    private BlockingQueue<Response> processed = Queues.newLinkedBlockingQueue();

    public void addRequest(Request request) {
        try {
            this.pending.put(request);
        } catch (Exception e) {
            log.info("调度器添加request出错", e);
        }
    }

    public void addResponse(Response response) {

        try {
            this.processed.put(response);
        } catch (InterruptedException e) {
            log.error("调度器添加response出错", e);
        }
    }

    public boolean hasRequest() {
        return pending.size() > 0;
    }

    public Request nextRequest() {
        try {
            return pending.take();
        } catch (InterruptedException e) {
            log.error("从调度器取出Request出错", e);
        }

        return null;
    }

    public boolean hasResponse() {
        return processed.size() > 0;
    }

    public Response nextResponse() {
        try {
            return processed.take();
        } catch (InterruptedException e) {
            log.error("从调度器取出Response出错", e);
        }
        return null;
    }

    public void addRequest(List<Request> requests) {
        requests.forEach(this::addRequest);
    }

    public void clear() {
        pending.clear();
    }   
}