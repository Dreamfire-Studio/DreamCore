# DreamStopwatch — Developer Guide

The **DreamStopwatch** is a simple tick-driven stopwatch utility for Bukkit/Spigot servers. It counts **upward in seconds** (or any tick period you specify) and provides full lifecycle control with events for start, pause, resume, tick, and stop.

---

## Why Use DreamStopwatch?

* ✅ Clean abstraction over Bukkit’s scheduler.
* ✅ Built-in **pause** and **resume** support.
* ✅ Callbacks (`onTick`, `onPause`, `onStop`) for inline logic.
* ✅ Fires **custom events** (`StopwatchStartedEvent`, `StopwatchPausedEvent`, etc.) for plugin-wide listeners.
* ✅ Includes helper formatting (`mm:ss`).

---

## Function Reference

### 1. `DreamStopwatch(Plugin plugin, long periodTicks, Consumer<Integer> onTick, BiConsumer<Integer, Boolean> onPause, Consumer<Integer> onStop)`

Creates a new stopwatch instance.

**Parameters:**

* `plugin` → Your plugin instance.
* `periodTicks` → Tick interval between updates (20 = 1 second).
* `onTick` → Callback fired every period with elapsed seconds.
* `onPause` → Callback fired on pause/resume with `(elapsed, paused)`.
* `onStop` → Callback fired once when stopped, with final elapsed seconds.

**Example:**

```java
DreamStopwatch watch = new DreamStopwatch(
    plugin,
    20L,
    secs -> player.sendActionBar(Component.text("Time: " + DreamStopwatch.formatHMS(secs))),
    (secs, paused) -> player.sendMessage(paused ? "Paused" : "Resumed"),
    secs -> player.sendMessage("Final time: " + DreamStopwatch.formatHMS(secs))
);
```

---

### 2. `start()`

Starts the stopwatch. If already started, does nothing.

**Example:**

```java
watch.start();
```

This also fires a `StopwatchStartedEvent`.

---

### 3. `pause()`

Pauses the stopwatch. Ticks stop incrementing but the current time is preserved.

**Example:**

```java
watch.pause();
```

Fires a `StopwatchPausedEvent`.

---

### 4. `resume()`

Resumes a paused stopwatch.

**Example:**

```java
watch.resume();
```

Fires a `StopwatchResumedEvent`.

---

### 5. `stop()`

Stops the stopwatch completely. Cancels the scheduled task, triggers the `onStop` callback, and fires a `StopwatchStoppedEvent`.

**Example:**

```java
watch.stop();
```

---

### 6. `reset()`

Resets elapsed time back to **zero**. Does not automatically start the stopwatch.

**Example:**

```java
watch.reset();
```

---

### 7. `getElapsedSeconds()`

Returns the elapsed time in whole seconds.

```java
int time = watch.getElapsedSeconds();
```

---

### 8. `isRunning()` / `isPaused()` / `isStopped()`

State-check helpers:

* `isRunning()` → `true` if the stopwatch is active and not paused/stopped.
* `isPaused()` → `true` if currently paused.
* `isStopped()` → `true` if fully stopped.

---

### 9. `formatHMS(int seconds)`

Formats a number of seconds into `MM:SS`.

**Example:**

```java
String formatted = DreamStopwatch.formatHMS(187); // "03:07"
```

---

## Event Reference

Every lifecycle change triggers an event you can listen for in your plugin:

* **`StopwatchStartedEvent`** → Fired when stopwatch starts.
* **`StopwatchPausedEvent`** → Fired when paused.
* **`StopwatchResumedEvent`** → Fired when resumed.
* **`StopwatchTickEvent`** → Fired every tick interval with elapsed seconds.
* **`StopwatchStoppedEvent`** → Fired when stopped.

**Example Listener:**

```java
@EventHandler
public void onStopwatchTick(StopwatchTickEvent e) {
    Bukkit.broadcastMessage("Stopwatch time: " + e.getElapsedSeconds());
}
```

---

## Best Practices

* Always call `stop()` in plugin `onDisable` to prevent orphaned tasks.
* Use `pause()` and `resume()` for mid-game pauses (e.g., waiting for players).
* Use event listeners if multiple systems need to react to stopwatch updates.
* Prefer `formatHMS` for displaying times to players.

---

## Suggestions for Future Improvements

* ⏱ Add **countdown mode** (counts down to 0).
* 🔁 Add **auto-reset + restart** mode for looping timers.
* 🎯 Support **multiple time formats** (e.g., `HH:MM:SS`).
* 🧵 Thread-safe API for async access.
* 📊 Built-in **boss bar / action bar** integration for visual timers.

---