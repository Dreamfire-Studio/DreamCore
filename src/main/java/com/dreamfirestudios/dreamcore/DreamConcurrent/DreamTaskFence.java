package com.dreamfirestudios.dreamcore.DreamConcurrent;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Ensures only ONE in-flight task per key. If another caller arrives while
 * the task is running, they receive the same {@link CompletableFuture}.
 *
 * <p>Great for "reload", "expensive fetch", or "serialize-once" actions.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * TaskFence<UUID, Void> fence = new TaskFence<>();
 * fence.runOnce(p.getUniqueId(), () ->
 *     CompletableFuture.runAsync(() -> doWork())
 * ).thenRun(() -> done());
 * }</pre>
 *
 * <p>After completion, the key is automatically released.</p>
 */
public final class DreamTaskFence<K, V> {
    private final Map<K, CompletableFuture<V>> inflight = new ConcurrentHashMap<>();

    /**
     * Run the supplier if nothing is running for {@code key}; otherwise return the existing future.
     * The supplier must return a non-null future.
     */
    public CompletableFuture<V> runOnce(K key, Supplier<CompletableFuture<V>> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return inflight.compute(key, (k, existing) -> {
            if (existing != null && !existing.isDone()) return existing;
            CompletableFuture<V> f = Objects.requireNonNull(supplier.get(), "supplier.get()");
            f.whenComplete((v, t) -> inflight.remove(k, f));
            return f;
        });
    }

    /** True if there is a task running for key. */
    public boolean isRunning(K key) {
        CompletableFuture<V> f = inflight.get(key);
        return f != null && !f.isDone();
    }

    /** Cancels and removes the in-flight task (if present). */
    public void cancel(K key, boolean mayInterruptIfRunning) {
        CompletableFuture<V> f = inflight.remove(key);
        if (f != null) f.cancel(mayInterruptIfRunning);
    }

    /** Clears all in-flight references (does not cancel). */
    public void clearAll() { inflight.clear(); }
}