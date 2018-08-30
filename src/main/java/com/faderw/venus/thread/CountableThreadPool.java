package com.faderw.venus.thread;

import lombok.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by FaderW on 2018/8/30
 */
@Data
public class CountableThreadPool {

    private int threadNum;
    private AtomicInteger threadAlive = new AtomicInteger();
    private ReentrantLock reentrantLock;
    private Condition condition;
    private ExecutorService executorService;

    public CountableThreadPool (int threadNum, ExecutorService executorService) {
        reentrantLock = new ReentrantLock();
        condition = reentrantLock.newCondition();
        this.executorService = executorService;
    }


    public void execute(final Runnable runnable) {
        if (threadAlive.get() > threadNum) {
            try {
                reentrantLock.lock();
                while (threadAlive.get() > threadNum) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(() -> {
            try {
                runnable.run();
            } finally {
                try {
                    reentrantLock.lock();
                    threadAlive.decrementAndGet();
                    condition.signal();
                } finally {
                    reentrantLock.unlock();
                }

            }

        });

    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public void shutdown() {
        this.executorService.shutdown();
    }


}
