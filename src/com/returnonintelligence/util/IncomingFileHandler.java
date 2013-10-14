package com.returnonintelligence.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class IncomingFileHandler extends Thread {

    private ExecutorService service;
    private int poolSize;
    private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();
    private int processedFilesCounter;

    public IncomingFileHandler(int poolSize, BlockingQueue<String> sharedQueue) {
        this.poolSize = poolSize;
        this.sharedQueue = sharedQueue;
    }

    @Override
    public void run() {
        service = Executors.newFixedThreadPool(poolSize);
        PackageProcessor p;
        String fileName;
        while (true) {
            try {
                fileName = sharedQueue.take();
                p = new PackageProcessor(fileName);
                service.submit(p);
                ++processedFilesCounter;
                System.out.println(fileName + " has been submitted to processing");
                Thread.yield();
            } catch (InterruptedException e) {
                System.out.println("File handler has been interrupted!");
                service.shutdownNow();
            }
        }
    }
}
