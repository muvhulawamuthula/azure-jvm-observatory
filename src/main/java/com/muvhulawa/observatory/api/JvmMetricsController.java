package com.muvhulawa.observatory.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JvmMetricsController {

    @GetMapping("/api/metrics/jvm")
    public Map<String, Object> jvmMetrics() {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        Runtime runtime = Runtime.getRuntime();

        Map<String, Object> data = new HashMap<>();

        data.put("availableProcessors", runtime.availableProcessors());
        data.put("heapUsedMB", memory.getHeapMemoryUsage().getUsed() / 1024 / 1024);
        data.put("heapMaxMB", memory.getHeapMemoryUsage().getMax() / 1024 / 1024);
        data.put("nonHeapUsedMB", memory.getNonHeapMemoryUsage().getUsed() / 1024 / 1024);

        data.put("liveThreads", threads.getThreadCount());
        data.put("daemonThreads", threads.getDaemonThreadCount());
        data.put("peakThreads", threads.getPeakThreadCount());

        data.put("gc", ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .map(gc -> Map.of(
                        "name", gc.getName(),
                        "collections", gc.getCollectionCount(),
                        "timeMs", gc.getCollectionTime()
                ))
                .toList());

        return data;
    }
}
