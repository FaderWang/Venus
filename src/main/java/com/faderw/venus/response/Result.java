package com.faderw.venus.response;

import java.util.List;

import com.faderw.venus.request.Request;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FaderW 
 */
public class Result<T> {
    private List<Request> requests = Lists.newArrayList();
    private T item;

    public Result(T item) {
        this.item = item;
    }

    public Result() {

    }

    public List<Request> getRequests() {
        return this.requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public T getItem() {
        return this.item;
    }

    public void setItem(T item) {
        this.item = item;
    }



    public void addRequest(Request request) {
        requests.add(request);
    }

    public void addRequests(List<Request> requests) {
        if (requests != null && requests.size() > 0) {
            this.requests.addAll(requests);
        }
    }
}