# DreamScoreboard — Animated Sidebar Scoreboards

The **DreamScoreboard** system lets you build animated, dynamic sidebar scoreboards for your Minecraft server. It handles multiple frames, smooth line updates, and viewer management with event hooks.

---

## Why Use DreamScoreboard?

* ✅ Animated scoreboards (frame-by-frame).
* ✅ Event-driven: hooks for when scoreboards are created, players join/leave.
* ✅ Builder pattern for simple construction.
* ✅ Support for dynamic line text functions.
* ✅ Safe viewer handling with cancellable events.

---

## Core Classes

### `DreamScoreboard`

Represents a full scoreboard instance with multiple frames and viewer management.

**Key methods:**

* `isPlayerViewing(Player)` → check if a player is currently viewing this board.
* `addPlayer(Player)` → add a player to this board (fires `ScoreboardPlayerAddedEvent`).
* `removePlayer(Player)` → remove a player (fires `ScoreboardPlayerRemovedEvent`).
* `clearViewers()` → remove all viewers safely.
* `displayNextFrame()` → advances the animation (call in your tick loop).
* `setPaused(boolean)` → pause or resume animation.

**Usage:**

```java
board.addPlayer(player);
board.setPaused(false);
board.displayNextFrame();
```

### `DreamScoreboardLines`

Represents a **frame** of the scoreboard: title + ordered lines.

* `createSidebar(Scoreboard, String)` → creates the sidebar objective and lines.
* `updateSidebar(Scoreboard)` → refreshes title and line texts.

**Builder example:**

```java
DreamScoreboardLines frame = DreamScoreboardLines.builder()
    .addLine(5, new DreamScoreboardData(i -> "Top line"))
    .addLine(1, new DreamScoreboardData(i -> "Bottom line"))
    .build(() -> "Scoreboard Title");
```

### `DreamScoreboardData`

Represents a single line of text.

* `createLine(Scoreboard, int score, int teamIndex)` → creates the entry for the line.
* `updateLine(Scoreboard, int score)` → updates the line text dynamically.

**Example:**

```java
DreamScoreboardData line = new DreamScoreboardData(score -> "Kills: " + getKills());
```

---

## Building a Scoreboard

The builder pattern makes it easy:

```java
DreamScoreboard board = DreamScoreboard.builder()
    .addLineHolder(frame, 40) // repeat frame for 40 ticks (~2 seconds)
    .addPlayer(player)
    .paused(false)
    .create(true);
```

Each frame can be repeated multiple times to control its display duration. The `create()` method automatically fires `ScoreboardCreatedEvent`.

---

## Events

The system fires Bukkit events at key moments:

* `ScoreboardCreatedEvent` → fired when a board is created.
* `ScoreboardPlayerAddedEvent` → fired before a player is added. Cancel to prevent.
* `ScoreboardPlayerRemovedEvent` → fired before a player is removed. Cancel to keep them.

**Example:**

```java
@EventHandler
public void onBoardCreated(ScoreboardCreatedEvent e) {
    Bukkit.broadcastMessage("A new scoreboard was created!");
}
```

---

## Animation Loop

To make the scoreboard animate:

```java
Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    board.displayNextFrame();
}, 0L, 1L); // every tick
```

If paused (`board.setPaused(true)`), frames won’t advance.

---

## Suggestions for Future Improvements

* 🎨 **Per-player customization**: allow dynamic lines per viewer.
* ⏱ **Timed frames**: instead of repeating frames, allow explicit time durations.
* 🔄 **Auto-refresh system**: built-in scheduler for automatic animation.
* 📊 **Integration with placeholders**: directly support PlaceholderAPI values.
* 🧩 **Multi-board support**: manage multiple sidebars with priority/resolution.

---