<!--
MIT License
Copyright (c) 2025 Dreamfire Studio

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
-->

# DreamConcurrent Core – Guide & Usage

This document explains the **concurrency utilities** in `com.dreamfirestudios.dreamcore.DreamConcurrent` and how to use them safely in Bukkit/Spigot/Paper plugins and other Java projects.

* [`DreamCooldownMap<K>`](#dreamcooldownmapk) – **simple per-key cooldowns** (e.g., action throttles per player).
* [`DreamKeyedRateLimiter<K>`](#dreamkeyedratelimiterk) – **token-bucket rate limiting** per key (burst + steady rate).
* [`DreamTaskFence<K,V>`](#dreamtaskfencekv) – **serialize one in-flight task per key** and share the same future.

> **Design goals:** lock-free for common paths, minimal allocations, testable, and predictable behavior under contention.

---

## `DreamCooldownMap<K>`

A lightweight per-key cooldown store that tracks an **absolute expiry timestamp**.

### When to use

* Command spam prevention (e.g., `/spawn` no more than once every 5s per player).
* Feature toggles that need a short **quiet period** after use.

### Key API

```java
DreamCooldownMap<UUID> cds = new DreamCooldownMap<>();
boolean ok = cds.tryAcquire(player.getUniqueId(), Duration.ofSeconds(3));
if (!ok) {
    Duration left = cds.remaining(player.getUniqueId());
    player.sendMessage("Please wait " + left.toMillis() + "ms");
}
```

* `set(key, duration)`: start/renew a cooldown.
* `tryAcquire(key, duration)`: start a cooldown **only if** not currently cooling down.
* `remaining(key)`: time left, or `Duration.ZERO` if ready.
* `isOnCooldown(key)`: convenience predicate.
* `clear(key)`, `clearExpired()`: maintenance.

### Thread-safety & perf

* Backed by `ConcurrentHashMap`.
* Uses `Clock` (injectable) for testability.
* `tryAcquire` performs a **get + put**; under high contention last write wins (acceptable for cooldown semantics).

### Pitfalls & tips

* Use **UTC clock** only (default) for consistency.
* Pass **non-negative durations**; negatives are treated as zero.
* For long-running maps, call `clearExpired()` during low-traffic windows to prune entries.

---

## `DreamKeyedRateLimiter<K>`

Per-key **token bucket**: supports **bursts up to capacity** then refills at a fixed rate.

### When to use

* Smooth out noisy actions: e.g., **5 actions per 10s** per player.
* Network/API call shapers.

### Constructing

```java
// Allow up to 5 actions every 10 seconds per player (burst up to 5)
DreamKeyedRateLimiter<UUID> limiter = new DreamKeyedRateLimiter<>(5, Duration.ofSeconds(10));
```

### Consuming permits

```java
if (!limiter.tryAcquire(player.getUniqueId())) {
    Duration wait = limiter.estimateWait(player.getUniqueId(), 1);
    player.sendMessage("Slow down. Try again in ~" + wait.toMillis() + "ms");
    return;
}
// proceed
```

* `tryAcquire(key)` / `tryAcquire(key, permits)`.
* `estimateWait(key, permits)`: time until acquisition would succeed, given current bucket state.
* `reset(key)`, `size()`.

### Internals & threading

* Each key maps to a **Bucket** (`double tokens`, `lastRefillNanos`).
* Uses `System.nanoTime()` for monotonic timing.
* **Per-bucket synchronization** (`synchronized (b)`) isolates contention between keys; no global lock.

### Tuning & gotchas

* `permits` and `period` define both **burst capacity** and **refill rate**: `refillPerSecond = permits / periodSeconds`.
* Buckets start **full** to allow initial burst.
* Always request **positive** `permits`.

---

## `DreamTaskFence<K,V>`

Guarantees **only one in-flight task** per key. Later callers reuse the **same `CompletableFuture`** until it completes.

### When to use

* Expensive computations you don’t want duplicated (e.g., **config reload**, **cache warm**, **remote fetch**).
* Queueing “do once” actions from multiple triggers.

### Example

```java
DreamTaskFence<String, Void> fence = new DreamTaskFence<>();
CompletableFuture<Void> f = fence.runOnce("reload", () ->
    CompletableFuture.runAsync(this::reloadAll)
);

f.whenComplete((v, t) -> {
    if (t != null) getLogger().warning("Reload failed: " + t.getMessage());
});
```

### API behaviors

* `runOnce(key, supplier)`:

    * If an unfinished future exists → **return it**.
    * Else call `supplier.get()` (must return non-null future), store it, and **remove on completion**.
* `isRunning(key)`: active future check.
* `cancel(key, mayInterrupt)`: attempts to cancel and evict the in-flight future.
* `clearAll()`: drops references without cancellation.

### Concurrency notes

* Uses `ConcurrentHashMap.compute` to establish/return a **single shared future** per key.
* Removes mapping **only** for the same future instance (safe if callers replace futures elsewhere).

---

## Integration in Bukkit/Paper

### Main-thread vs async

* **Never block the main thread**. Combine these utilities with async tasks (Paper’s async scheduler or `CompletableFuture`).
* For player messaging after an async check, use `Bukkit.getScheduler().runTask(plugin, () -> ...)` to hop back.

### Typical patterns

```java
// Cooldown + async execution
if (!cds.tryAcquire(uuid, Duration.ofSeconds(3))) {
    player.sendMessage("Wait a bit...");
    return true;
}
CompletableFuture.runAsync(() -> doWork(uuid))
        .thenRun(() -> Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage("Done")));
```

---

## Testing strategies

* **Deterministic time**: construct `DreamCooldownMap` with a custom `Clock`.
* For `DreamKeyedRateLimiter`, prefer **nano-time abstraction** in tests (or test with coarse waits around refill math).
* Use `CompletableFuture` timeouts in unit tests for `DreamTaskFence` to avoid hangs.

---

## Extension ideas (fit with Dreamfire style)

* **Interfaces & helpers**

    * `ICooldownStore<K>` exposing `tryAcquire`, `remaining`, `set`, `clear` for easier mocking.
    * `IRateLimiter<K>` exposing `tryAcquire`, `estimateWait`, `reset`.
* **Enums & events**

    * Define `CooldownKey` enum for well-known throttles (e.g., `TELEPORT`, `HOME`, `REPAIR`).
    * Fire `CooldownExpiredEvent` if you need listeners when a key becomes ready (via scheduled sweep).
* **Scriptable thresholds** (via config): map enum → `permits`/`period` for rate limiter and default durations for cooldowns.
* **Metrics hooks**: counters for rejects/accepts, average wait.

---

## Anti-patterns to avoid

* **Sleeping on main thread** to wait out cooldown/rate limits.
* Creating **short-lived limiter instances** per call; reuse per subsystem.
* Swallowing exceptions from tasks fenced by `DreamTaskFence`—always add a `.whenComplete` to observe failures.

---

## Quick reference

### Cooldown vs RateLimiter vs TaskFence

| Concern                       | Use                     | Pros                              | Cons                            |
| ----------------------------- | ----------------------- | --------------------------------- | ------------------------------- |
| Prevent rapid repeats         | `DreamCooldownMap`      | Minimal state; clear UX           | Binary allow/deny; no smoothing |
| Smooth throughput with bursts | `DreamKeyedRateLimiter` | Natural bursts; steady refill     | Slightly more complex math      |
| Ensure single execution       | `DreamTaskFence`        | No duplicate work; shared results | Requires Future-based APIs      |

---

## Checklist for production use

* [ ] Decide per-key type (e.g., `UUID` for players).
* [ ] Centralize instances in a **service class**; inject where needed.
* [ ] Add **configurable** durations/permits via your plugin config.
* [ ] Add **logging/metrics** for rejects and long waits.
* [ ] Unit tests for edge cases: zero duration, huge duration, overflow, cancel paths.

---

<small>© 2025 Dreamfire Studio – DreamfireV2. This document accompanies the core utilities and follows the project’s documentation standards.</small>