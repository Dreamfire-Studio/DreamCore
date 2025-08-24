# DreamWorld — Beginner-Friendly Guide (Per-World Locks: Time, Difficulty, Player State)

> **What is this?**
> `DreamWorld` lets you enforce **world-level locks**: time of day, difficulty, and per-player state (gamemode, health, hunger, saturation) for everyone inside a specific world. Attach a `DreamWorld` controller to a Bukkit world by its UUID, configure the locks you want, and call `TickWorld()` periodically to keep the world consistent.

---

## Quick Start

```java
// 1) Create controller for a world
UUID worldId = world.getUID();
DreamWorld dw = new DreamWorld(worldId);

// 2) Configure locks
dw.setTimeLock(TimeLock.Noon);            // world time fixed at 6000
dw.setDifficultyLock(Difficulty.HARD);    // difficulty forced to HARD

dw.setGameModeLock(GameMode.ADVENTURE);  // all players in this world forced to ADVENTURE

dw.setHeartLock(20);                      // health fixed to 20 (10 hearts)
dw.setHungerLock(20);                     // hunger fixed to 20
dw.setSaturationLock(5);                   // saturation fixed to 5

// 3) Register in your core registry
DreamCore.DreamWorlds.put(worldId, dw);

// 4) Periodically enforce (e.g., each tick or every few ticks)
Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    for (DreamWorld w : DreamCore.DreamWorlds.values()) w.TickWorld();
}, 0L, 1L);
```

> **Threading:** Call `TickWorld()` from the **main server thread**.

---

## Time Presets: `TimeLock`

`TimeLock` is an enum of common time-of-day values in a Minecraft day (0..23999):

* `Daytime` (0)
* `Day` (1000)
* `Noon` (6000)
* `Sunset` (12000)
* `Nighttime` (13000)
* `Midnight` (18000)
* `Sunrise` (23000)

Use it with `setTimeLock(...)` to freeze world time to a specific value.

---

## Class: `DreamWorld`

### Constructor

```java
public DreamWorld(UUID worldUUID)
```

**Purpose:** Tie a controller to a specific Bukkit world (by UUID).

---

### `GetWorld() : World | null`

Resolves the Bukkit `World` for the stored UUID.

* If no world is found, the controller automatically unregisters itself from `DreamCore.DreamWorlds` and returns `null`.

**Example**

```java
World w = dw.GetWorld();
if (w == null) {
    getLogger().warning("World unloaded; controller removed.");
}
```

---

### `TickWorld()`

Enforces all configured locks on the world and every player currently inside it.

**What it does:**

1. Resolves the `World` via `GetWorld()` — if missing, exits.
2. Applies **Time** and **Difficulty** locks to the world.
3. Iterates **online players** and, for those in this world, applies:

    * **Gamemode** lock
    * **Health** lock
    * **Hunger** lock
    * **Saturation** lock

**When to call:**

* **Each tick** for strict enforcement, or less frequently (e.g., every 5–20 ticks) if you prefer reduced overhead.
* After changing lock configuration.
* On join/teleport/world-change events (optional immediate enforcement).

---

## Individual Lock Tick Methods (Internals)

> These are private helpers the controller uses during `TickWorld()`; you don’t call them directly but it’s helpful to understand what they do.

### `TickTimeLock(World world)`

* If `timeLock != null`, sets `world.setTime(timeLock.time)`.

### `TickDifficultyLock(World world)`

* If `difficultyLock != null`, sets `world.setDifficulty(difficultyLock)`.

### `TickGameModeLock(Player player)`

* If `gameModeLock != null`, sets `player.setGameMode(gameModeLock)`.

### `TickHeartLock(Player player)`

* If `heartLock != null`, sets `player.setHealth(heartLock)`.

### `TickHungerLock(Player player)`

* If `hungerLock != null`, sets `player.setFoodLevel(hungerLock)`.

### `TickSaturationLock(Player player)`

* If `saturationLock != null`, sets `player.setSaturation(saturationLock)`.

---

## Usage Patterns

### Adventure Lobbies

```java
DreamWorld lobby = new DreamWorld(lobbyWorld.getUID());
lobby.setTimeLock(TimeLock.Day);
lobby.setDifficultyLock(Difficulty.PEACEFUL);
lobby.setGameModeLock(GameMode.ADVENTURE);
lobby.setHeartLock(20);
lobby.setHungerLock(20);
lobby.setSaturationLock(20);
DreamCore.DreamWorlds.put(lobbyWorld.getUID(), lobby);
```

### Hardcore World

```java
DreamWorld hardcore = new DreamWorld(hardcoreWorld.getUID());
hardcore.setDifficultyLock(Difficulty.HARD);
// Leave player locks unset to allow natural survival gameplay
DreamCore.DreamWorlds.put(hardcoreWorld.getUID(), hardcore);
```

### Freeze Time at Night

```java
DreamWorld nightOnly = new DreamWorld(nightWorld.getUID());
nightOnly.setTimeLock(TimeLock.Nighttime);
DreamCore.DreamWorlds.put(nightWorld.getUID(), nightOnly);
```

---

## Best Practices & Edge Cases

* **Null World Cleanup:** The controller removes itself if the world cannot be resolved. Ensure your registry code tolerates removals while iterating (iterate over `new ArrayList<>(DreamCore.DreamWorlds.values())`).
* **Scheduling Cadence:** For heavy servers, consider applying every 5–20 ticks rather than every tick unless you need strict enforcement.
* **Health Bounds:** `player.setHealth(int)` can throw if the value exceeds max health or is negative. Prefer clamping to `[0, player.getAttribute(GENERIC_MAX_HEALTH)]` (see Suggestions).
* **Saturation Range:** Minecraft saturation is a float; large ints will be coerced. Keep saturation within a reasonable range (e.g., `0..20`).
* **Gamemode Changes:** Repeatedly setting the same gamemode each tick is usually fine, but you can avoid redundant sets for minor efficiency.
* **TimeLock Semantics:** This system sets **absolute time** each tick; if you want *time freeze* but not jitter, consider `doDaylightCycle=false` gamerule or only set time when it drifts.
* **Difficulty in Multiverse:** Some server setups mirror difficulty across worlds; ensure your admin tools don’t fight your lock.

---

## Troubleshooting

* **My world time still changes:** Another plugin or `/gamerule doDaylightCycle` may be interfering. Disable the gamerule or run `TickWorld()` each tick.
* **Players’ hearts/hunger bounce:** Another system is modifying player state. Either increase enforcement cadence or centralize the authority.
* **Controller disappears:** The target world is likely unloaded. Recreate/register the controller on world load.

---

## Suggestions / Future Enhancements

* **Clamping & Safety:** Add clamping helpers (e.g., `setHeartLock` → clamp to `[0, maxHealth]`, `setHungerLock` `[0,20]`, `setSaturationLock` `0..20f`) and avoid exceptions.
* **Redundancy Guard:** Only apply changes when a value actually differs (cache last applied per player/world) to reduce churn.
* **Event Hooks:** Fire events like `WorldLockAppliedEvent`, `PlayerLockAppliedEvent` for observability and plugin integrations.
* **Per-World Scheduler:** Let each `DreamWorld` own its own scheduled task with a configurable period instead of relying on a global tick loop.
* **Config/Serialization:** Provide serialize/deserialize for `DreamWorld` so locks persist across restarts.
* **Permission Escape Hatches:** Allow bypass for certain players (e.g., admins exempt from gamemode/health locks).
* **Partial Time Freeze:** Option to set time only when it deviates more than N ticks (reduces constant writes).
* **GUI Editor:** A simple GUI or command set to manage locks in-game.