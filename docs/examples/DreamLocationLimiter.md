# DreamLocationLimiter — Developer Guide

The **DreamLocationLimiter** system allows you to restrict players to a defined area around a point. It’s perfect for arenas, dungeons, minigames, or any controlled region where you need to stop players from wandering too far.

---

## 🚧 What is a LocationLimiter?

A **LocationLimiter**:

* Defines an **origin point** (center of the zone).
* Defines a **radius (extents)** around that point.
* Watches players and ensures they stay within that radius.
* Supports two behaviors when a player leaves the zone:

    * **SNAP\_TO\_ORIGIN**: instantly teleports them back.
    * **PUSH\_BACK**: pushes them back with velocity.

---

## 🛠️ Creating a Limiter

You typically use the **builder** to configure and create a limiter.

```java
DreamLocationLimiter limiter = new DreamLocationLimiter.LocationLimiterBuilder()
    .type(LocationLimiterType.PUSH_BACK)
    .extents(20)
    .edgeMessage("Stay inside the arena!")
    .addPlayer(player)
    .build(arenaCenter);

limiter.startLocationLimiter();
```

This will:

* Create a new limiter centered on `arenaCenter`.
* Limit players to a 20-block radius.
* Push them back if they try to leave.
* Send them the message *"Stay inside the arena!"*.

---

## 👥 Adding and Removing Players

### `AddPlayer(Player)`

Adds a player to the limiter. Fires `LocationLimiterPlayerAddedEvent`, which can be cancelled.

```java
limiter.AddPlayer(player);
```

### `RemovePlayer(Player)`

Removes a player from the limiter. Fires `LocationLimiterPlayerRemovedEvent`.

```java
limiter.RemovePlayer(player);
```

---

## ▶️ Starting and Stopping

### `startLocationLimiter()`

Begins enforcing the limiter. If configured with `LocationLimiterStart.ORIGIN_POINT`, all players are teleported to the origin.

```java
limiter.startLocationLimiter();
```

### `stopLocationLimiter()`

Stops enforcement and unregisters the limiter from DreamCore.

```java
limiter.stopLocationLimiter();
```

### `toggleLocationLimiter(boolean)`

Enable or pause enforcement without destroying the limiter.

```java
limiter.toggleLocationLimiter(true); // resume
limiter.toggleLocationLimiter(false); // pause
```

---

## 🔄 Main Loop Enforcement

### `tickLocationLimiter()`

Checks all players each tick:

* If a player is in another world → teleports them back.
* If they exceed the radius → handles them based on limiter type.

You typically call this inside your main loop or scheduler.

```java
Bukkit.getScheduler().runTaskTimer(plugin, limiter::tickLocationLimiter, 0L, 20L);
```

---

## 🚨 Boundary Handling

When a player exceeds the radius:

* They receive the **edge message**.
* A `LocationLimiterLimitHit` event is fired.
* Depending on limiter type:

    * **SNAP\_TO\_ORIGIN**: teleports them back.
    * **PUSH\_BACK**: applies a velocity toward origin.

```java
@EventHandler
public void onHit(LocationLimiterLimitHit e) {
    e.getPlayer().sendMessage("Boundary reached!");
}
```

---

## ⚙️ Configuration Methods

* `setEdgeMessage(String)` → Change warning message.
* `setDistanceExtents(int)` → Change radius.
* `setLocationLimiterType(LocationLimiterType)` → Switch between push/teleport.
* `setOriginPoint(Location)` → Move limiter center.

---

## 📡 Events

### `LocationLimiterPlayerAddedEvent`

* Fired when a player is about to be added.
* **Cancellable**.
* Useful for permissions or conditions.

### `LocationLimiterPlayerRemovedEvent`

* Fired after a player is removed.
* Not cancellable.
* Good for cleanup or notifications.

### `LocationLimiterLimitHit`

* Fired when a player hits the limiter boundary.
* Triggered every time they are pushed or snapped back.

---

## ✅ Summary

As a developer:

* Use the **builder** to create limiters.
* Add/remove players as needed.
* Call `tickLocationLimiter` on a repeating schedule.
* Handle limiter events to extend gameplay (warnings, penalties, effects).
* Customize radius, behavior, and messages.

---

## 💡 Suggestions for Future Changes

* Add **polygon/irregular region support** instead of just circles.
* Support **vertical bounds** (Y min/max).
* Add **per-player extents** (different radii per player).
* Allow **dynamic messages** with placeholders (e.g., `{distanceRemaining}`).
* Provide built-in **integration with scoreboards/boss bars** for visual feedback.