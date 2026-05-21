package com.muvhulawa.observatory.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Service
public class StressLabService {

    private final List<byte[]> memoryLeakStore = new ArrayList<>();

    public String burnCpu(int seconds) {
        long end = System.currentTimeMillis() + seconds * 1000L;

        Thread.startVirtualThread(() -> {
            while (System.currentTimeMillis() < end) {
                Math.sqrt(Math.random() * Double.MAX_VALUE);
            }
        });

        return "CPU burn started for " + seconds + " seconds";
    }

    public String leakMemory(int megabytes) {
        for (int i = 0; i < megabytes; i++) {
            memoryLeakStore.add(new byte[1024 * 1024]);
        }

        return "Leaked approximately " + megabytes + "MB. Current leak objects: " + memoryLeakStore.size();
    }

    public String clearMemoryLeak() {
        int size = memoryLeakStore.size();
        memoryLeakStore.clear();
        System.gc();

        return "Cleared " + size + "MB from leak store and requested GC";
    }

    public String createVirtualThreads(int count) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < count; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(30_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        return "Created " + count + " virtual threads";
    }

    public String blockPlatformThreads(int count) {
        for (int i = 0; i < count; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            thread.start();
        }

        return "Created " + count + " blocked platform threads";
    }
}
