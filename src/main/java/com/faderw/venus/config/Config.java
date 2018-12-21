package com.faderw.venus.config;

/**
 * @author FaderW
 */
public class Config implements Cloneable{

    /**
     * 域名
     */
    private String domain;

    /**
     * 读取超时时间
     */
    private int timeout = 10000;

    /**
     * 下载间隔
     */
    private int delay = 10;

    /**
     * 同时下载的最大线程数
     */
    private int parallelThreads = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * 超时重试次数
     */
    private int retry = 1;

    /**
     * 重试等待时间
     */
    private int retrySleep = 1000;

    /**
     * userAgent
     */
    private String userAgent = UserAgent.CHROME_FOR_MAC;

    /**
     * 队列深度
     */
    private int queueSize = 1024;


    public static Config me() {
        return new Config();
    }

    public Config domain(String domain) {
        this.domain = domain;
        return this;
    }

    public String domain() {
        return this.domain;
    }

    public Config timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int timeout() {
        return this.timeout;
    }

    public Config delay(int delay) {
        this.delay = delay;
        return this;
    }

    public int delay() {
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

    public Config retry(int retry) {
        this.retry = retry;
        return this;
    }

    public int retry() {
        return this.retry;
    }

    public Config retrySleep(int retrySleep) {
        this.retrySleep = retrySleep;
        return this;
    }

    public int retrySleep() {
        return this.retrySleep;
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