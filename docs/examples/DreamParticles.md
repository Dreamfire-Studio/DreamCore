# DreamParticles — Developer Guide

The **DreamParticles** utility class provides an easy way to work with Minecraft’s particle system. It supports **basic emissions**, **parametric shapes**, and **orientation helpers** so you can quickly build reusable particle effects.

---

## 🌟 Basic Particle Emission

### `spawnSingle(World, Particle, Location)`

Spawns a single particle at a location.

```java
DreamParticles.spawnSingle(world, Particle.FLAME, player.getLocation());
```

### `spawnSingle(World, Particle, Vector)`

Spawns a single particle at a raw vector position.

```java
DreamParticles.spawnSingle(world, Particle.HEART, new Vector(0, 80, 0));
```

### `spawn(...)`

Full control emission: count, offsets, speed, data, and force.

```java
DreamParticles.spawn(world, Particle.CRIT, loc, 8, 0.1, 0.1, 0.1, 0.01, null, false);
```

### `spawn(World, Particle, Location, int, double, double, double, double)`

Legacy convenience wrapper.

```java
DreamParticles.spawn(world, Particle.CLOUD, loc, 20, 0.5, 0.5, 0.5, 0.02);
```

---

## 🔷 Shape System

Instead of placing particles manually, you can use **shapes** — collections of relative points around an origin.

### `ParticleShape`

Functional interface that returns a `Collection<Vector>`.

```java
ParticleShape shape = DreamParticles.ring(2.0, 64);
DreamParticles.emitShape(world, Particle.FLAME, player.getLocation(), shape, 1, 0.0, 0.0);
```

### `emitShape(...)`

Emit all points of a shape around an origin.

```java
DreamParticles.emitShape(world, Particle.ENCHANTMENT_TABLE, loc,
    DreamParticles.sphere(1.5, 10, 20), 1, 0, 0, 0, 0, null, false);
```

Overload available with simplified arguments.

---

## 🌀 Built-in Shapes

### `sphere(double radius, int rings, int segments)`

Creates a hollow sphere shell.

```java
ParticleShape s = DreamParticles.sphere(2.0, 12, 32);
DreamParticles.emitShape(world, Particle.SOUL_FIRE_FLAME, center, s, 1, 0, 0, 0, 0, null, false);
```

### `ring(double radius, int segments)`

Flat ring in the XZ plane.

```java
ParticleShape r = DreamParticles.ring(3.0, 64);
DreamParticles.emitShape(world, Particle.END_ROD, center, r, 1, 0, 0);
```

### `cube(double sideLength, int stepsPerEdge)`

Cube shell using grid points on each face.

```java
ParticleShape c = DreamParticles.cube(2.0, 8);
DreamParticles.emitShape(world, Particle.REDSTONE, center, c, 1, 0, 0);
```

### `cone(double radius, double height, int rings, int segments)`

Cone shell aligned along +Y.

```java
ParticleShape cone = DreamParticles.cone(2.0, 5.0, 12, 32);
DreamParticles.emitShape(world, Particle.FIREWORKS_SPARK, loc, cone, 1, 0, 0);
```

### `spiral(double radius, double height, double turns, int points)`

Vertical spiral path.

```java
ParticleShape spiral = DreamParticles.spiral(1.0, 6.0, 3, 200);
DreamParticles.emitShape(world, Particle.VILLAGER_HAPPY, loc, spiral, 1, 0, 0);
```

---

## 🎛️ Orientation Helpers

### `rotateAroundAxis(Vector, Vector, double)`

Rotate a single vector around an axis.

```java
Vector rotated = DreamParticles.rotateAroundAxis(new Vector(1, 0, 0), new Vector(0, 1, 0), Math.PI/2);
```

### `rotatePoints(Collection<Vector>, Vector, double)`

Rotate an entire collection.

```java
List<Vector> rotated = DreamParticles.rotatePoints(shape.sample(), new Vector(0, 1, 0), Math.PI);
```

### `orientPoints(Collection<Vector>, Vector)`

Orient points so their local Y aligns with a given axis.

```java
List<Vector> oriented = DreamParticles.orientPoints(shape.sample(), new Vector(0, 0, 1));
```

---

## ⚠️ Deprecated Aliases

* `spawnParticle(World, Particle, Location)` → use `spawnSingle`.
* `spawnParticle(World, Particle, Vector)` → use `spawnSingle`.
* `spawnParticle(World, Particle, Location, int, double, double, double, double)` → use modern `spawn`.

These are kept for migration but should be avoided in new code.

---

## ✅ Summary

With DreamParticles you can:

* Spawn simple particles with `spawnSingle`.
* Build shapes like spheres, rings, cubes, cones, spirals.
* Orient shapes to match directions.
* Compose reusable effects with `ParticleShape`.

---

## 💡 Suggestions for Future Changes

* Add **filled shapes** (solid sphere, solid cube).
* Provide **noise-based shapes** (e.g., perlin cloud effects).
* Add **motion trails** (particles following moving entities).
* Include **color gradients** for REDSTONE particles.
* Support **time-based animations** for dynamic particle sequences.