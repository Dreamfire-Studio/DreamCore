# DreamRaycast — Player Raycasting Utilities

DreamRaycast is a utility for performing **raycasts** (line-of-sight checks) from players. It makes working with Bukkit’s ray tracing API much easier by providing convenience methods, entity filtering, block include/ignore rules, and optional particle trails.

---

## Why Use DreamRaycast?

* 🔦 Easy **block and entity detection** from player eyes.
* 🧱 Block material filtering (ignore or must-match certain blocks).
* 👥 Entity filters with adjustable ray thickness.
* ✨ Optional particle/debug trail rendering.
* 📏 Returns a simple `RaycastHit` with block/entity, hit position, and distance.

---

## Core Class: `DreamRaycast`

### `RaycastHit`

Represents the result of a raycast.

* `block()` → hit block (nullable).
* `entity()` → hit entity (nullable).
* `hitPosition()` → world-space location.
* `distance()` → distance from player eye.
* `hitBlock()` / `hitEntity()` → quick checks.

### `PathRenderer`

Functional interface used to draw trails. Example:

```java
DreamRaycast.PathRenderer trail = (world, loc, t, max) -> {
    world.spawnParticle(Particle.CRIT, loc, 1);
};
```

---

## Functions

### `rayCastFromPlayerIgnore`

Raycasts for the first **non-ignored** block.

```java
Block target = DreamRaycast.rayCastFromPlayerIgnore(player, 64, Particle.CRIT, Set.of(Material.GLASS));
```

* Skips `Material.GLASS`, finds the first solid beyond it.
* Optional particle trail.

### `rayCastFromPlayerMust`

Raycasts for the first block that **must match** the provided set.

```java
Block gold = DreamRaycast.rayCastFromPlayerMust(player, 50, null, Set.of(Material.GOLD_BLOCK));
```

* Ignores everything except matching blocks.

### `raycastEntities`

Entity-only raycast (no blocks).

```java
RaycastHit ent = DreamRaycast.raycastEntities(
    player, 32, 0.3,
    e -> e instanceof LivingEntity && e != player,
    DreamRaycast.simpleParticle(Particle.END_ROD)
);
```

* `raySize` = radius of capsule (0 disables entity checks).
* `entityFilter` = decide which entities to hit.

### `raycast`

Combined block + entity raycast. Returns the closest hit.

```java
RaycastHit hit = DreamRaycast.raycast(
    player,
    40,
    0.25,
    e -> e != player,
    Set.of(Material.GLASS, Material.LEAVES),
    false,
    DreamRaycast.simpleParticle(Particle.CRIT)
);
```

* Supports both block material rules and entity detection.
* Chooses the **nearest hit** automatically.

### `simpleParticle`

Quick helper to build a `PathRenderer` that spawns one particle per step.

```java
DreamRaycast.PathRenderer trail = DreamRaycast.simpleParticle(Particle.END_ROD);
```

---

## Example: Sniper Bow

```java
RaycastHit hit = DreamRaycast.raycast(
    player, 80, 0.2, e -> e instanceof Player && e != player,
    Set.of(Material.GLASS), false,
    DreamRaycast.simpleParticle(Particle.SMOKE_NORMAL)
);

if (hit != null && hit.hitEntity()) {
    Player target = (Player) hit.entity();
    target.damage(10.0, player);
}
```

---

## Suggestions for Future Improvements

* ✅ **Async raycasts**: allow running off-thread for performance.
* ✅ **Custom step size**: expose particle spacing parameter.
* ✅ **Fluid handling options**: include/exclude water/lava collisions.
* ✅ **Multi-hit results**: return all entities/blocks along the path, not just the closest.
* ✅ **Block face detection**: provide which face of the block was hit.

---