package com.github.kuro46.scriptblockimproved.common;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
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
    private final ScheduledExecutorService executor;
    private final Duration delay;
    private volatile boolean shutdown = false;

    private ScheduledFuture<?> scheduled;

    public Debouncer(@NonNull final Duration delay) {
        this.delay = delay;
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            return new Thread(r, "sbi-debouncer-" + THREAD_COUNT.incrementAndGet());
        });
    }

    public void runLater(final Runnable task) {
        if (shutdown) throw new IllegalStateException("This debouncer is shut down!");
        lock.lock();
        try {
            if (scheduled != null) scheduled.cancel(false);
            scheduled = executor.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS);
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        shutdown = true;
        try {
            if (scheduled == null || scheduled.isDone()) return;
            scheduled.get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Cannot terminate ScheduledFuture", e);
        } finally {
            executor.shutdown();
        }
    }
}
