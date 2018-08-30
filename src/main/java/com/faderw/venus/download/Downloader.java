package com.faderw.venus.download;

import com.faderw.venus.Page;
import com.faderw.venus.request.Request;

/**
 * Created by FaderW on 2018/8/30
 */

public interface Downloader {

    Page download(Request request);
}
