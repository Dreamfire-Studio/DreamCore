# DreamItemDisplay — Developer Guide with Examples

The **DreamItemDisplay** system provides a fluent builder for spawning and configuring `ItemDisplay` entities in modern Minecraft (Paper/Bukkit API). It makes it much easier to handle scaling, rotations, translations, glow, and view properties without manually dealing with low-level APIs.

This guide is written for developers new to this system.

---

## 📦 Core Class: DreamItemDisplay.ItemDisplayBuilder

### Overview

The `ItemDisplayBuilder` is a fluent API that lets you configure all aspects of an `ItemDisplay` before spawning it in the world. Most methods return the builder itself for chaining.

If no `world` or `location` is provided, it defaults to:

* The **first loaded world** from Bukkit.
* That world’s **spawn location**.

---

### `world(World world)`

Sets the world where the item display will be spawned.

* Must be non-null.
* Automatically updates spawn location to world’s spawn point if none is set.

**Example:**

```java
builder.world(player.getWorld());
```

---

### `location(Location location)`

Sets the exact spawn location.

* Must be non-null.
* Also updates the builder’s `world` if the location has one.

**Example:**

```java
builder.location(new Location(world, 10.5, 65, -7.25));
```

---

### `translation(Vector3f translation)`

Applies a local translation (pre-rotation) to the item.

* Useful for nudging the item’s display position inside the entity.

**Example:**

```java
builder.translation(new Vector3f(0f, 0.25f, 0f));
```

---

### `scale(float uniform)`

Sets a uniform scale across all axes.

* Must be greater than `0`.
* Overridden if `scale(Vector3f)` is later set.

**Example:**

```java
builder.scale(0.75f); // Shrinks item to 75%
```

---

### `scale(Vector3f scale)`

Sets non-uniform scaling.

* Each component must be greater than `0`.

**Example:**

```java
builder.scale(new Vector3f(1.0f, 0.5f, 1.25f));
```

---

### `leftRotation(Quaternionf q)`

Sets the left rotation quaternion.

* Use for precise quaternion math.

**Example:**

```java
builder.leftRotation(new Quaternionf().rotateY((float)Math.toRadians(90)));
```

---

### `rightRotation(Quaternionf q)`

Sets the right rotation quaternion.

* Usually left as identity unless advanced transformation required.

**Example:**

```java
builder.rightRotation(new Quaternionf());
```

---

### `eulerDegrees(float yawDeg, float pitchDeg, float rollDeg)`

Convenience method for rotation using Euler angles.

* Converts degrees into a quaternion.
* Applies in order: yaw(Z), pitch(X), roll(Y).

**Example:**

```java
builder.eulerDegrees(0f, 45f, 0f);
```

---

### `viewRange(float viewRange)`

Sets render distance (in blocks).

* Clamped to ≥ 0.

**Example:**

```java
builder.viewRange(32.0f);
```

---

### `shadowRadius(float shadowRadius)`

Sets shadow radius (in blocks).

* Clamped to ≥ 0.

**Example:**

```java
builder.shadowRadius(0.4f);
```

---

### `shadowStrength(float shadowStrength)`

Sets shadow strength.

* Clamped to \[0, 1].

**Example:**

```java
builder.shadowStrength(0.7f);
```

---

### `displayHeight(float displayHeight)`

Sets display height in world units.

* Must be > 0.

**Example:**

```java
builder.displayHeight(1.5f);
```

---

### `displayWidth(float displayWidth)`

Sets display width in world units.

* Must be > 0.

**Example:**

```java
builder.displayWidth(0.8f);
```

---

### `billboard(Display.Billboard billboard)`

Sets the billboard rendering mode.

* Defaults to `CENTER` if null.

**Example:**

```java
builder.billboard(Display.Billboard.FIXED);
```

---

### `glowColor(Color color)`

Sets the glow color override.

* Null clears the override.

**Example:**

```java
builder.glowColor(Color.AQUA);
```

---

### `brightness(Display.Brightness brightness)`

Sets the display brightness override.

* Null resets to default.

**Example:**

```java
builder.brightness(Display.Brightness.LOW);
```

---

### `spawn(ItemStack itemStack)`

Spawns the configured `ItemDisplay` in the world.

* ItemStack must not be null or AIR.
* Throws if no world available.
* Automatically applies transformations and properties.
* Fires an `ItemDisplaySpawnEvent`.

**Example:**

```java
ItemDisplay display = builder.spawn(new ItemStack(Material.DIAMOND_SWORD));
```

---

## 📢 ItemDisplaySpawnEvent

Fired immediately after an `ItemDisplay` is spawned and configured.

**Example:**

```java
@EventHandler
public void onItemDisplaySpawn(ItemDisplaySpawnEvent e) {
    e.getItemDisplay().setPersistent(true);
}
```

---

## 💡 Suggestions for Future Improvements

* Add support for **animations** (e.g., rotation updates per tick).
* Add chaining helpers for **common presets** (e.g., floating sword, spinning item).
* Provide async-safe pre-building (apply configs before scheduling main-thread spawn).
* Add event cancellation support before spawn (like a pre-spawn event).
* Extend builder to also support **BlockDisplay** and **TextDisplay** with shared API.