package xyz.shirokuro.scriptblockimproved.common;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import lombok.NonNull;

public final class Debouncer<V> {

    private static final AtomicInteger THREAD_COUNT = new AtomicInteger();
    private final Lock lock = new ReentrantLock();
    @NonNull
    private final ScheduledExecutorService executor;
    private final Duration delay;
    private final Consumer<V> task;
    private volatile boolean shutdown = false;

    private ScheduledFuture<?> scheduled;

    public Debouncer(@NonNull final Duration delay, @NonNull final Consumer<V> task) {
        this.task = task;
        this.delay = delay;
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            return new Thread(r, "sbi-debouncer-" + THREAD_COUNT.incrementAndGet());
        });
    }

    /**
     * This method queues a task, which is specified in the constructor, into ScheduledExecutorService.
     * If previous task is already queued, it will be cancelled.
     */
    public void runLater(final V value) {
        if (shutdown) throw new IllegalStateException("This debouncer is unavailable!");
        lock.lock();
        try {
            if (scheduled != null) scheduled.cancel(false);
            scheduled = executor.schedule(() -> {
                task.accept(value);
            }, delay.toNanos(), TimeUnit.NANOSECONDS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method shuts down this debouncer.
     * This debouncer cannot be used in the future.
     */
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
