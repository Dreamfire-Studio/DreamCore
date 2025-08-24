# DreamActionBar — Beginner’s Guide & API

Welcome! This guide explains how to use **DreamActionBar** in DreamCore to show animated messages in the Minecraft action bar. It’s written from the perspective of a brand‑new user integrating this system for the first time.

---

## What is DreamActionBar?

`DreamActionBar` is a small stateful object that:

* Holds a **sequence of frames** (`DreamActionBarData`).
* Has a **set of viewers** (`Player`s who will see the messages).
* Advances a **current frame index** and displays that frame to all viewers when you call `displayNextFrame()` (typically on a repeating task).
* Can be **paused**, **played**, and **stopped**. Each of these actions fires cancellable **Bukkit events** so other plugins/systems can intercept behavior.

You’ll usually build an instance with `DreamActionBar.Builder`, register it inside `DreamCore.DreamActionBars`, and tick it with a scheduler.

---

## Quick Start (Copy/Paste)

```java
// 1) Create some frames (each frame can format text per-viewer)
DreamActionBarData hello = new DreamActionBarData(
    p -> "&aHello, " + p.getName() + "!",
    p -> DreamMessageSettings.all()
);
DreamActionBarData tips = new DreamActionBarData(
    p -> "&7Tip: &fUse /spawn to return home.",
    p -> DreamMessageSettings.all()
);

// 2) Build the action bar and add viewers
DreamActionBar bar = new DreamActionBar.Builder()
    .addFrame(hello, 40) // repeat this frame for ~2s at 20tps
    .addFrame(tips, 60)  // repeat this frame for ~3s
    .addViewer(player)   // anyone you want to see it
    .build(true);        // allow multiple per player (see notes)

// 3) Schedule it to advance frames
int taskId = Bukkit.getScheduler().runTaskTimer(
    myPlugin,
    () -> {
        // When displayNextFrame returns true, it means no frames or no viewers
        boolean finished = bar.displayNextFrame();
        if (finished) {
            Bukkit.getScheduler().cancelTask(taskId);
            bar.stop();
        }
    },
    0L, 1L // advance every tick (adjust for your pacing)
).getTaskId();
```

> **Tip:** If you want a slower animation, call `displayNextFrame()` less often (e.g., every 5 or 10 ticks) or add frames with more repetition.

---

## Core Concepts

### Frames (`DreamActionBarData`)

A frame encapsulates **what to show** and **how to format it**.

```java
public record DreamActionBarData(
    Function<Player, String> messageProvider,
    Function<Player, DreamMessageSettings> settingsProvider
) {
    public void displayActionBar(Player player) { /* … */ }
}
```

* `messageProvider` is evaluated **per player**. Use it to inject names, stats, or placeholders.
* `settingsProvider` gives you a `DreamMessageSettings` instance (colors, placeholders, etc.). If it returns `null`, the system uses `DreamMessageSettings.all()` by default.
* `displayActionBar(player)` resolves the message and sends it via `DreamMessageFormatter.format(...)` ➜ `player.sendActionBar(...)`.

**Examples:**

```java
// Static text
new DreamActionBarData(p -> "&eWelcome!", p -> DreamMessageSettings.all());

// Per-player dynamic value
new DreamActionBarData(
    p -> "&bCoins: &f" + getCoins(p),
    p -> DreamMessageSettings.all()
);

// Conditional style
new DreamActionBarData(
    p -> p.isOp() ? "&cAdmin Mode" : "&aPlayer Mode",
    p -> DreamMessageSettings.all()
);
```

### Viewers

`DreamActionBar` keeps an internal list of `Player` viewers.

* `addViewer(Player player, boolean multipleActionBars)` (on the instance) respects a global check (`DreamActionBarAPI.IsPlayerInActionBar(player)`) unless you explicitly allow multiples.
* `removeViewer(Player)` and `clearViewers()` manage the audience.
* `isPlayerViewing(Player)` lets you query membership.

> **Note:** The `Builder.addViewer(Player)` queues viewers for the new instance. When you call `build(...)`, the instance copies them in.

### Playback lifecycle

* `displayNextFrame()` shows the current frame to **all viewers** and advances the index. Returns `true` when there are **no frames or no viewers** (handy for stopping a scheduler).
* `pause()` ➜ fires `DreamActionBarPaused` (cancellable). When paused, `displayNextFrame()` does nothing (returns `false`).
* `play()` ➜ fires `DreamActionBarPlayed` (cancellable) and resumes.
* `stop()` ➜ fires `DreamActionBarStopped` (cancellable), **removes** the instance from `DreamCore.DreamActionBars`, and clears viewers.

---

## Builder API

```java
DreamActionBar bar = new DreamActionBar.Builder()
    .addFrame(frame, repeat)
    .addViewer(player)
    .build(true);
```

