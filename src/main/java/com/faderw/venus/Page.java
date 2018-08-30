package com.faderw.venus;

import com.faderw.venus.request.Request;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 保存下载信息的page对象
 * @author FaderW
 * 2018/8/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    private List<Request> targetRequest = Lists.newArrayList();
    private Request request;
    private String rawText;
    private Object item;
    private boolean skip = false;
    private boolean isDownloadSuccess;
    private int httpStatus = 200;

    public static Page build() {
        return new Page();
    }

    public void addTargetRequest(Request request) {
        this.targetRequest.add(request);
    }

    public void addTargetRequests(List<Request> targetRequest) {
        this.targetRequest.addAll(targetRequest);
    }
}
