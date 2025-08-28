package com.dreamfirestudios.dreamcore.DreamConcurrent;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-key token-bucket limiter (burst + steady rate).
 *
 * <p>Construct with "permits per period" (capacity == burst, refill rate == permits/period).
 * Example: 5 actions per 10s with small bursts:
 * <pre>{@code
 * KeyedRateLimiter<UUID> limiter = new KeyedRateLimiter<>(5, Duration.ofSeconds(10));
 * if (!limiter.tryAcquire(p.getUniqueId())) {
 *     Duration wait = limiter.estimateWait(p.getUniqueId(), 1);
 *     // tell player to wait `wait`
 * }
 * }</pre>
 *
 * <p>Thread-safety: per-bucket synchronization; scalable for many keys.</p>
 */
public final class DreamKeyedRateLimiter<K> {

    private final double capacity;
    private final double refillPerSecond; // tokens per second
    private final Map<K, Bucket> buckets = new ConcurrentHashMap<>();

    /** permits per {@code period}. */
    public DreamKeyedRateLimiter(int permits, Duration period) {
        if (permits <= 0) throw new IllegalArgumentException("permits must be > 0");
        long nanos = Objects.requireNonNull(period, "period").toNanos();
        if (nanos <= 0) throw new IllegalArgumentException("period must be > 0");
        this.capacity = permits;
        this.refillPerSecond = permits / (nanos / 1_000_000_000.0);
    }

    /** Tries to consume 1 permit. */
    public boolean tryAcquire(K key) { return tryAcquire(key, 1); }

    /** Tries to consume {@code permits} permits. */
    public boolean tryAcquire(K key, int permits) {
        if (permits <= 0) throw new IllegalArgumentException("permits must be > 0");
        final long now = System.nanoTime();
        final Bucket b = buckets.computeIfAbsent(key, k -> Bucket.fresh(now, capacity));

        synchronized (b) {
            refill(b, now);
            if (b.tokens >= permits) {
                b.tokens -= permits;
                return true;
            }
            return false;
        }
    }

    /** Estimates how long until {@code permits} can be acquired (zero if already possible). */
    public Duration estimateWait(K key, int permits) {
        if (permits <= 0) return Duration.ZERO;
        final long now = System.nanoTime();
        final Bucket b = buckets.computeIfAbsent(key, k -> Bucket.fresh(now, capacity));

        synchronized (b) {
            refill(b, now);
            if (b.tokens >= permits) return Duration.ZERO;
            final double deficit = permits - b.tokens; // tokens needed
            final double seconds = deficit / refillPerSecond;
            long millis = (long)Math.ceil(seconds * 1000.0);
            return Duration.ofMillis(Math.max(0L, millis));
        }
    }

    /** Clears a key's bucket. */
    public void reset(K key) { buckets.remove(key); }

    /** Optional: size of the internal map. */
    public int size() { return buckets.size(); }

    private void refill(Bucket b, long now) {
        final long dtNanos = now - b.lastRefillNanos;
        if (dtNanos <= 0) return;
        final double add = (dtNanos / 1_000_000_000.0) * refillPerSecond;
        b.tokens = Math.min(capacity, b.tokens + add);
        b.lastRefillNanos = now;
    }

    private static final class Bucket {
        volatile double tokens;
        volatile long lastRefillNanos;

        static Bucket fresh(long now, double capacity) {
            Bucket b = new Bucket();
            b.tokens = capacity;            // start full (burst allowed)
            b.lastRefillNanos = now;
            return b;
        }
    }
}