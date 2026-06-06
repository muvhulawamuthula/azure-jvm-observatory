package com.muvhulawa.observatory.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JvmHealthWatcherTest {

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

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

    @Test
    void formatsPercentWithTwoDecimals() {
        Locale.setDefault(Locale.US);
        assertEquals("2.33", JvmHealthWatcher.formatPercent(2.333));
    }

    @Test
    void formatsPercentWithDotEvenInCommaLocale() {
        // Germany uses a comma as the decimal separator; the log value must
        // still use a dot so it stays parseable regardless of host locale.
        Locale.setDefault(Locale.GERMANY);
        assertEquals("2.33", JvmHealthWatcher.formatPercent(2.333));
    }
}
