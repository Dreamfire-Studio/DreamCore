package com.dreamfirestudios.dreamcore.DreamConcurrent;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A light-weight per-key cooldown map.
 *
 * <p>Usage:
 * <pre>{@code
 * CooldownMap<UUID> cds = new CooldownMap<>();
 * if (!cds.tryAcquire(p.getUniqueId(), Duration.ofSeconds(3))) {
 *     Duration left = cds.remaining(p.getUniqueId());
 *     // tell player to wait `left`
 *     return;
 * }
 * // proceed
 * }</pre>
 *
 * <p>Thread-safety: Concurrent and lock-free for common ops.</p>
 */
public final class DreamCooldownMap<K> {
    private final ConcurrentHashMap<K, Long> expiresAt = new ConcurrentHashMap<>();
    private final Clock clock;

    /** Uses system UTC clock. */
    public DreamCooldownMap() { this(Clock.systemUTC()); }

    /** For tests or custom time sources. */
    public DreamCooldownMap(Clock clock) { this.clock = Objects.requireNonNull(clock, "clock"); }

    /** Starts/renews a cooldown for {@code key}. */
    public void set(K key, Duration duration) {
        expiresAt.put(Objects.requireNonNull(key), nowMillis() + Math.max(0L, duration.toMillis()));
    }

    /**
     * Attempts to acquire. If the key is not cooling down, starts a new cooldown and returns true.
     * Otherwise returns false.
     */
    public boolean tryAcquire(K key, Duration duration) {
        final long now = nowMillis();
        final long until = expiresAt.getOrDefault(key, 0L);
        if (until > now) return false;
        expiresAt.put(key, now + Math.max(0L, duration.toMillis()));
        return true;
    }

    /** Returns remaining time (zero if ready). */
    public Duration remaining(K key) {
        final long now = nowMillis();
        final long until = expiresAt.getOrDefault(key, 0L);
        return until <= now ? Duration.ZERO : Duration.ofMillis(until - now);
    }

    /** Clears a specific cooldown. */
    public void clear(K key) { expiresAt.remove(key); }

    /** Removes all expired entries (optional maintenance). */
    public void clearExpired() {
        final long now = nowMillis();
        expiresAt.entrySet().removeIf(e -> e.getValue() <= now);
    }

    /** True if the key is currently cooling down. */
    public boolean isOnCooldown(K key) { return remaining(key).isPositive(); }

    private long nowMillis() { return clock.millis(); }
}