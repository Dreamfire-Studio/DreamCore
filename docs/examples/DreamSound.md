# DreamSound — Developer Guide

The **DreamSound** utility class is a set of convenience wrappers for playing sounds in Minecraft. It builds on top of Bukkit’s `playSound` API but provides a more compact and standardized way of triggering sounds across your project.

---

## Why Use DreamSound?

* ✅ Cleaner and shorter method calls.
* ✅ Consistent API across your codebase.
* ✅ Works for **player-specific sounds** and **world/global sounds**.
* ✅ Automatically safe against null worlds when broadcasting sounds.

---

## Function Reference

### 1. `PlaySound(Sound minecraftSound, Player player, Location location, int volume, int pitch)`

This method plays a sound **only for a specific player**, originating from a given location.

**When to use:**

* UI interactions (menu clicks, confirmations).
* Player-only events (notifications, warnings).
* Feedback that should not disturb other players nearby.

**Parameters:**

* `minecraftSound` → The `Sound` enum constant (e.g., `Sound.UI_BUTTON_CLICK`).
* `player` → The specific player who should hear the sound.
* `location` → The world location where the sound originates.
* `volume` → Integer volume value (1 = normal volume).
* `pitch` → Integer pitch value (1 = normal pitch).

**Example:**

```java
// Play a button click sound for the player at their current position
DreamSound.PlaySound(Sound.UI_BUTTON_CLICK, player, player.getLocation(), 1, 1);
```

---

### 2. `PlaySound(Sound minecraftSound, Location location, int volume, int pitch)`

This method plays a sound **in the world at a given location**, audible to all players in range.

**When to use:**

* Environmental sounds (explosions, anvils, ambient effects).
* Shared world events (boss spawn, item drop, area triggers).
* Any sound that should be broadcast to multiple players.

**Parameters:**

* `minecraftSound` → The `Sound` enum constant.
* `location` → The world location where the sound originates.
* `volume` → Integer volume value.
* `pitch` → Integer pitch value.

This method includes a **safety check** to prevent errors if the `location.getWorld()` is null.

**Example:**

```java
// Play an anvil sound at a block location for all nearby players
DreamSound.PlaySound(Sound.BLOCK_ANVIL_LAND, someLocation, 1, 1);
```

---

## Best Practices

* Use **player-specific sounds** for menus and feedback to avoid disturbing other players.
* Use **world sounds** for global events that everyone should notice.
* Keep `volume` and `pitch` close to `1` for natural-sounding effects.
* Experiment with pitch values (e.g., `0.5` or `2.0`) for varied audio cues.
* Prefer `Sound` enums instead of strings for compile-time safety.

---

## Suggestions for Future Improvements

* 🎵 Add overloads using **float volume/pitch** (closer to Bukkit internals).
* 🎧 Add support for **SoundCategory** (e.g., `MUSIC`, `PLAYERS`, `AMBIENT`).
* 🧩 Provide a helper for **broadcasting sounds to players in a radius**.
* ⏱ Provide async-safe methods for triggering sounds from async tasks.
* 🔊 Add a **multi-sound sequence API** (play multiple sounds in order for effects).

---