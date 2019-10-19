package com.github.kuro46.scriptblockimproved.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebouncerTests {

    @Test
    void runLaterTest() {
        final AtomicInteger executionCount = new AtomicInteger();
        final Debouncer debouncer =
            new Debouncer(executionCount::incrementAndGet, 100, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 3; i++) {
            debouncer.runLater();
        }
        debouncer.shutdown();
        assertTrue(executionCount.get() == 1);
    }
}
