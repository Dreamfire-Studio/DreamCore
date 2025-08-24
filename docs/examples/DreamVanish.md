# DreamVanish — Beginner-Friendly Guide (Per-Viewer Visibility)

> **What is this?**
> `DreamVanish` lets you control **who can see whom** on your server. It tracks per-viewer visibility intent and applies it using Bukkit’s `hideEntity(...)` / `showEntity(...)` API. You can hide any `Entity` (players, armor stands, NPCs) from specific players and later reveal them.

---

## Concepts

* **Intent store:** `DreamCore.DreamVanishs` is a `Map<UUID, List<UUID>>` mapping **target UUID → list of viewer UUIDs** who should **NOT** see that target.
* **Application step:** Call `updateVanishOnAllPlayers()` to reconcile the intent with reality using `Player#hideEntity(...)`/`showEntity(...)`.
* **Events:** When you mark a hide/show intent, a Bukkit event is dispatched immediately: `VanishHideTargetEvent` / `VanishShowTargetEvent`.

> **Threading:** Always call these methods from the **main server thread**. Bukkit entity visibility APIs are not thread-safe.

---

## Quick Start

```java
// Hide Alice from Bob (Bob can’t see Alice anymore)
DreamVanish.hideTargetFromViewer(alice, bob);
// Later, push the change to the live server state
DreamVanish.updateVanishOnAllPlayers();

// Reveal Alice to Bob again
DreamVanish.showTargetToViewer(alice, bob);
DreamVanish.updateVanishOnAllPlayers();
```

---

## API Reference

### `hideTargetFromViewer(Entity target, Player viewer)`

**Purpose:** Record the intent that `viewer` should no longer see `target`. Fires `VanishHideTargetEvent` immediately.

**Parameters**

* `target`: Any Bukkit `Entity` to hide.
* `viewer`: The player who should not see the target.

**Behavior**

* Safely no-ops on `null` parameters.
* Adds `viewer.getUniqueId()` to the list for `target.getUniqueId()` in `DreamCore.DreamVanishs` (creating the list if needed).
* Dispatches `VanishHideTargetEvent(target, viewer)` synchronously.

**Example**

```java
// Staff vanish: hide staff member from a specific player
DreamVanish.hideTargetFromViewer(staff, player);
```

---

### `showTargetToViewer(Entity target, Player viewer)`

**Purpose:** Remove the hide intent for this viewer-target pair. Fires `VanishShowTargetEvent` immediately.

**Parameters**

* `target`: Entity to reveal.
* `viewer`: Player to whom the target becomes visible again.

**Behavior**

* Safely no-ops on `null` parameters.
* Removes `viewer` from the hidden list. If the list becomes empty, removes the target entry from `DreamCore.DreamVanishs`.
* Dispatches `VanishShowTargetEvent(target, viewer)` synchronously.

**Example**

```java
// Un-vanish target for a player
DreamVanish.showTargetToViewer(target, viewer);
```

---

### `canViewerSeeTarget(Entity target, Player viewer) : boolean`

**Purpose:** Convenience check to see if, per the stored intent, the viewer **should** be able to see the target.

**Return value**

* `true` if visible (no hide intent present), `false` if hidden or arguments are null.

**Example**

```java
if (!DreamVanish.canViewerSeeTarget(target, viewer)) {
    viewer.sendMessage(Component.text("You cannot see that entity right now."));
}
```

---

### `updateVanishOnAllPlayers()`

**Purpose:** Apply the entire hide/show matrix to the live server state.

**Behavior**

* Iterates all target entries in `DreamCore.DreamVanishs`.
* For each online player as a potential viewer:

    * If the viewer’s UUID is in the hidden list → `viewer.hideEntity(DreamCore.DreamCore, target)`.
    * Else → `viewer.showEntity(DreamCore.DreamCore, target)` (ensures previously hidden entities are re-shown).
* If a stored target is no longer present in the world, the entry is removed.

**When to call**

* After **batching** multiple hide/show intents.
* On **player join** (to enforce existing intents against the new viewer).
* On **player quit** / **world change** (optional housekeeping to realign view state).
* **Periodically** if your systems change intents asynchronously but you want a single reconciliation point.

**Example**

```java
@EventHandler
public void onJoin(PlayerJoinEvent e) {
    // Ensure new players immediately respect existing vanish intents
    Bukkit.getScheduler().runTask(plugin, DreamVanish::updateVanishOnAllPlayers);
}
```

---

## Events

### `VanishHideTargetEvent`

* **When**: After calling `hideTargetFromViewer(target, viewer)`.
* **Fields**: `Entity getTarget()`, `Player getViewer()`.
* **Usage**:

