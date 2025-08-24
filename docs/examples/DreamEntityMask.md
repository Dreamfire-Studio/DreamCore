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

# DreamEntityMask — Beginner’s Guide & API

This guide explains how to use **DreamEntityMask** to control what entities a player can see. Masks are **per-player filters** that hide or reveal entities based on type, UUID, and distance.

---

## What DreamEntityMask Does

* Hides entities from a player without removing them from the world.
* Can filter by **entity type**, **UUID**, or by distance from the player.
* Provides lifecycle control (`play`, `pause`, `stop`).
* Fires events when masks are created, paused, resumed, or stopped.

---

## Quick Start

```java
DreamEntityMask mask = new DreamEntityMask.Builder()
    .minDistance(2.0)
    .maxDistance(20.0)
    .entityMaskType(EntityMaskType.LivingEntity)
    .CreateMask(player);

mask.play();

// In your tick loop
mask.displayNextFrame();
```

---

## Core Methods (DreamEntityMask)

### `addToExceptions(EntityType type)`

Adds an entity type to always remain visible.

```java
mask.addToExceptions(EntityType.VILLAGER);
```

### `addToExceptions(UUID id)`

Adds a specific entity (by UUID) to always remain visible.

### `displayNextFrame()`

Runs one tick of mask logic:

* Finds entities in the player’s world.
* Filters based on mask type, distance, and exceptions.
* Hides newly matched entities, restores previously hidden ones.

**Notes:**

* Must be called periodically (usually once per tick).
* Uses `DreamVanish.hideTargetFromViewer` and `DreamVanish.showTargetToViewer`.

### `pause()`

Pauses the mask.

* All hidden entities are revealed.
* Fires `EntityMaskPausedEvent`.

### `play()`

Resumes the mask.

* Entities matching the rules are hidden again.
* Fires `EntityMaskStartedEvent`.

### `stop()`

Stops and unregisters the mask.

* Restores all hidden entities.
* Removes mask from `DreamCore.DreamEntityMasks`.
* Fires `EntityMaskStoppedEvent`.

---

## Builder API

The `DreamEntityMask.Builder` helps configure masks fluently.

### Options

* `.entityTypeExceptions(List<EntityType>)` — keep certain types visible.
* `.uuidExceptions(List<UUID>)` — keep specific entities visible.
* `.deleteMaskOnNull(boolean)` — auto-delete if player ref is null.
* `.minDistance(double)` / `.maxDistance(double)` — set distance bounds.
* `.entityMaskType(EntityMaskType)` — filter scope (`Entity`, `LivingEntity`, `Player`).

### Creation

```java
DreamEntityMask mask = new DreamEntityMask.Builder()
    .minDistance(5.0)
    .maxDistance(30.0)
    .entityMaskType(EntityMaskType.Player)
    .CreateMask(player);
```

**Note:** If a mask already exists for the player, this will return the stored one and merge new exceptions into it.

---

## Events

DreamEntityMask fires these events (non-cancellable):

* **EntityMaskCreateEvent** — when a mask is created.
* **EntityMaskPausedEvent** — when paused.
* **EntityMaskStartedEvent** — when resumed.
* **EntityMaskStoppedEvent** — when stopped and unregistered.

```java
@EventHandler
public void onMaskCreated(EntityMaskCreateEvent e) {
    e.getPlayer().sendMessage("Entity mask active!");
}
```

---

## Usage Flow

1. Build mask with `Builder`.
2. Call `CreateMask(player)`.
3. Call `play()` to start.
4. Tick with `displayNextFrame()`.
5. Pause/stop as needed.
6. Listen to events.

---

## Pitfalls & Notes

* **Manual ticking required** — `displayNextFrame()` must be called regularly.
* **Player offline/null** — handle with `deleteMaskOnNull`.
* **Performance** — scanning all entities in world each tick can be heavy in large worlds.
* **Exceptions merging** — re-creating a mask merges exceptions, does not overwrite.

---

## Suggestions

1. Add automatic ticking integration (e.g., scheduled task per mask).
2. Optimize `displayNextFrame()` with chunk-based entity lookups.
3. Consider making events cancellable to intercept visibility changes.
4. Add bulk API to pause/stop all masks at once.
5. Add configuration for maximum tracked entities per mask.