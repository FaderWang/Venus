package com.faderw.venus.request;

import com.faderw.venus.response.Response;
import com.faderw.venus.response.Result;

/**
 * @author FaderW
 */
public interface Parser<T> {

    Result<T> parse(Response response);
    
}