```java
@EventHandler
public void onHide(VanishHideTargetEvent e) {
    // Log or update UI when someone is hidden from someone else
}
```

### `VanishShowTargetEvent`

* **When**: After calling `showTargetToViewer(target, viewer)`.
* **Fields**: `Entity getTarget()`, `Player getViewer()`.
* **Usage**:

```java
@EventHandler
public void onShow(VanishShowTargetEvent e) {
    // Clean up UI elements, notify viewer, etc.
}
```

---

## Usage Patterns

### Staff Vanish (Per-Viewer)

```java
public void setStaffHiddenFrom(Player staff, Player viewer, boolean hidden) {
    if (hidden) DreamVanish.hideTargetFromViewer(staff, viewer);
    else DreamVanish.showTargetToViewer(staff, viewer);
    DreamVanish.updateVanishOnAllPlayers();
}
```

### Spectator Mode

Hide spectators from all non-spectators, but let spectators see each other.

```java
public void refreshSpectatorMatrix(Set<Player> spectators) {
    for (Player target : Bukkit.getOnlinePlayers()) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            boolean hidden = spectators.contains(target) && !spectators.contains(viewer);
            if (hidden) DreamVanish.hideTargetFromViewer(target, viewer);
            else DreamVanish.showTargetToViewer(target, viewer);
        }
    }
    DreamVanish.updateVanishOnAllPlayers();
}
```

### Temporary Target Hiding

```java
// Hide entity while an ability is active, then reveal after duration
DreamVanish.hideTargetFromViewer(entity, viewer);
Bukkit.getScheduler().runTaskLater(plugin, () -> {
    DreamVanish.showTargetToViewer(entity, viewer);
    DreamVanish.updateVanishOnAllPlayers();
}, 20L * 5); // 5 seconds
```

---

## Edge Cases & Best Practices

* **Self-visibility:** `updateVanishOnAllPlayers()` skips hiding a target from itself.
* **Entity lifecycle:** If an entity despawns/unloads, its entry is removed during update; you can also proactively clean on `EntityDeathEvent`/`PlayerQuitEvent`.
* **Performance:** For large servers, avoid O(N²) loops too frequently. Batch intents, then update once. Consider per-world scoping if needed.
* **Collections:** Using a `List<UUID>` means `contains(...)` is O(n). If you expect many viewers, a `Set<UUID>` is more scalable (see Suggestions).
* **Threading:** Only call from the main thread. If producing intents async, marshal a sync task for `updateVanishOnAllPlayers()`.
* **Cross-plugins:** `hideEntity(plugin, entity)` requires a **plugin instance**; the code uses `DreamCore.DreamCore` (singleton). Ensure that’s initialized.

---

## Troubleshooting

* **Viewer still sees target**: Did you call `updateVanishOnAllPlayers()` after setting intent? Are you on the main thread? Is the viewer actually in the same world/chunk (visibility still applies when entities load in)?
* **Target flickers visible**: Some systems may re-show entities. Make sure only one system owns visibility, or centralize via DreamVanish.
* **ConcurrentModification**: If you modify `DreamCore.DreamVanishs` while iterating it elsewhere, you may hit CME. See Suggestions for safer iteration strategies.

---

## Suggestions / Future Enhancements

* **Use `Set<UUID>`** for hidden viewers per target (`ConcurrentHashMap.newKeySet()`) to make `contains/remove` O(1) and avoid duplicates.
* **Use `ConcurrentHashMap<UUID, Set<UUID>>`** for `DreamCore.DreamVanishs` to reduce CME risk; or copy-on-write during updates.
* **Iterator-safe cleanup** in `updateVanishOnAllPlayers()` (collect orphan target UUIDs, remove after loop) to prevent concurrent modification.
* **Per-viewer apply** method: `updateVanishForViewer(Player viewer)` to avoid full-matrix passes on join.
* **Per-target apply** method: `updateVanishForTarget(Entity target)` to refresh just one entity.
* **Bulk APIs**: `hideTargetsFromViewers(Collection<Entity> targets, Collection<Player> viewers)` to batch set intents.
* **Events on apply**: Optional post-apply event (e.g., `VanishAppliedEvent`) for systems that need to react after the actual Bukkit hide/show calls.
* **Persistence**: Store vanish intents and re-apply on restart, with TTL support for temporary hides.
* **Policy layer**: Introduce roles/visibility rules (e.g., "Moderators are invisible to Non-Staff by default") with a resolver that populates intents before apply.
* **Auditing/Debug**: A debug command `/vanishdebug <player>` to print matrix and live visibility state.