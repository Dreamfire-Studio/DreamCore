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

# DreamBlock — Beginner’s Guide & API

This guide explains how to **scan, count, replace, and clear** blocks around a location using DreamCore’s `DreamBlock` utilities. It’s written from the perspective of a new user who wants to run **safe main‑thread edits** and **async read‑only queries** fast and correctly.

---

## At a Glance

* **Region shapes:** `RegionShape.CUBE` or `RegionShape.SPHERE`.
* **Sampling:** Use a `step` > 0 to skip coordinates and speed up scans.
* **Threading:**

    * **Read‑only** helpers can be wrapped with `Async` methods (returning `CompletableFuture`).
    * **World‑modifying** helpers **must run on the server main thread**.
* **Performance:** Prefer material filters and larger `step` where precision allows.

---

## Quick Start (Copy/Paste)

```java
Location center = player.getLocation();

// 1) Find nearby ores in a sphere of radius 12, sample every block
List<Block> ores = DreamBlock.filterBlocksInRadius(
    center, 12, 1, RegionShape.SPHERE,
    b -> b.getType().toString().endsWith("_ORE")
);

// 2) Count leaves quickly using step=2 (skips every other block)
int leaves = DreamBlock.countBlocksInRadius(center, 24, 2, RegionShape.CUBE,
    Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES);

// 3) Replace stone with deepslate (MAIN THREAD ONLY)
int replaced = DreamBlock.replaceBlocksInRadius(center, 6, 1, RegionShape.CUBE,
    new Material[]{ Material.STONE }, Material.DEEPSLATE);

// 4) Async: get all water blocks without blocking the server thread
CompletableFuture<List<Block>> waterFuture = DreamBlock.returnAllBlocksInRadiusAsync(
    center, 32, 3, RegionShape.SPHERE, Material.WATER
);
waterFuture.thenAccept(list -> Bukkit.getLogger().info("Found water: " + list.size()));
```

---

## Parameters You’ll See Everywhere

* **`location`** – Center of the region. Must have a valid `World`.
* **`radius`** – Half‑extent in blocks. Total cube size is `2*radius` on each axis.
* **`step`** – Grid sampling stride in blocks. `1` = check every coordinate; `2` = check every other, etc.
* **`shape`** – `CUBE` (fastest) or `SPHERE` (distance check).
* **`materials`** – Optional filter list. Empty = no filter.
* **`condition`** – A `Predicate<Block>` to decide inclusion.

> **Tip:** For spheres we use a **squared‑distance** check; no expensive square roots.

---

## Read‑Only Queries (Sync)

### `filterBlocksInRadius(location, radius, step, shape, condition)`

Returns a `List<Block>` that satisfy `condition`.

**Use when:** You need custom logic (e.g., name‑based ore detection, block data checks).

**Example:**

```java
var logs = DreamBlock.filterBlocksInRadius(center, 16, 2, RegionShape.CUBE,
    b -> Tag.LOGS.isTagged(b.getType())
);
```

**Notes:** Returns empty list if `world == null` or `step <= 0`.

---

### `returnAllBlocksInRadius(location, radius, step, shape, materials...)`

Returns **all** sampled blocks, optionally filtered by materials.

**Use when:** You only need material checks (fast path with `HashSet`).

**Example:**

```java
List<Block> liquids = DreamBlock.returnAllBlocksInRadius(center, 24, 1, RegionShape.SPHERE,
    Material.WATER, Material.LAVA
);
```

---

### `countBlocksInRadius(location, radius, step, shape, materials...)`

Counts the number of sampled block positions, optionally filtered by materials.

**Use when:** You only need a number.

**Example:**

```java
int airHoles = DreamBlock.countBlocksInRadius(center, 12, 1, RegionShape.CUBE, Material.AIR);
```

---

### `findClosestBlock(location, radius, shape, material)`

Returns the **nearest** block of a given material, or `null` if none is found.

**Use when:** You want the closest water source, bedrock sample, etc.

**Example:**

```java
Block nearestChest = DreamBlock.findClosestBlock(center, 20, RegionShape.SPHERE, Material.CHEST);
```

---

## World‑Modifying Queries (Main Thread Only)

### `replaceBlocksInRadius(location, radius, step, shape, targetMaterials[], replacementMaterial)`

Replaces matching blocks using `block.setType(replacement, false)` (no physics).

**Use when:** Bulk edits like turning regions into glass or clearing ores to another type.

**Main‑thread requirement:** Must be invoked on the server thread.

**Example:**

```java
int n = DreamBlock.replaceBlocksInRadius(center, 8, 1, RegionShape.CUBE,
    new Material[]{Material.DIRT, Material.GRASS_BLOCK}, Material.COARSE_DIRT);
```

---

### `clearBlocksInRadius(location, radius, step, shape, materials...)`

Sets matched blocks to `Material.AIR` via `setType(Material.AIR, false)`.

**Use when:** Carving tunnels, hollowing areas, or removing specific materials.

**Example:**

```java
int cleared = DreamBlock.clearBlocksInRadius(center, 6, 1, RegionShape.SPHERE, Material.STONE, Material.DEEPSLATE);
```

---

## Async Wrappers (Read‑Only)

Asynchronous variants return a `CompletableFuture` and internally call the synchronous read‑only method on a background thread. **They never modify world state.**

