# DreamLocation — Developer Guide

The **DreamLocation** utility class provides a collection of safe and reliable helpers for working with Minecraft `Location`, `Vector`, and `Block` objects. These helpers handle **null checks**, **world consistency**, and **efficient math** (using `distanceSquared` where possible). If you’re building features like regions, trails, or geometric effects, this is your toolkit.

---

## 📍 Closest Location

### `DreamLocation.closestLocation(List<Location>, Location)`

Finds the nearest location to a given origin.

#### Example

```java
Location nearest = DreamLocation.closestLocation(homes, player.getLocation());
```

* Requires all locations to be in the same world.
* Returns `null` if none share the world.
* Uses squared distance for performance.

---

## 🔗 Midpoint Between Two Locations

### `DreamLocation.midpoint(Location, Location)`

Finds the exact midpoint between two positions in the same world.

#### Example

```java
Location mid = DreamLocation.midpoint(home, spawn);
```

* Returns `null` if worlds differ.
* Useful for dividing paths, centering effects, or marking halfway points.

---

## 🧱 Block Between Two Corners

### `DreamLocation.isBlockBetween(Location, Location, Block)`

Checks if a block lies inside the cuboid defined by two corner locations.

#### Example

```java
if (DreamLocation.isBlockBetween(corner1, corner2, block)) {
    player.sendMessage("Inside region!");
}
```

* Requires all positions to be in the same world.
* Checks **axis-aligned bounding box** inclusively.

---

## 🌌 Points Between Two Locations

### `DreamLocation.pointsBetween(Location, Location, double)`

Returns evenly spaced points between two locations, including both endpoints.

#### Example

```java
Location[] trail = DreamLocation.pointsBetween(a, b, 1.0);
```

* `spacing` determines how close points are.
* If spacing > distance, just `{start, end}` is returned.
* Great for particle effects, paths, or teleport trails.

---

## 📦 Cube Fill (Area Locations)

### `DreamLocation.cubeLocations(Location, Location)`

Generates every block location inside the cuboid defined by two corners.

#### Example

```java
List<Location> cube = DreamLocation.cubeLocations(a, b);
```

* Iterates inclusively in all 3 axes.
* Used for filling areas, region selection, or scanning blocks.

---

## 📏 Total Path Distance

### `DreamLocation.totalDistance(Location...)`

Calculates the total length of a path that visits each location in order.

#### Example

```java
double distance = DreamLocation.totalDistance(a, b, c, d);
```

* Requires at least 2 locations.
* Validates that consecutive points are in the same world.
* Perfect for measuring path lengths (roads, waypoints, patrols).

---

## ➡️ Translate Location by Direction

### `DreamLocation.translate(Location, Vector, double)`

Moves a location a given distance in a given direction.

#### Example

```java
Location ahead = DreamLocation.translate(player.getLocation(), player.getLocation().getDirection(), 5);
```

* Normalizes the vector, then multiplies by distance.
* Useful for “look-ahead” positions, raycasts, or offset effects.

---

## 📐 Axis Angle Between Locations

### `DreamLocation.axisAngle(Location, Location, String)`

Computes the angle (radians) from one location to another, projected onto a plane defined by an axis.

#### Example

```java
double yaw = DreamLocation.axisAngle(a, b, "Y");
```

* Axis options:

    * `X`: angle in **YZ plane** → `atan2(dY, dZ)`
    * `Y`: angle in **XZ plane** → `atan2(dX, dZ)`
    * `Z`: angle in **XY plane** → `atan2(dY, dX)`
* Useful for aiming, rotations, or directional calculations.

---

## ✅ Summary

As a plugin developer:

* Use **closest**, **midpoint**, and **pointsBetween** for spatial math.
* Use **isBlockBetween** and **cubeLocations** for region/area checks.
* Use **translate** and **axisAngle** for motion and direction.
* Always trust these helpers to **validate inputs** and avoid null/world mismatches.

---

## 💡 Suggestions for Future Changes

* Add **sphere/circle location generators** (for particle rings, explosions, etc.).
* Add **region volume calculations** (counting blocks or estimating space).
* Introduce **enum for axis selection** instead of string (`Axis.X`, `Axis.Y`, `Axis.Z`).
* Provide **stream-based APIs** (`Stream<Location>`) for functional processing.
* Optimize **cubeLocations** with lazy iteration to avoid large memory lists.