* **`addFrame(frame, repeatFrames)`**: Appends the same frame N times.

    * If `repeatFrames <= 0`, the builder ignores it.
* **`addViewer(player)`**: Queues a viewer for the new instance.
* **`build(allowMultipleActionBars)`**: Creates the `DreamActionBar`, copies queued frames & viewers, registers it into `DreamCore.DreamActionBars`, and returns it.

    * Throws `IllegalArgumentException` if you built with **no frames**.

> **Important:** The instance‑level `addViewer(player, multipleActionBars)` enforces the single/multiple policy. The builder’s `build(allowMultipleActionBars)` parameter does not currently propagate that choice during copy. If you need strict multi‑bar control at build time, add viewers **after** build using `addViewer(player, allowMultipleActionBars)`. See **Suggestions** at the end.

---

## DreamActionBar methods (How to use & when)

### `boolean isPlayerViewing(Player player)`

**Use when:** You need to check if a specific player already receives this bar.

* Throws `IllegalArgumentException` if `player == null`.
* Returns `true` if present in the internal viewers list.

**Example:**

```java
if (!bar.isPlayerViewing(p)) {
    bar.addViewer(p, true);
}
```

---

### `void addViewer(Player player, boolean multipleActionBars)`

**Use when:** You want to add a new viewer at runtime.

* If `multipleActionBars == false`, the method first checks **any** existing action bar via `DreamActionBarAPI.IsPlayerInActionBar(player)`.
* Fires **`DreamActionBarPlayerAdded`** (cancellable). If cancelled, the viewer is not added.

**Example:**

```java
bar.addViewer(p, false); // enforce at most one bar per player
```

---

### `void removeViewer(Player player)`

**Use when:** You want to stop showing the bar to someone.

* Fires **`DreamActionBarPlayerRemoved`** (cancellable). If cancelled, the viewer remains.

**Example:**

```java
bar.removeViewer(p);
```

---

### `void clearViewers()`

**Use when:** You want to purge the audience (e.g., on world change).

* Internally calls `removeViewer(...)` for each member (thus events fire), then clears.

---

### `void updateFrame(int index, DreamActionBarData data)`

**Use when:** You want to patch the frame sequence in place (e.g., swap the tip text).

* Replaces the frame at `index` if it’s within current bounds. If `index` is invalid, it does nothing.
* Throws `IllegalArgumentException` if `data == null`.

**Example:**

```java
bar.updateFrame(0, new DreamActionBarData(p -> "&6Breaking News!", p -> DreamMessageSettings.all()));
```

---

### `boolean displayNextFrame()`

**Use when:** On each tick/interval to progress the animation.

* Returns `true` if there are **no frames** or **no viewers** so your task can stop itself cleanly.
* If `paused == true`, returns `false` without sending anything (so your loop can continue without spamming).

**Pattern:**

```java
boolean finished = bar.displayNextFrame();
if (finished) { bar.stop(); }
```

---

### `void pause()` / `void play()`

**Use when:** Temporarily halt or resume animation.

* `pause()` fires `DreamActionBarPaused` (cancellable) and sets internal `paused = true`.
* `play()` fires `DreamActionBarPlayed` (cancellable) and sets `paused = false`.

**Example:**

```java
bar.pause();
// … later …
bar.play();
```

---

### `void stop()`

**Use when:** You’re done with this bar (plugin disable, scene end, etc.).

* Fires `DreamActionBarStopped` (cancellable). If **not** cancelled, the instance is removed from `DreamCore.DreamActionBars` and all viewers are cleared.

**Example:**

```java
bar.stop();
```

---

## DreamActionBarAPI

### `static boolean IsPlayerInActionBar(Player player)`

Checks whether a player is currently part of **any** active `DreamActionBar` (by scanning `DreamCore.DreamActionBars`). Useful for enforcing one‑bar‑per‑player policies.

**Example:**

```java
if (!DreamActionBarAPI.IsPlayerInActionBar(p)) {
    bar.addViewer(p, false);
}
```

---

## Events (Listen & Intercept)

Each event is a standard Bukkit `Event` and **cancellable**. They self‑dispatch via `Bukkit.getPluginManager().callEvent(this)` on the main thread.

### `DreamActionBarPaused`

* **When:** `bar.pause()` is called.
* **Cancel effect:** Prevents entering the paused state.

### `DreamActionBarPlayed`

* **When:** `bar.play()` is called.
* **Cancel effect:** Prevents resuming playback.

### `DreamActionBarPlayerAdded`

* **When:** `bar.addViewer(player, ...)` is called.
* **Cancel effect:** Prevents the player from being added.

### `DreamActionBarPlayerRemoved`

* **When:** `bar.removeViewer(player)` is called (including via `clearViewers()`).
* **Cancel effect:** Prevents the player from being removed.

