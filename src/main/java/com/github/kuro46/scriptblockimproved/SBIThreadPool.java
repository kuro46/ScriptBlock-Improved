package com.github.kuro46.scriptblockimproved;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;

public final class SBIThreadPool {

    private static final AtomicInteger THREAD_COUNT = new AtomicInteger();
    private static final ExecutorService EXECUTOR_SERVICE =
        Executors.newCachedThreadPool(r -> new Thread(r, "sbi-thread-pool-" + THREAD_COUNT.incrementAndGet()));

    public static void execute(@NonNull final Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }
}
