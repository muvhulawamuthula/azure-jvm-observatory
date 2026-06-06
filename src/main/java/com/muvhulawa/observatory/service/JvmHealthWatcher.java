package com.muvhulawa.observatory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

@Component
public class JvmHealthWatcher {

    private static final Logger log = LoggerFactory.getLogger(JvmHealthWatcher.class);

    @Scheduled(fixedRate = 15000)
    public void watchJvm() {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();

        long used = memory.getHeapMemoryUsage().getUsed();
        long max = memory.getHeapMemoryUsage().getMax();

        double usage = heapUsagePercent(used, max);
        int threadCount = threads.getThreadCount();

        if (usage > 80) {
            log.warn("⚠ JVM ALERT: Heap pressure critical: {}%", String.format("%.2f", usage));
        }

        if (threadCount > 200) {
            log.warn("⚠ JVM ALERT: Thread spike detected: {}", threadCount);
        }

        log.info("JVM heartbeat | heap={}%, threads={}",
                String.format("%.2f", usage),
                threadCount);
    }

    /**
     * Computes heap usage as a percentage of the configured maximum.
     *
     * <p>{@link java.lang.management.MemoryUsage#getMax()} returns -1 when no
     * heap maximum is defined. Guarding against a non-positive max keeps the
     * calculation from producing a negative percentage that would silently
     * disable heap-pressure detection.
     *
     * @return the usage percentage, or -1 when no heap maximum is defined
     */
    static double heapUsagePercent(long used, long max) {
        return max > 0 ? ((double) used / max) * 100 : -1;
    }
}
