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

# DreamBlockDisplay — Beginner’s Guide & API

This guide shows you how to **spawn**, **pool**, and **animate** `BlockDisplay` entities using DreamCore’s helpers. It’s written for first‑time users and explains each function, event, and common pattern with examples.

---

## What you can do

* Quickly **spawn and configure** displays with a fluent **builder**.
* **Animate** scale, rotation, and transforms on a scheduler.
* **Pool** displays to avoid frequent spawn/despawn overhead.
* **Listen** to a creation event to apply last‑mile tweaks.

---

## Quick Start (copy/paste)

```java
// 1) Build & spawn a diamond block display at a location
BlockDisplay display = new DreamBlockDisplay.BlockDisplayBuilder()
    .world(world)
    .location(loc)
    .itemScale(0.85)
    .billboard(Display.Billboard.CENTER)
    .spawn(Material.DIAMOND_BLOCK);

// 2) Spin it on the Y axis every tick
int taskId = BlockDisplayAnimator.animateCustom(display, t -> {
    t.getLeftRotation().rotateY((float) Math.toRadians(4));
}, 1L, plugin);

// 3) Stop later
Bukkit.getScheduler().cancelTask(taskId);
```

---

## Animation Helpers — `BlockDisplayAnimator`

Use these to mutate a display’s `Transformation` every `period` ticks. **You control** the scheduler task lifetime using the returned task ID.

### `animateScale(BlockDisplay display, Vector3f scaleIncrement, long period, JavaPlugin plugin)`

Incrementally scales the display each tick.

**Parameters**

* `display`: the display to animate.
* `scaleIncrement`: how much to add to X/Y/Z scale per tick.
* `period`: tick interval (1 = every tick).
* `plugin`: your plugin instance for scheduling.

**Returns**: Bukkit task ID (int).

**Example**

```java
int id = BlockDisplayAnimator.animateScale(display, new Vector3f(0.01f, 0.01f, 0.01f), 1L, plugin);
```

**Notes**

* Scale is mutated **in place**. Clamp in your logic if you don’t want it to grow forever.

---

### `animateCustom(BlockDisplay display, Consumer<Transformation> animator, long period, JavaPlugin plugin)`

Runs a custom transformation mutator each tick.

**Parameters**

* `display`: the display to animate.
* `animator`: callback receiving the **current** `Transformation` to mutate.
* `period`: tick interval.
* `plugin`: scheduler owner.

**Returns**: Bukkit task ID (int).

**Example: spin + bob**

```java
int id = BlockDisplayAnimator.animateCustom(display, t -> {
    // spin
    t.getLeftRotation().rotateY((float) Math.toRadians(3));
    // bob up/down lightly
    float y = t.getTranslation().y + 0.02f * (float) Math.sin(System.currentTimeMillis() / 180.0);
    t.getTranslation().y = y;
}, 1L, plugin);
```

**Tip**: You can combine rotation, translation, and scale in one animator.

---

## Event — `BlockDisplayCreatedEvent`

Fires right after a `BlockDisplay` is spawned and configured by the builder. It’s **not cancellable**.

**Data**

* `BlockDisplay blockDisplay` — the created entity.

**Listener pattern**

```java
@EventHandler
public void onCreated(BlockDisplayCreatedEvent e) {
    e.getBlockDisplay().setGlowing(true);
}
```

> **Note:** The event’s constructor already calls `PluginManager#callEvent`. If you fire it again manually, it will dispatch twice. See **Suggestions** for tightening this up.

---

## Pool — `BlockDisplayPool`

Keeps a simple list of inactive `BlockDisplay`s. Released displays are teleported to a hidden location and reused later.

### `acquire(World world, Location location, BlockData blockData) : BlockDisplay`

* Reuses an entity from the pool if present; otherwise spawns a new one.
* Teleports to `location` and assigns the given `blockData`.
* Validates `world`, `location`, and `blockData` are non‑null.

**Example**

```java
BlockDisplay d = BlockDisplayPool.acquire(world, loc, Bukkit.createBlockData(Material.QUARTZ_BLOCK));
```

### `release(BlockDisplay display)`

* Ignores null/invalid displays.
* Sets block to `air` and teleports to a hidden Y (−1000) in the default world, then stores it.

**Example**

```java
BlockDisplayPool.release(d);
```

### `clear()`

* Removes all pooled entities from the world and empties the pool. Use on plugin shutdown.

**Pitfalls**

* **Residual state:** `Transformation`, brightness, glow color, etc. are **not** reset on release. If you reuse, consider resetting those via the builder or a utility.
* **Hidden location world:** If no worlds are loaded, the hidden location’s world can be `null` (teleport is skipped). Ensure a world is loaded before pooling.

---

## Builder — `DreamBlockDisplay.BlockDisplayBuilder`

A fluent API to spawn and configure `BlockDisplay` entities.

### Lifecycle