### `DreamActionBarStopped`

* **When:** `bar.stop()` is called.
* **Cancel effect:** Prevents the bar from being removed from the registry and keeps viewers as-is.

**Listener Example:**

```java
@EventHandler
public void onAdd(DreamActionBarPlayerAdded e) {
    if (isInRestrictedWorld(e.getPlayer())) {
        e.setCancelled(true);
    }
}
```

---

## Scheduling Patterns

### 1) Simple every‑tick animation

```java
int id = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    if (bar.displayNextFrame()) {
        bar.stop();
        Bukkit.getScheduler().cancelTask(id);
    }
}, 0L, 1L).getTaskId();
```

### 2) Slower pace (every 10 ticks)

```java
Bukkit.getScheduler().runTaskTimer(plugin, bar::displayNextFrame, 0L, 10L);
```

### 3) Pause/Play on demand

```java
// command example
if (cmd.equalsIgnoreCase("pausebar")) bar.pause();
if (cmd.equalsIgnoreCase("playbar"))  bar.play();
```

---

## Formatting & Placeholders

`DreamActionBarData` uses `DreamMessageFormatter.format(message, player, settings)` internally. Keep messages short (action bars truncate long text). Use `&` color codes if supported by your formatter.

**Example:** `"&aWilderness &7(Chunk: &f" + chunkX + ", " + chunkZ + ")"`

---

## Common Pitfalls & How to Avoid Them

* **No frames added** ➜ `build(...)` throws. Always add at least one frame.
* **No viewers** ➜ `displayNextFrame()` will return `true` immediately. Add viewers **before** starting your scheduler or handle the return value.
* **Multiple bars per player** ➜ If you want to enforce a single bar globally, call `addViewer(player, false)` and check `DreamActionBarAPI.IsPlayerInActionBar(player)`.
* **Paused but still ticking** ➜ `displayNextFrame()` returns `false` when paused. Your scheduler keeps running unless you cancel it yourself.
* **Long/expensive providers** ➜ `messageProvider` runs **for each player**. Keep it fast and avoid blocking calls.

---

## Testing Checklist

* [ ] Frames appear to all intended players.
* [ ] Animation advances at the expected rate.
* [ ] Pause/Play works and events fire.
* [ ] Stopping removes the bar from `DreamCore.DreamActionBars`.
* [ ] Listeners can cancel additions/removals.

---

## Suggestions (for your review/approval — no behavior changed yet)

1. **Propagate multi‑bar policy in Builder:** Consider storing the `allowMultipleActionBars` flag inside the built instance or provide a builder method like `.allowMultiplePerPlayer(boolean)` so viewers added via the builder can respect the policy consistently.
2. **Guard against stale players:** Optionally filter `viewers` for `!player.isOnline()` before sending, or auto‑remove offline players (behind an event).
3. **Immutable frames list:** Expose frames via an unmodifiable view or copy to preserve invariants.
4. **Add `removeIf(Predicate<Player>)`:** Convenience for bulk removal with a single event per player.
5. **Metrics hooks:** Integrate a lightweight timer around `displayNextFrame()` for observability (ties nicely with your `MetricsTimer`).

If you want, I can draft these changes behind interfaces and events to keep compatibility and reusability.

---

## FAQ

**Q: Do I need to store a reference to the `DreamActionBar`?**
A: Yes, so you can control it (pause/play/stop) or update frames later.

**Q: What happens if there are zero viewers?**
A: `displayNextFrame()` returns `true`. You should usually stop the scheduler and/or the bar.

**Q: Can I run multiple bars for the same player?**
A: Yes, but only if you pass `multipleActionBars = true` when adding the viewer on the instance. (Builder currently doesn’t enforce this automatically.)

**Q: Where are bars stored?**
A: In `DreamCore.DreamActionBars`, keyed by the instance’s class ID (from `DreamClassID`).

---

## Reference Summary

### DreamActionBar

* `boolean isPlayerViewing(Player)`
* `void addViewer(Player, boolean multipleActionBars)`
* `void removeViewer(Player)`
* `void clearViewers()`
* `void updateFrame(int index, DreamActionBarData data)`
* `boolean displayNextFrame()`
* `void pause()` / `void play()`
* `void stop()`

### DreamActionBar.Builder

* `Builder addFrame(DreamActionBarData frame, int repeatFrames)`
* `Builder addViewer(Player player)`
* `DreamActionBar build(boolean allowMultipleActionBars)`

### DreamActionBarAPI

* `static boolean IsPlayerInActionBar(Player player)`

### Events (all `Cancellable`)

* `DreamActionBarPaused`
* `DreamActionBarPlayed`
* `DreamActionBarPlayerAdded`
* `DreamActionBarPlayerRemoved`
* `DreamActionBarStopped`

---