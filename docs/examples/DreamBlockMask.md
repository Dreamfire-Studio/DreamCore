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

# DreamBlockMask — Beginner’s Guide & API

This guide shows you how to render a **per‑player block mask** using ephemeral block changes (client‑side only) around a player. It walks through the **Builder**, the mask **lifecycle** (`play`, `pause`, `displayNextFrame`, `stop`), and the **events** you can listen to.

> **What is a block mask?**
> It’s a temporary, player‑local view of the world made by sending *fake* block states to just that player. Server world state does not change.

---

## Quick Start (copy/paste)

```java
// 1) Configure a mask for the player
DreamBlockMask mask = new DreamBlockMask.Builder()
    .ignoreAir(true)                 // skip air for performance
    .minDistance(2.0)                // keep a 2‑block safety ring around the player
    .maxX(8).maxY(6).maxZ(8)         // mask region around the player
    .blockExceptions(Map.of(         // real->view substitutions inside the region
        Material.STONE, Material.GLASS,
        Material.DIRT,  Material.COARSE_DIRT
    ))
    .CreateMask(player);             // register or merge with existing

// 2) Start it
mask.play();

// 3) Tick it (e.g., every server tick)
int taskId = Bukkit.getScheduler().runTaskTimer(plugin, mask::displayNextFrame, 0L, 1L).getTaskId();

// 4) Pause/stop when needed
mask.pause();
mask.stop(); // restores and unregisters
```

---

## Core Concepts

* **Region**: An axis‑aligned box centered on the player: `maxX`, `maxY`, `maxZ` define the extents on each axis.
* **Min distance**: `minDistance` forms a hollow center so you don’t mask blocks too close to the player.
* **Exceptions**: `Map<Material, Material>` mapping *real* block type → the *view* block type to show.
* **Frames**: Each call to `displayNextFrame()` computes the desired view and sends the changes to the player.
* **Trail**: Optional persistent masked blocks that remain across frames.

---

## Class: `DreamBlockMask`

Represents a single, per‑player mask instance. You control it via the methods below.

### Lifecycle Methods

#### `boolean displayNextFrame()`

Computes and applies the next frame. **Call periodically** (e.g., every tick) while playing.

* **Returns**: `true` when nothing happened (player is null/offline or paused); otherwise `false`.
* **Behavior**:

    * Iterates positions in the axis‑aligned region around the player.
    * Skips positions within `minDistance` of the player.
    * If `ignoreAir` is true, it skips air blocks.
    * Chooses a view material via `blockExceptions.getOrDefault(realType, Material.BARRIER)`.
    * Prepares two maps:

        * `previousFrameStates`: the real states before masking.
        * `newFrameStates`: the states to send for this frame (view states and any restores).
    * Fires **BlockMaskFrameComputedEvent** (pre‑send), sends `player.sendBlockChanges(newFrameStates.values())`, updates `lastFrameBlockStates`, then fires **BlockMaskFrameAppliedEvent** (post‑send).

> **Heads‑up:** When `resetLastFrames` is true, any blocks displayed last frame but not present in the new frame are restored, unless they are kept in the persistent trail.

#### `void play()`

Switches from paused → playing and fires **BlockMaskStartedEvent**.

#### `void pause()`

Switches to paused, restores visible changes from the last frame and the persistent trail, and fires **BlockMaskPausedEvent**.

#### `DreamBlockMask stop()`

Stops, restores visible changes, fires **BlockMaskStoppedEvent**, and **unregisters** from `DreamCore.DreamBlockMasks` by calling `Map.remove(playerUUID)`. The return value is the *previous* mapping from the registry (by `Map.remove` semantics).

### Configuration / Introspection

#### `void addToExceptions(Map<Material, Material> blockExceptions)`

Merges more exception mappings into this mask (real → view). Defensive null check included.

#### `Map<Vector, BlockState> getVisitedTrailLocationsView()`

Returns an **unmodifiable view** of the persistent trail entries.

#### Getters

* `boolean isDeleteMaskOnNull()` — whether to auto‑delete on invalid player (used by builder only; this class does not schedule cleanup).
* `boolean isIgnoreAir()` — skip air in region iteration.
* `boolean isResetLastFrames()` — restore previous masked blocks if not in the new frame.
* `boolean isKeepTrailTheSame()` — persist masked blocks in `visitedTrailLocations`.
* `double getMinDistance()`, `getMaxX()`, `getMaxY()`, `getMaxZ()` — region parameters.
* `Map<Material, Material> getBlockExceptions()` — current mapping (immutable).
* `boolean isActionBarPaused()` — current play state (`true` = paused).

---

## Builder: `DreamBlockMask.Builder`

Use the builder to configure and create (or merge) a mask for a specific player.

### Builder Options

* `blockExceptions(Map<Material,Material>)` — bulk add mappings.
* `blockExceptions(Material target, Material view)` — add single mapping.
* `deleteMaskOnNull(boolean)` — request auto‑delete behavior when player becomes invalid *(note: the core class does not auto‑schedule deletions; you would implement cleanup from a player quit listener or scheduler).*
* `ignoreAir(boolean)` — skip air for performance.
* `resetLastFrames(boolean)` — restore last frame’s blocks if missing in new frame.
* `keepTrailTheSame(boolean)` — keep a persistent mask trail.
* `minDistance(double)` — non‑negative, hollow center radius.
* `maxX/maxY/maxZ(double)` — non‑negative extents of the region.

### `DreamBlockMask CreateMask(Player player)`

Creates or merges a mask for `player` and registers it in `DreamCore.DreamBlockMasks`.