1. Create a builder (defaults to world `"world"` and its spawn location).
2. Chain configuration setters.
3. Call `spawn(Material)` **or** `spawn(BlockData)`.
4. The builder configures the display, applies transforms and properties, and fires `BlockDisplayCreatedEvent`.

### Configuration Methods

* `world(World world)` — target world (non‑null).
* `location(Location location)` — spawn location (non‑null).
* `itemScale(double itemScale)` — uniform scale > 0.
* `leftRotation(Vector3f leftRotation)` — set left‑hand rotation quaternion components (*x,y,z*; engine resolves *w*).
* `customTransformation(Consumer<Transformation> fn)` — mutate the initial transform.
* `viewRange(float)` — render range (≥ 0).
* `shadowRadius(float)` — (≥ 0).
* `shadowStrength(float)` — (≥ 0).
* `displayHeight(float)` — (> 0).
* `displayWidth(float)` — (> 0).
* `billboard(Display.Billboard)` — facing mode.
* `itemGlowColor(Color)` — glow color override.
* `itemBrightness(Display.Brightness)` — emissive brightness.

### Spawn Methods

* `spawn(Material material) : BlockDisplay` — creates `BlockData` from material, then spawns.
* `spawn(BlockData blockData) : BlockDisplay` — spawns directly with your `BlockData`.

**Example**

```java
BlockDisplay bd = new DreamBlockDisplay.BlockDisplayBuilder()
    .world(world)
    .location(loc)
    .itemScale(0.75)
    .leftRotation(new Vector3f(0f, (float)Math.toRadians(30), 0f))
    .viewRange(16f)
    .shadowRadius(0.4f)
    .shadowStrength(0.7f)
    .displayWidth(1.0f)
    .displayHeight(1.0f)
    .billboard(Display.Billboard.CENTER)
    .itemGlowColor(Color.AQUA)
    .itemBrightness(new Display.Brightness(15, 15))
    .spawn(Bukkit.createBlockData(Material.EMERALD_BLOCK));
```

**Under the hood**

* Pulls current `Transformation`, applies scale and left rotation, runs any `customTransformation`, then sets it back.
* Applies view range, shadow settings, display width/height, billboard, glow color, and brightness.
* Fires `BlockDisplayCreatedEvent`.

---

## Recipes

### Breathing (pulsing) scale

```java
int id = BlockDisplayAnimator.animateCustom(display, t -> {
    float base = 0.85f;
    float pulse = (float)(0.1f * Math.sin(System.currentTimeMillis() / 200.0));
    t.getScale().set(base + pulse, base + pulse, base + pulse);
}, 1L, plugin);
```

### Carousel of blocks using the pool

```java
BlockData[] palette = {
    Bukkit.createBlockData(Material.QUARTZ_BLOCK),
    Bukkit.createBlockData(Material.IRON_BLOCK),
    Bukkit.createBlockData(Material.GOLD_BLOCK),
};

BlockDisplay d = BlockDisplayPool.acquire(world, loc, palette[0]);
AtomicInteger idx = new AtomicInteger();
int id = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    d.setBlock(palette[idx.getAndIncrement() % palette.length]);
}, 0L, 20L).getTaskId();
```

### Spin forever until removed

```java
int id = BlockDisplayAnimator.animateCustom(display, t -> t.getLeftRotation().rotateY((float)Math.toRadians(5)), 1L, plugin);
// later: cancel with Bukkit.getScheduler().cancelTask(id)
```

---

## Common pitfalls & gotchas

* **Don’t forget to cancel tasks.** Both animator methods return a task ID—store and cancel it when done.
* **Null checks are on you.** The helpers assume non‑null `display` and valid plugin context.
* **Scale explosions.** Incremental scaling without bounds will explode values. Clamp or oscillate.
* **Pooling doesn’t reset transforms.** Either reset on acquire or supply a `customTransformation`.
* **Event double‑fire.** Calling `PluginManager#callEvent` for `BlockDisplayCreatedEvent` more than once will dispatch multiple times.

---

## Suggestions

1. **Single event dispatch:** Remove the extra manual `callEvent(event)` where the event’s constructor already triggers dispatch. Keep one mechanism for consistency.
2. **Safety checks in animator:** Guard against `display == null` or `!display.isValid()` before mutating transformations.
3. **Reset on acquire:** Add optional flags or a `reset(BlockDisplay)` helper to clear transformation, brightness, glow, billboard, and view/shadow settings when reusing from the pool.
4. **Hidden world fallback:** Ensure the pool’s hidden location always targets a loaded world (e.g., lazily pick `location.getWorld()` from `acquire` when teleporting on release).
5. **Expose stop helpers:** Provide `startAnimation(...) -> BukkitTask` and `stop(BukkitTask)` helpers to avoid manual ID tracking.
6. **Easing & timelines:** Offer higher‑level animations (e.g., tween duration with easing curves) atop `animateCustom`.