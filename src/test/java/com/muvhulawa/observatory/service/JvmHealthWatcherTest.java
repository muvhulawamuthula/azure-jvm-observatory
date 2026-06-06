package com.muvhulawa.observatory.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JvmHealthWatcherTest {

    @Test
    void computesPercentageWhenMaxIsDefined() {
        assertEquals(50.0, JvmHealthWatcher.heapUsagePercent(512, 1024));
    }

    @Test
    void reportsFullUsageAtMax() {
        assertEquals(100.0, JvmHealthWatcher.heapUsagePercent(1024, 1024));
    }

    @Test
    void returnsSentinelWhenMaxUndefined() {
        // getMax() returns -1 when no heap maximum is configured; the guard
        // must not produce a negative percentage that suppresses the alert.
        assertEquals(-1, JvmHealthWatcher.heapUsagePercent(900, -1));
    }

    @Test
    void returnsSentinelWhenMaxIsZero() {
        assertEquals(-1, JvmHealthWatcher.heapUsagePercent(900, 0));
    }
}
