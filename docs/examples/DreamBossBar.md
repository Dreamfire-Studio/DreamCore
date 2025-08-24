```
MIT License

Copyright (c) 2025 Dreamfire Studio

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

# DreamBossBar — Beginner’s Guide & API

The **DreamBossBar** system lets you create animated boss bars with multiple frames, viewer tracking, and events. You can cycle through frames to show changing messages, colors, and progress, or pause/play/stop the bar as needed.

---

## What DreamBossBar Does

* Creates an **animated boss bar** that cycles through frames.
* Lets you **add/remove players** as viewers.
* Provides **events** for lifecycle changes (pause, play, stop) and player management.
* Integrates with **DreamCore** registry for persistence.

---

## Quick Start

```java
DreamBossBar bar = new DreamBossBar.Builder()
    .dreamfireBossBarData(
        new DreamBossBarData(
            BarColor.BLUE,
            BarStyle.SEGMENTED_20,
            1.0,
            (p) -> "Welcome, " + p.getName()
        ), 40 // show this frame for 40 ticks
    )
    .players(player)
    .build();

bar.play();

// In your tick loop or scheduler
taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, bar::displayNextFrame, 1L, 1L);
```

---

## Core Methods (DreamBossBar)

### `boolean isPlayer(Player player)`

Checks if a player is currently a viewer of this boss bar.

### `void resetBossBar()`

Resets the bar to its first frame and reapplies settings.

### `void addPlayer(Player... players)`

Adds one or more players to the boss bar.

* Fires `BossBarPlayerAddedEvent` (cancellable).

### `void addPlayer(Player player)`

Adds a single player.

* Ensures boss bar is initialized.
* Cancels if event is cancelled.

### `void removePlayer(Player player)`

Removes a player from the bar.

* Fires `BossBarPlayerRemovedEvent`.

### `void removeAllPlayers()`

Removes all players safely (avoids concurrent modification).

### `void displayNextFrame()`

Advances the bar to the next frame.

* Updates all viewers.
* Fires `BossBarFrameAdvancedEvent`.

### `void pause()`

Pauses animation.

* Fires `BossBarPausedEvent`.

### `void play()`

Resumes animation if paused.

* Fires `BossBarStartedEvent`.

### `DreamBossBar stop()`

Stops the bar completely.

* Removes all players.
* Fires `BossBarStoppedEvent`.
* Removes itself from `DreamCore.DreamBossBars`.

---

## Builder API

The `DreamBossBar.Builder` is the recommended way to create bars.

### `dreamfireBossBarData(DreamBossBarData data, int numberOfFrames)`

Adds a frame repeated for a number of ticks.

### `players(Player... toAdd)`

Adds players who should initially see the bar.

### `build(BarFlag... flags)`

Builds and registers the bar in `DreamCore`.

* Note: Returns the previous mapped instance due to `Map.put`. Keep in mind when retrieving.

---

## Frame Data (DreamBossBarData)

Each frame defines:

* `BarColor` — the color.
* `BarStyle` — the style (solid/segmented).
* `double barProgress` — between `0.0` and `1.0`.
* `Function<Player,String>` messageProvider — dynamic title per player.

### `void DisplayBarData(BossBar bar, Player player)`

Applies this frame to a specific player.

### `String safeTitle(Player player)`

Returns a formatted, plain-text safe title.

### `double clampedProgress()`

Clamps progress between `0.0` and `1.0`.

---

## Events

Events are dispatched **immediately from constructors**:

* **BossBarPlayerAddedEvent** — before a player is added (cancellable).
* **BossBarPlayerRemovedEvent** — after a player is removed.
* **BossBarFrameAdvancedEvent** — when a frame is applied.
* **BossBarPausedEvent** — when paused.
* **BossBarStartedEvent** — when resumed.
* **BossBarStoppedEvent** — when stopped.

**Example Listener:**

```java
@EventHandler
public void onBossBarAdded(BossBarPlayerAddedEvent e) {
    if (!e.getPlayer().hasPermission("bossbar.view")) {
        e.setCancelled(true);
    }
}
```

---

## Usage Flow

1. Build with `Builder`.
2. Register with `build()`.
3. Call `play()` to start animation.
4. Schedule `displayNextFrame()` every tick.
5. Pause/resume/stop as needed.
6. Handle events for customization.

---

## Pitfalls & Notes

* **Manual ticking required** — must call `displayNextFrame()` via scheduler.
* **Map.put behavior** — `build()` returns previous instance, not new.
* **Dynamic titles** — use `messageProvider` for per-player messages.
* **Events** — all fire synchronously during method calls.

---

## Suggestions

1. Change `build()` to return the **newly created bar**, not the previous mapping.
2. Add an **internal scheduler option** for auto-advancing frames.
3. Add `getViewers()` to return current players.
4. Make `BossBarPlayerRemovedEvent` cancellable for consistency.
5. Add support for **asynchronous title/message formatting** for heavy providers.
6. Provide utilities to save/load boss bar animations for reuse.