# DreamTimer — Beginner-Friendly Guide (Bukkit/Spigot/Paper)

> **What is this?**
> `DreamTimer` is a lightweight countdown timer utility that you can drop into any plugin. It runs on the main thread, offers pause/resume/cancel controls, and emits Bukkit events so the rest of your plugin can react easily.

---

## Quick Start

```java
DreamTimer timer = new DreamTimer(
    plugin,
    10,      // start at 10 seconds
    20L,     // 20 ticks = 1 second
    secs -> player.sendActionBar(Component.text("⏳ " + DreamTimer.formatHMS(secs))),
    (secs, paused) -> player.sendMessage(paused ? "⏸ Paused" : "▶ Resumed"),
    secs -> player.sendMessage("✅ Finished!")
);

timer.start();
```

**What happens:**

* Fires **TimerStartedEvent** immediately.
* Every period, fires **TimerTickEvent** (pre-decrement) and calls your `onTick`.
* When it reaches zero (or you cancel), calls `onFinish` and fires **TimerFinishedEvent**.
* `pause()`/`resume()` toggle countdown and fire **TimerPausedEvent**/**TimerResumedEvent**.

---

## Class: `DreamTimer`

### Constructor

```java
DreamTimer(
  Plugin plugin,
  int startingSeconds,
  long periodTicks,
  Consumer<Integer> onTick,
  BiConsumer<Integer, Boolean> onPause,
  Consumer<Integer> onFinish
)
```

**Parameters**

* `plugin`: your owning plugin instance. Must be non-null.
* `startingSeconds`: initial value. Negative is clamped to `0`.
* `periodTicks`: schedule period in ticks (≥1). Typical: `20L` for 1s cadence.
* `onTick`: receives **remaining seconds** before decrement.
* `onPause`: invoked on **pause/resume** with `(remaining, paused)`.
* `onFinish`: called once on completion or cancel; receives final remaining (clamped to `0`).

**Threading**: all callbacks are on the **main server thread**.

---

### `start()`

Starts the timer. No-op if already started.

**Lifecycle**

1. Immediately fires **TimerStartedEvent** with starting seconds.
2. Every `periodTicks`, calls `onTick(remaining)` and fires **TimerTickEvent(remaining)\`**.
3. Decrements remaining; if `<= 0`, completes and fires **TimerFinishedEvent**.

**Example**

```java
timer.start();
```

---

### `pause()`

Pauses if running.

* Calls `onPause(remaining, true)`.
* Fires **TimerPausedEvent**.
* Subsequent `pause()` calls while already paused are ignored.

**Example**

```java
if (timer.isRunning()) {
    timer.pause();
}
```

---

### `resume()`

Resumes if paused.

* Calls `onPause(remaining, false)`.
* Fires **TimerResumedEvent**.

**Example**

```java
if (timer.isPaused()) {
    timer.resume();
}
```

---

### `cancel()`

Cancels early and triggers the same completion pathway as reaching zero.

* Stops scheduling, calls `onFinish(finalRemaining)` and fires **TimerFinishedEvent**.

**Example**

```java
// Cancel the round timer when the game ends early
if (!timer.isFinished()) {
    timer.cancel();
}
```

---

### Accessors

* `int getRemainingSeconds()` → non-negative remaining seconds.
* `boolean isRunning()` → started, not paused, not finished.
* `boolean isPaused()` → currently paused.
* `boolean isFinished()` → completed or cancelled.

**Example**

```java
if (timer.isRunning()) {
    int secs = timer.getRemainingSeconds();
    bossBar.setProgress(Math.max(0f, Math.min(1f, secs / 60f)));
}
```

---

### Utility: `formatHMS(int seconds)`

Formats `seconds` as `mm:ss`.

```java
String label = DreamTimer.formatHMS(187); // "03:07"
```

---

## Events

Each event extends Bukkit `Event` and provides `getHandlerList()` / `getHandlers()` as usual.

> All events are fired on the main thread.

### `TimerStartedEvent`

* **When**: right after `start()`.
* **Fields**: `DreamTimer timer`, `int startingSeconds`.

```java
@EventHandler
public void onTimerStart(TimerStartedEvent e) {
    getLogger().info("Timer started at " + e.getStartingSeconds() + "s");
}
```

### `TimerTickEvent`

* **When**: every tick period while running, before decrement.
* **Fields**: `DreamTimer timer`, `int remainingSeconds`.

```java
@EventHandler
public void onTimerTick(TimerTickEvent e) {
    // Update UI elements, action bar, bossbar, etc.
}
```

### `TimerPausedEvent`

* **When**: on `pause()`.
* **Fields**: `DreamTimer timer`, `int remainingSeconds`.

```java
@EventHandler
public void onTimerPaused(TimerPausedEvent e) {
    getLogger().info("Timer paused with " + e.getRemainingSeconds() + "s left");
}
```

### `TimerResumedEvent`

* **When**: on `resume()`.
* **Fields**: `DreamTimer timer`, `int remainingSeconds`.

```java
@EventHandler
public void onTimerResumed(TimerResumedEvent e) {
    // Re-enable moving UI, sounds, etc.
}
```

### `TimerFinishedEvent`

* **When**: when reaching zero or on `cancel()`.
* **Fields**: `DreamTimer timer`, `int finalRemainingSeconds` (clamped to 0).

```java
@EventHandler
public void onTimerFinished(TimerFinishedEvent e) {
    gameManager.endRound();
}
```

---

## Common Patterns

### A. Cooldowns per Player

```java
private final Map<UUID, DreamTimer> cooldowns = new HashMap<>();

public void startCooldown(Player p, int seconds) {
    DreamTimer old = cooldowns.remove(p.getUniqueId());
    if (old != null && !old.isFinished()) old.cancel();

    DreamTimer cd = new DreamTimer(plugin, seconds, 20L,
        remain -> p.sendActionBar(Component.text("Cooldown: " + DreamTimer.formatHMS(remain))),
        (remain, paused) -> {},
        r -> p.sendMessage("You can use that again!")
    );
    cooldowns.put(p.getUniqueId(), cd);
    cd.start();
}
```

### B. Game Round Timer with BossBar

```java
BossBar bar = Bukkit.createBossBar(Component.text("Round"), BarColor.BLUE, BarStyle.SEGMENTED_10);
bar.addPlayer(player);
int total = 120;
DreamTimer round = new DreamTimer(plugin, total, 20L,
    remain -> bar.setProgress(Math.max(0.0, Math.min(1.0, (double) remain / total))),
    (remain, paused) -> bar.setTitle(paused ? Component.text("Round (Paused)") : Component.text("Round")),
    r -> {
        bar.setProgress(0);
        bar.removeAll();
        gameManager.endRound();
    }
);
round.start();
```

### C. Pausing During Server Events

```java
@EventHandler
public void onWorldSave(WorldSaveEvent e) {
    if (roundTimer != null && roundTimer.isRunning()) {
        roundTimer.pause();
    }
}
```

---

## Error Handling & Edge Cases

* `start()` is idempotent; calling it twice does nothing once scheduled.
* `periodTicks` is clamped to `≥ 1` to avoid invalid scheduling.
* `startingSeconds` is clamped to `≥ 0`.
* `cancel()` converges to the same completion path as natural finish.
* Callbacks are defensively defaulted to no-ops when `null`.

---

## Testing Tips

* Use a shorter period (e.g., `2L`) in tests to simulate rapid ticks.
* Spy on events using a test listener to assert ordering: Started → Tick\* → Finished.
* Verify that `onTick` receives **pre-decrement** values.

---

## API Reference (Quick)

* `start()` → begin countdown; emits `TimerStartedEvent`.
* `pause()` / `resume()` → toggles; emits `TimerPausedEvent` / `TimerResumedEvent`.
* `cancel()` → stop early; emits `TimerFinishedEvent`.
* `getRemainingSeconds()` / `isRunning()` / `isPaused()` / `isFinished()`.
* `formatHMS(int seconds)` → `mm:ss` formatter.

---

## Suggestions / Future Enhancements

* **Count-Up Mode**: optional mode that counts upward with a max duration.
* **Jitter-Resilient Ticks**: optionally catch-up or compensate when the server lags.
* **Persistence**: serialize timers across restarts (e.g., store end timestamp, resume on enable).
* **Composite Timers**: chain timers (warmup → round → cooldown) with a fluent API.
* **Listener Interface**: provide a `TimerListener` to bundle callbacks instead of separate lambdas.
* **Builder Pattern**: `DreamTimer.builder(plugin).starting(60).period(20L)...build()` for readability.
* **Named Timers** & **Registry**: registry service for querying and managing timers globally.
* **Error Hooks**: on-exception consumer around callbacks to ensure plugin stability.
* **Scriptable Objects** (if ported to non-Java contexts): configurable timer presets.