# DreamFakeBlock — Beginner’s Guide & API

This guide explains how to use **DreamFakeBlock** to create per-player illusions of blocks. Observers see fake materials instead of real ones, until removed.

---

## What DreamFakeBlock Does

* Creates fake blocks at specific world locations.
* Sends fake block data only to selected players.
* Can change, clear, or remove fake blocks dynamically.
* Fires events for lifecycle changes (created, updated, cleared, observer added/removed).

---

## Quick Start

```java
// Create a fake diamond block at a location
DreamFakeBlock fake = new DreamFakeBlock(location, Material.DIAMOND_BLOCK);

// Add a player as observer
fake.addObserver(player);

// Update block later
fake.updateMaterialForAllObservers(Material.EMERALD_BLOCK);

// Remove observer
fake.removeObserver(player);

// Clear all observers
fake.removeAllObservers();
```

---

## Core Methods (DreamFakeBlock)

### `addObserver(Player player)`

Adds a player to observers and sends them the fake block.

* Fires `FakeBlockObserverAddedEvent`.

### `removeObserver(Player player)`

Removes a player and restores the **real block**.

* Fires `FakeBlockObserverRemovedEvent`.

### `removeAllObservers()`

Clears all observers and restores the real block for everyone.

* Fires `FakeBlockClearedEvent`.

### `isPlayerObservingAtLocation(Player player, Location location)`

Checks if a player is observing this block at that location.

### `isLocation(Location location)`

Checks if this fake block is at the given location.

### `updateMaterialForAllObservers(Material newMaterial)`

Changes the fake block’s material and updates all observers.

* Fires `FakeBlockUpdatedEvent`.

### `displayNextFrame()`

Re-sends the current block state to observers (e.g., during frame updates).

### `getObserverCount()`

Returns how many players are observing this fake block.

---

## DreamFakeBlockAPI

Utility class for managing fake blocks stored in `DreamCore.DreamFakeBlocks`.

### `createFakeBlock(String id, Location location, Material material, Player... players)`

Creates and registers a new fake block, adds initial observers, and fires `FakeBlockCreatedEvent`.

### `removePlayerFromFakeBlock(Player player, Location location)`

Removes a player from a fake block at a location.

### `removePlayerFromFakeBlock(Player player, String id)`

Removes a player from a fake block by ID.

### `removeFakeBlock(Location location)`

Removes and clears a fake block at a location.

### `removeFakeBlock(String id)`

Removes and clears a fake block by ID.

### `returnMaterialForPlayer(Player player, Location location)`

Returns the fake material a player sees at a location, or null.

### `returnMaterialForID(String id)`

Returns the fake material for a given fake block ID.

---

## Events

* **FakeBlockCreatedEvent** — when a fake block is created.
* **FakeBlockClearedEvent** — when all observers are removed.
* **FakeBlockObserverAddedEvent** — when a player starts observing.
* **FakeBlockObserverRemovedEvent** — when a player stops observing.
* **FakeBlockUpdatedEvent** — when material changes.

```java
@EventHandler
public void onFakeBlockCreated(FakeBlockCreatedEvent e) {
    Bukkit.broadcastMessage("Fake block created at " + e.getFakeBlock().getLocation());
}
```

---

## Usage Flow

1. Create block with `DreamFakeBlock` or `DreamFakeBlockAPI.createFakeBlock`.
2. Add/remove observers as needed.
3. Update materials dynamically.
4. Clear when done.
5. Listen to events for changes.

---

## Pitfalls & Notes

* **Observer sync** — if players relog, fake blocks must be re-sent.
* **Persistence** — fake blocks do not survive server restarts.
* **Performance** — avoid mass updates every tick for hundreds of blocks.
* **ID collisions** — ensure unique IDs when using `DreamFakeBlockAPI`.

---

## Suggestions

1. Add persistence support (saving fake blocks across restarts).
2. Provide bulk update helpers (e.g., update multiple fake blocks in a region).
3. Add auto-cleanup when observers log out.
4. Support `BlockData` directly instead of just `Material`.
5. Add scheduled lifetimes (auto-remove after X seconds).