* `filterBlocksInRadiusAsync(...)` → `CompletableFuture<List<Block>>`
* `returnAllBlocksInRadiusAsync(...)` → `CompletableFuture<List<Block>>`
* `countBlocksInRadiusAsync(...)` → `CompletableFuture<Integer>`
* `findClosestBlockAsync(...)` → `CompletableFuture<Block>`

**Pattern:**

```java
DreamBlock.countBlocksInRadiusAsync(center, 48, 3, RegionShape.CUBE, Material.STONE)
    .thenAccept(count -> Bukkit.getLogger().info("Stone: " + count));
```

> **Reminder:** Bukkit API calls that access world state are generally **not thread‑safe**. These async helpers only **read** block types through the captured `World` reference. Avoid passing the resulting `Block` objects back to the main thread without re‑validating them (chunks may unload). Prefer returning **coordinates** if you need to act later.

---

## Choosing `step` & `shape` (Performance)

* **`step` trade‑off:**

    * `1` → maximum precision, slowest.
    * `2+` → fewer checks, faster scans; good for rough counts or proximity.
* **`CUBE` vs `SPHERE`:**

    * `CUBE` is fastest (no distance math).
    * `SPHERE` reduces edge samples but adds a squared‑distance check per point.
* **Material filters:** Provide them when possible—hash lookups are cheap and cut memory/GC.

**Rule of thumb:** For large radii, start with `CUBE + step=2` and refine if needed.

---

## Common Pitfalls & Safeguards

* **`step <= 0`** → methods early‑return (or 0) to protect you. Always pass `step >= 1`.
* **Null world** → methods early‑return empty/0/null.
* **Async misuse** → never call `replace...`/`clear...` off the main thread.
* **Chunk boundaries** → iterators do not force chunk loads; querying unloaded chunks will still return blocks if the world API yields them, but values may be stale. For strict correctness, ensure chunks are loaded.
* **Large scans** → radius^3 cost. Use `step` and material filters aggressively.

---

## Practical Recipes

### Count Nearby Ores (fast)

```java
int oreCount = DreamBlock.countBlocksInRadius(center, 48, 3, RegionShape.SPHERE,
    Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
    Material.DIAMOND_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.COPPER_ORE);
```

### Clear a Spherical Room

```java
int cleared = DreamBlock.clearBlocksInRadius(center, 7, 1, RegionShape.SPHERE);
player.sendMessage("Cleared blocks: " + cleared);
```

### Replace Dirt with Grass (no physics)

```java
int changed = DreamBlock.replaceBlocksInRadius(center, 10, 2, RegionShape.CUBE,
    new Material[]{ Material.DIRT }, Material.GRASS_BLOCK);
```

### Find Closest Chest

```java
Block nearest = DreamBlock.findClosestBlock(center, 20, RegionShape.SPHERE, Material.CHEST);
if (nearest != null) player.sendMessage("Nearest chest at: " + nearest.getLocation());
```

### Async Scan + Then Edit on Main Thread

```java
DreamBlock.returnAllBlocksInRadiusAsync(center, 32, 2, RegionShape.CUBE, Material.STONE)
    .thenAccept(blocks -> Bukkit.getScheduler().runTask(plugin, () -> {
        for (Block b : blocks) b.setType(Material.DEEPSLATE, false);
    }));
```

---

## API Index

**Synchronous:**

* `filterBlocksInRadius(Location, int, int, RegionShape, Predicate<Block>) : List<Block>`
* `returnAllBlocksInRadius(Location, int, int, RegionShape, Material...) : List<Block>`
* `replaceBlocksInRadius(Location, int, int, RegionShape, Material[], Material) : int`
* `countBlocksInRadius(Location, int, int, RegionShape, Material...) : int`
* `findClosestBlock(Location, int, RegionShape, Material) : Block`
* `clearBlocksInRadius(Location, int, int, RegionShape, Material...) : int`

**Asynchronous (read‑only):**

* `filterBlocksInRadiusAsync(...) : CompletableFuture<List<Block>>`
* `returnAllBlocksInRadiusAsync(...) : CompletableFuture<List<Block>>`
* `countBlocksInRadiusAsync(...) : CompletableFuture<Integer>`
* `findClosestBlockAsync(...) : CompletableFuture<Block>`

**Enums:**

* `RegionShape { CUBE, SPHERE }`

---

## Suggestions

1. **Coordinate return type for async:** Consider `BlockRef` DTO `{ world, x, y, z }` for async methods to avoid holding live `Block` references across threads. Add converters `toBlock()` on main thread.
2. **Bulk executor control:** Accept an optional `Executor` in async wrappers to integrate with your plugin’s thread pool.
3. **Strategy interfaces:** Extract a `RegionIterator` interface with `CubeIterator`/`SphereIterator` implementations, and a `BlockAction` functional interface for reuse/testing.
4. **Early abort:** Add a variant that stops scanning once a condition is met (e.g., `findAnyBlock` with predicate + optional `limit`).
5. **Physics mode enum:** For replace/clear, expose `PhysicsMode { APPLY, SUPPRESS }` rather than hardcoding `false`.
6. **Bounds clamping:** Optionally clamp Y to world min/max to avoid unnecessary calls when center±radius exceeds build height.