package com.faderw.venus.pipeline;

import com.faderw.venus.request.Request;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author FaderW
 * 2018/12/21
 */
@Slf4j
public class FilePipline implements Pipeline<InputStream>{

    private static final int BUFFER_SIZE = 1024;
    private final String fileName;

    public FilePipline(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void process(InputStream item, Request<?> Request) {
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        if (item != null) {
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                bufferedInputStream = new BufferedInputStream(item);
                byte[] buf = new byte[BUFFER_SIZE];
                int length;
                while ((length = bufferedInputStream.read(buf)) != -1) {
                    bufferedOutputStream.write(buf, 0, length);
                }
                bufferedOutputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != bufferedInputStream) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != bufferedOutputStream) {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            log.warn("[FilePipline] inputStream is null");
        }
    }
}