* **If a mask already exists:** returns the existing one after merging your builder’s `blockExceptions` into it.
* **If none exists:** constructs a new mask with your options, fires **BlockMaskCreatedEvent**, and places it in the registry.

> **Important (current behavior):** As written, this method returns the **previous value** from the registry (per `Map.put`), which is typically `null` on creation. This is left **unchanged** to respect existing behavior. See **Suggestions** for an API refinement that returns the new/existing mask directly.

---

## Events (listen to these!)

All events are standard Bukkit events. Each class calls `PluginManager#callEvent(this)` from its **constructor**, so creating the event instance dispatches it immediately.

* **BlockMaskCreatedEvent** — fired after creating/registering a mask for a player.

    * Data: `Player player`, `DreamBlockMask mask`.
* **BlockMaskStartedEvent** — fired after `play()` takes effect.

    * Data: `Player player`, `DreamBlockMask mask`.
* **BlockMaskPausedEvent** — fired after `pause()` restores changes.

    * Data: `Player player`, `DreamBlockMask mask`.
* **BlockMaskStoppedEvent** — fired after `stop()` restores and unregisters.

    * Data: `Player player`, `DreamBlockMask mask`.
* **BlockMaskFrameComputedEvent** — fired **before** sending changes for a frame.

    * Data: `Player player`, `DreamBlockMask mask`, `Map<Vector, BlockState> newFrameStates`, `Map<Vector, BlockState> previousFrameStates`.
* **BlockMaskFrameAppliedEvent** — fired **after** sending changes for a frame.

    * Data: `Player player`, `DreamBlockMask mask`, `Map<Vector, BlockState> appliedStates`.

**Listener example**

```java
@EventHandler
public void onFrame(BlockMaskFrameComputedEvent e) {
    // Example: block masking of bedrock is not allowed
    e.getNewFrameStates().entrySet().removeIf(ent -> ent.getValue().getType() == Material.BEDROCK);
}
```

> **Note:** The event maps are exposed as **unmodifiable** in the current implementation. If you need mutability, you’ll want to clone/edit before send within the mask code (see Suggestions), or provide callbacks/APIs for safe interception.

---

## Recipes

### X‑ray style reveal (show ores only)

```java
DreamBlockMask mask = new DreamBlockMask.Builder()
    .ignoreAir(true)
    .maxX(12).maxY(8).maxZ(12)
    .blockExceptions(Map.ofEntries(
        Map.entry(Material.STONE, Material.BARRIER),    // everything else -> barrier
        Map.entry(Material.DEEPSLATE, Material.BARRIER)
    ))
    .CreateMask(player);
mask.play();
Bukkit.getScheduler().runTaskTimer(plugin, mask::displayNextFrame, 0L, 1L);
```

### Persistent trail behind the player

```java
DreamBlockMask mask = new DreamBlockMask.Builder()
    .keepTrailTheSame(true)
    .blockExceptions(Map.of(Material.AIR, Material.LIGHT)) // draw with invisible light blocks
    .maxX(6).maxY(6).maxZ(6)
    .CreateMask(player);
mask.play();
```

### Safe toggle on movement

```java
PlayerMoveEvent listener = new PlayerMoveEvent() { /* ... */ };
// on move start: mask.play();
// on move stop/quit: mask.pause(); or mask.stop();
```

---

## Pitfalls & Best Practices

* **Ticking is required:** The mask does *nothing* unless you call `displayNextFrame()` periodically.
* **Registry return value:** `CreateMask` returning the previous mapping is easy to misread. See Suggestions for an overload that returns the newly ensured mask.
* **Distance checks:** The region iterates using `double` loops for parity with existing logic. For large regions, consider coarse stepping or pre‑filtering if you add such features.
* **BARIER default:** If a real material isn’t in `blockExceptions`, the view defaults to `Material.BARRIER`. If you prefer “no change” defaulting, adjust the mapping accordingly.
* **Threading:** All calls (including `sendBlockChanges`) must run on the **main server thread**.
* **Restoration:** `pause()`/`stop()` send the last known real states back to the player; ensure you track them correctly if you extend the logic.

---

## Suggestions

1. **Return the ensured mask:** Make `CreateMask(Player)` return the *new or existing* mask directly. Option A: use `compute`/`computeIfAbsent` and return that. Option B: add `ensureMask(Player) : DreamBlockMask` alongside the current method for backward compatibility.
2. **Mutable pre‑send hook:** For `BlockMaskFrameComputedEvent`, provide a **mutable** map to allow trusted plugins to prune/alter `newFrameStates` safely before send (or add a dedicated `BlockMaskPreSendCallback`).
3. **Step & shape controls:** Add optional `step` and `RegionShape` (CUBE/SPHERE) like `DreamBlock`, plus Y‑clamping to world min/max for performance.
4. **Trail management API:** Add `clearTrail()`, `removeTrailAt(Vector)`, and `maxTrailSize` to prevent unbounded growth.
5. **Null/invalid player policy:** If `deleteMaskOnNull` is set, add a lightweight scheduler or an event listener (PlayerQuitEvent) to automatically `stop()` and unregister.
6. **Physics‑safe materials:** Provide a `MaskProfile` (enum/record) with presets (e.g., `XRAY`, `WIREFRAME`, `LIGHT_ONLY`) to bundle exception maps.
7. **Metrics & debug:** Add counters (blocks considered/masked/restored per frame) and a debug overlay command to help tune performance.
8. **Enum for defaults:** Replace the implicit `BARRIER` default with a `DefaultViewMode` enum: `{ BARRIER, NO_CHANGE, CUSTOM(Material) }`.
9. **API consistency:** Rename `CreateMask` → `createMask` (Java naming) and expose `isPaused()` instead of `isActionBarPaused()` for clarity.