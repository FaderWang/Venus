package com.faderw.venus.pipeline;

import com.faderw.venus.request.Request;

/**
 * @author FaderW
 * 数据处理管道
 */
public interface Pipeline<T> {

    void process(T item, Request<?> Request);
}