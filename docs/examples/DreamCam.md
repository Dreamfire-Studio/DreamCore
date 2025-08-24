# DreamCam — Beginner’s Guide & API

This guide explains how to use **DreamCamPath** to create cinematic camera paths that move players smoothly along waypoints, optionally with focus targets. It covers path building, playback, event hooks, and look-at modes. Designed for plugin developers new to cinematic camera systems.

---

## What DreamCamPath Does

* Plays **camera fly-throughs** for one or more players.
* Teleports them smoothly along precomputed points.
* Preserves and restores player state (gamemode, location, flight).
* Fires events for start/stop, players joining/leaving, and waypoints reached.

---

## Quick Start

```java
DreamCamPath path = new DreamCamPath.CamPathBuilder()
    .addTravelPath(start, end, LookAtType.NoFocus, null, 100) // 100 ticks long
    .players(player)
    .create();

path.startCamPath();
```

---

## Core Methods (DreamCamPath)

### `addPlayer(Player)` / `addPlayer(Player...)`

Adds players to the path.

* Fires **CamPathPlayerAddedEvent** (cancellable).
* If cancelled, the player is not added.

### `removePlayerFromPath(Player, boolean hasFinished)`

Removes a single player, restores their state, and fires **CamPathPlayerLeaveEvent**.

* `hasFinished` → `true` if path ended normally, `false` if early exit.

### `removeAllPlayers(boolean hasFinished)`

Removes all current players from the path at once.

* Useful when stopping globally.

### `startCamPath()`

Begins playback for all added players.

* Saves gamemode, location, and flight state.
* Sets players to spectator.
* Fires **CamPathStartEvent**.
* Starts the `DreamCamPathRunnable` tick loop.

### `endCamPath(boolean hasFinished)`

Stops playback and restores states for all players.

* Fires **CamPathStopEvent**.
* Cancels the runnable.

### `onDisable()`

Safe cleanup hook — removes all players when the plugin disables.

### `onPlayerLeave(Player)`

Removes a specific player if they disconnect unexpectedly.

---

## DreamCamPathRunnable

Handles ticking through points once `startCamPath` is called.

* Teleports all players each tick.
* Adjusts rotation based on **LookAtType**.
* Fires **CamPathPointReachedEvent** every tick.
* Ends automatically when points are exhausted.

---

## DreamCamSet

Represents a segment of the path.

* Contains ordered points (locations).
* Stores rotation behavior (`LookAtType`).
* Can look at a fixed location, a moving entity, or simply forward.

**Rotation is calculated per point:**

```java
current.setYaw(newYaw);
current.setPitch(newPitch);
```

---

## LookAtType (Enum)

* **FixedFocus** — always face a specific location.
* **MovingFocus** — follow a moving entity (like tracking a boss).
* **NoFocus** — just face along the path.

---

## Events

* **CamPathPlayerAddedEvent** — before a player is added (cancellable).
* **CamPathPlayerLeaveEvent** — when a player leaves or finishes.
* **CamPathStartEvent** — when playback begins.
* **CamPathStopEvent** — when playback ends.
* **CamPathPointReachedEvent** — every tick, when reaching a point.

**Example:**

```java
@EventHandler
public void onPoint(CamPathPointReachedEvent e) {
    e.getLocation().getWorld().playSound(e.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
}
```

---

## Builder API

The `DreamCamPath.CamPathBuilder` helps you set up paths.

### `.addTravelPath(start, end, lookAtType, data, durationInTicks)`

Creates a linear segment from `start` to `end` with smooth interpolation.

* `lookAtType` → determines camera focus mode.
* `data` → location or entity, depending on mode.
* `durationInTicks` → how long the travel takes.

### `.players(Player...)`

Adds initial players to the path.

### `.create()`

Builds and registers the path in `DreamCore.DreamCamPaths`.

* **Note:** Returns the previous mapping if any (because of `Map.put`).

---

## Usage Flow

1. Build path with `CamPathBuilder`.
2. Call `startCamPath()` to play.
3. Listen for events to trigger effects.
4. Call `endCamPath(true/false)` to stop manually.

---

## Pitfalls & Notes

* **Duration must be > 0** when adding paths.
* **Player state must be restored** — handled automatically, but be careful if players log off mid-path.
* **Events fire instantly from constructors** — do not create events manually.
* **Large paths** with many points can be heavy — consider optimizing step counts.

---

## Suggestions

1. Change `.create()` to return the **new path instance** instead of the previous mapping to reduce confusion.
2. Add path looping/replay support.
3. Add easing functions (ease-in/out) instead of only linear travel.
4. Add per-segment delays or triggers (e.g., wait at a location).
5. Allow dynamic runtime modification (adding/removing players mid-run).