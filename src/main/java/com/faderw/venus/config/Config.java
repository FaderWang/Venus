package com.faderw.venus.config;

/**
 * @author FaderW
 */
public class Config implements Cloneable{

    /**
     * 读取超时时间
     */
    private int timeout = 10000;

    /**
     * 下载间隔
     */
    private int delay = 1000;

    /**
     * 同时下载的最大线程数
     */
    private int parallelThreads = Runtime.getRuntime().availableProcessors();

    /**
     * userAgent
     */
    private String userAgent = UserAgent.CHROME_FOR_MAC;

    /**
     * 队列深度
     */
    private int queueSize;

    public static Config me() {
        return new Config();
    }

    public Config timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int timeout() {
        return this.timeout;
    }

    public Config Delay(int delay) {
        this.delay = delay;
        return this;
    }

    public int Delay() {
        return this.delay;
    }

    public Config parallelThreads(int parallelThreads) {
        this.parallelThreads = parallelThreads;
        return this;
    }

    public int parallelThreads() {
        return this.parallelThreads;
    }

    public Config userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String userAgent() {
        return this.userAgent;
    }

    public Config queueSize(int size) {
        this.queueSize = size;
        return this;
    }

    public int queueSize() {
        return this.queueSize;
    }

    @Override
    public Config clone() {
        try {
			return (Config) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
        }
        
        return null;
    }
}