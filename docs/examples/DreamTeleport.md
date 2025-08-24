# DreamTeleport — Developer Guide

The **DreamTeleport** system provides a flexible way to handle scheduled teleports for one or more players with countdowns, movement checks, and target flexibility. Instead of instantly teleporting players, you can enforce delays, add feedback via actionbar messages, and even cancel if players move away.

---

## Why Use DreamTeleport?

* ⏳ Adds **countdown-based teleports** instead of instant moves.
* 🧍 Supports **multiple players at once**.
* 🧭 Teleports to either a **fixed location** or a **live entity** (e.g., a boss).
* 🚫 Can **cancel on movement** (with tolerance).
* 📟 Provides **actionbar countdown feedback**.
* 🔄 Managed entirely on the **main Bukkit thread**, no async safety issues.

---

## Core Class: `DreamTeleport`

### Constructor

```java
DreamTeleport tp = new DreamTeleport(
    plugin,
    players,
    liveTarget, // LivingEntity (nullable)
    fixedTarget, // Location (nullable if liveTarget provided)
    seconds,     // countdown in seconds
    20L,         // periodTicks (20 = once per second)
    true,        // showCountdown
    true,        // cancelOnMove
    0.25         // moveTolerance (blocks)
);
```

**Parameters:**

* `plugin` → Owning plugin instance.
* `players` → Collection of players to teleport.
* `liveTarget` → Target entity (nullable).
* `fixedTarget` → Fixed destination (nullable if liveTarget given).
* `seconds` → Countdown duration.
* `periodTicks` → Scheduler period (20 = once per second).
* `showCountdown` → Whether to show countdown messages.
* `cancelOnMove` → Whether to cancel if player moves.
* `moveTolerance` → Distance in blocks before movement cancels teleport.

### Key Methods

* `start()` → Begins the teleport countdown.
* `cancel()` → Cancels teleportation without teleporting.
* `isEmpty()` → Returns `true` if no players are queued.
* `contains(Player p)` → Checks if a player is queued.
* `remove(Player p)` → Removes a player from the queue.
* `getSecondsLeft()` → Remaining countdown seconds.
* `getTotalSeconds()` → Original countdown time.

---

## Static API: `DreamTeleportAPI`

For ease of use, `DreamTeleportAPI` provides static helper methods and maintains a global registry of active teleports.

### 1. `teleportPlayer`

```java
DreamTeleportAPI.teleportPlayer(plugin, player, null, location, 5, true, true);
```

Teleports a single player with default tolerances.

**Parameters:**

* `plugin` → Owning plugin.
* `player` → Target player.
* `liveTarget` → Target entity (optional).
* `fixedTarget` → Location (optional if `liveTarget` is given).
* `seconds` → Countdown duration.
* `showCountdown` → Show countdown via actionbar.
* `cancelOnMove` → Cancel teleport if player moves.

---

### 2. `teleportPlayers`

```java
DreamTeleportAPI.teleportPlayers(
    plugin,
    List.of(p1, p2),
    bossEntity, // follow boss if alive
    null,
    10, // seconds
    true,
    false, // allow movement
    0.25,  // tolerance
    20L    // update once per second
);
```

Teleports multiple players with full parameter control.

---

### 3. `isPlayerTeleporting`

Checks if a player is currently queued for teleport.

```java
if (DreamTeleportAPI.isPlayerTeleporting(player)) {
    player.sendMessage("Already teleporting!");
}
```

---

### 4. `cancelPlayerTeleport`

Cancels a specific player’s teleport.

```java
DreamTeleportAPI.cancelPlayerTeleport(player);
```

---

### 5. `cancelAll`

Cancels all teleportations for a collection of players.

```java
DreamTeleportAPI.cancelAll(List.of(player1, player2));
```

---

## Best Practices

* Use **cancelOnMove** for PvP arenas or safe zone warps.
* Use **entity targets** (e.g., bosses) for cinematic events.
* Provide **feedback messages** for both success and cancellation.
* Always set a **moveTolerance** above `0.1` to prevent false cancels from micro-movements.
* Schedule `displayNextFrame()` or related calls in your tick/task loop if combining with countdown UIs.

---

## Suggestions for Future Improvements

* 📡 Add **events** (`TeleportStartedEvent`, `TeleportCancelledEvent`, `TeleportCompletedEvent`).
* 🎨 Add support for **customizable actionbar/title messages**.
* ⏱ Add **pause/resume** capability for cinematic sequences.
* 👥 Add **group teleport completion callbacks** (e.g., trigger once all players arrive).
* 🌍 Add support for **cross-world safe teleport checks**.

---