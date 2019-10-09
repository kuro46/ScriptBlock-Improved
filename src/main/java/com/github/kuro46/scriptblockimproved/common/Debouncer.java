package com.github.kuro46.scriptblockimproved.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NonNull;

public final class Debouncer {

    private static final AtomicInteger THREAD_COUNT = new AtomicInteger();
    private final Lock lock = new ReentrantLock();
    @NonNull
    private final Runnable task;
    @NonNull
    private final ScheduledExecutorService executor;
    @NonNull
    private final TimeUnit delayUnit;
    private final long delay;

    private ScheduledFuture<?> scheduled;

    public Debouncer(
            @NonNull final Runnable task,
            final long delay,
            @NonNull final TimeUnit delayUnit) {
        this.task = task;
        this.delay = delay;
        this.delayUnit = delayUnit;
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            return new Thread(r, "sbi-debouncer-" + THREAD_COUNT.incrementAndGet());
        });
    }

    public void runLater() {
        lock.lock();
        try {
            if (scheduled != null) scheduled.cancel(false);
            scheduled = executor.schedule(task, delay, delayUnit);
        } finally {
            lock.unlock();
        }
    }
}
