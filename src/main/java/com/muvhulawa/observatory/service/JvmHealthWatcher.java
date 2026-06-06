package com.muvhulawa.observatory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Locale;

@Component
public class JvmHealthWatcher {

    private static final Logger log = LoggerFactory.getLogger(JvmHealthWatcher.class);

    private final double heapAlertThresholdPercent;
    private final int threadAlertThreshold;

    public JvmHealthWatcher(
            @Value("${observatory.health.heap-alert-threshold-percent:80}") double heapAlertThresholdPercent,
            @Value("${observatory.health.thread-alert-threshold:200}") int threadAlertThreshold) {
        this.heapAlertThresholdPercent = heapAlertThresholdPercent;
        this.threadAlertThreshold = threadAlertThreshold;
    }

    @Scheduled(fixedRate = 15000)
    public void watchJvm() {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();

        long used = memory.getHeapMemoryUsage().getUsed();
        long max = memory.getHeapMemoryUsage().getMax();

        double usage = heapUsagePercent(used, max);
        int threadCount = threads.getThreadCount();

        if (usage > heapAlertThresholdPercent) {
            log.warn("⚠ JVM ALERT: Heap pressure critical: {}%", formatPercent(usage));
        }

        if (threadCount > threadAlertThreshold) {
            log.warn("⚠ JVM ALERT: Thread spike detected: {}", threadCount);
        }

        log.info("JVM heartbeat | heap={}%, threads={}", formatPercent(usage), threadCount);
    }

    /**
     * Formats a percentage with two decimals using a fixed locale, so the
     * decimal separator is always a dot (e.g. {@code 2.33}) regardless of the
     * host's default locale. This keeps the logged values reliably parseable.
     */
    static String formatPercent(double usage) {
        return String.format(Locale.ROOT, "%.2f", usage);
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
