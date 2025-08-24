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

# DreamArmorStand — Beginner’s Guide & API

This guide walks you through **everything** you need to animate, pose, and manage `ArmorStand` entities using DreamCore’s `DreamArmorStand` toolkit. It’s written for first‑time users and includes quick‑start snippets, per‑method explanations, events, and pitfalls.

---

## TL;DR — Quick Start (Copy/Paste)

```java
// 1) Build a frame (head nod for 40 ticks)
ArmorStandAnimationFrameData nod = new ArmorStandAnimationFrameDataBuilder()
    .duration(40)
    .head(new EulerAngle(0, 0, 0), new EulerAngle(Math.toRadians(30), 0, 0))
    .build();

// 2) Spawn an ArmorStand and configure basics
ArmorStand stand = new DreamfireArmorStandBuilder(spawnLoc)
    .arms(true)
    .basePlate(false)
    .customNameVisible(true)
    .customName("&aHello, World!")
    .build();

// 3) Create an animator and add frames
DreamArmorStandAnimator animator = new DreamArmorStandAnimator.Builder()
    .targetArmorStand(stand)
    .addFrame(nod, 3)   // repeat nod 3x
    .build();

// 4) Run the animation every tick
int id = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    if (animator.displayNextFrame()) {
        Bukkit.getScheduler().cancelTask(id);
    }
}, 0L, 1L).getTaskId();

// 5) Pause/Play at will
animator.pause();
animator.play();
```

---

## Core Building Blocks

### ArmorStandAnimationFrameData

Defines a **single animation frame**: start/end `EulerAngle` per body part + a **duration in ticks**.

```java
new ArmorStandAnimationFrameData(
    /* durationTicks */ 40,
    /* headStart, headEnd */ new EulerAngle(0,0,0), new EulerAngle(Math.toRadians(30),0,0),
    /* bodyStart, bodyEnd */ null, null,
    /* leftArmStart, leftArmEnd */ null, null,
    /* rightArmStart, rightArmEnd */ null, null,
    /* leftLegStart, leftLegEnd */ null, null,
    /* rightLegStart, rightLegEnd */ null, null
);
```

* Pass `null` for any body part you don’t animate in this frame.
* `durationTicks` **must be > 0**.

**When to use:** You want fine control over interpolation for specific limbs per frame.

**Key methods:**

* `long getDurationTicks()` — frame length.
* `EulerAngle get{Part}Start()/get{Part}End()` — per‑part angles (may be `null`).

**Pitfall:** Mixing degrees and radians. **EulerAngle expects radians.** (Builder examples below convert degrees → radians.)

---

### ArmorStandAnimationFrameDataBuilder

Fluent builder for `ArmorStandAnimationFrameData`. Great for readability and skipping parts with `null`.

```java
ArmorStandAnimationFrameData wave = new ArmorStandAnimationFrameDataBuilder()
    .duration(30)
    .rightArm(new EulerAngle(0,0,0), new EulerAngle(0, Math.toRadians(45), 0))
    .build();
```

* `duration(long ticks)` — must be `> 0`.
* `head/body/leftArm/rightArm/leftLeg/rightLeg(EulerAngle start, EulerAngle end)` — set per‑part angles.
* `build()` — returns the immutable frame object.

**Tip:** Use `Math.toRadians(deg)` when building angles by hand.

---

### ArmorStandAnimatorData

Wraps a simple per‑frame **action**: `Consumer<ArmorStand>`. Use when you need arbitrary per‑frame logic rather than numeric interpolation.

```java
ArmorStandAnimatorData glowOn = new ArmorStandAnimatorData(s -> s.setGlowing(true));

// later
glowOn.displayFrame(stand);
```

* Validates non‑null action.
* `displayFrame(ArmorStand)` simply applies your action.

**When to use:** Quick one‑off effects or complex mutations that aren’t just pose interpolation.

---

## Events (listen & react)

All events extend Bukkit `Event`. The ones below are **not cancellable** and fire immediately when their corresponding helper is called or state changes.

### ArmorStandEqippedEvent *(typo in class name)*

Fired after `DreamArmorStand.equipArmorStand(...)`.

* **Data:** `ArmorStand armorStand`, `ItemStack item`, `ArmorStandSlot slot`.
* **Note:** Consider renaming class to `ArmorStandEquippedEvent` for correctness (see suggestions).

### ArmorStandGlowEvent

Fired after `DreamArmorStand.setGlowing(...)`.

* **Data:** `ArmorStand armorStand`, `boolean glowing`.

### ArmorStandPosedEvent

Fired after `DreamArmorStand.setPose(...)` for a specific body part.

* **Data:** `ArmorStand armorStand`, `ArmorStandPose pose`, `float angleX/Y/Z` (**degrees** passed to method; event stores the same values you passed).

### ArmorStandSpawnEvent

Fired by `DreamfireArmorStandBuilder.build()` right after spawning.

* **Data:** `ArmorStand armorStand`, `Location location`, `boolean isVisible`, `boolean customNameVisible`, `String customName`, `boolean canPickupItems`, `boolean useGravity`.

**Listener pattern:**

```java
@EventHandler
public void onSpawn(ArmorStandSpawnEvent e) {
    if (e.isVisible()) e.getArmorStand().setGlowing(true);
}
```

---

## Enums

### ArmorStandPose

`HEAD`, `BODY`, `LEFT_ARM`, `RIGHT_ARM`, `LEFT_LEG`, `RIGHT_LEG` — used by `setPose(...)` and pose events.

### ArmorStandSlot

`HEAD`, `CHEST`, `LEGS`, `FEET`, `HAND`, `OFFHAND` — used by equipment helpers and equip events.

---

## Utilities & Builders

### DreamArmorStand (static helpers)

Convenience methods to operate on an `ArmorStand`.

* **`equipArmorStand(ArmorStand, ItemStack, ArmorStandSlot)`**
  Sets the appropriate equipment slot then fires `ArmorStandEqippedEvent`.

  ```java
  DreamArmorStand.equipArmorStand(stand, new ItemStack(Material.DIAMOND_HELMET), ArmorStandSlot.HEAD);
  ```

* **`setPose(ArmorStand, ArmorStandPose, float angleX, float angleY, float angleZ)`**
  Accepts **degrees**, converts to radians internally, applies to the selected limb, then fires `ArmorStandPosedEvent`.

  ```java
  DreamArmorStand.setPose(stand, ArmorStandPose.RIGHT_ARM, 0f, 45f, 0f);
  ```

* **`toggleVisibility(ArmorStand)`**
  Flips `visible`.

* **`disableGravity(ArmorStand)`** / **`enableGravity(ArmorStand)`**
  Sets `gravity` false/true.

* **`setGlowing(ArmorStand, boolean)`**
  Applies glowing and fires `ArmorStandGlowEvent`.

* **`teleportArmorStand(ArmorStand, Location)`**
  Teleports the entity.

* **`setCustomName(ArmorStand, String)`**
  Formats with `DreamMessageFormatter` ➜ assigns as Adventure `Component`.

* **`cloneArmorStand(ArmorStand original)`**
  Spawns a new stand with the **same core properties** via `DreamfireArmorStandBuilder`. (Note: equipment/poses are not copied here; see suggestions if you want that.)

**Validation:** All helpers throw `IllegalArgumentException` for null inputs.

---

### DreamfireArmorStandBuilder

Spawns and configures a new `ArmorStand`.

```java
ArmorStand stand = new DreamfireArmorStandBuilder(loc)
    .visible(true)
    .customNameVisible(true)
    .customName("&eQuest NPC")
    .arms(true)
    .basePlate(false)
    .small(false)
    .marker(false)
    .gravity(true)
    .glowing(false)
    .build();
```

* Validates `location` and its `world`.
* Applies all configured flags.
* Uses `DreamMessageFormatter` for `customName` and converts to `Component`.
* Fires `ArmorStandSpawnEvent` after spawning.

**Notes:**

* If you plan to equip or pose immediately after spawn, do it **after** `build()`; you can also listen for `ArmorStandSpawnEvent`.

---

## Animator — Smooth Pose Interpolation

### DreamArmorStandAnimator

Drives pose interpolation between `ArmorStandAnimationFrameData` frames over time. You advance it by calling `displayNextFrame()` on a timer.

**State:**

* `UUID animatorID` — optional external identifier.
* `ArmorStand targetArmorStand` — the entity to animate.
* `List<ArmorStandAnimationFrameData> frames` — sequence.
* `boolean paused` — starts `true` until `play()`.

**Core API:**

* `boolean displayNextFrame()`

    * Returns `true` when **no frames** or **no target** (use to stop your scheduler).
    * When running, calculates `t = currentTick / durationTicks` ∈ \[0,1], linearly interpolates each limb with non‑null start/end, applies poses, and advances tick/frame.
* `void pause()` / `void play()` — toggle animation.
* `void stop()` — pauses and **clears frames** (animator becomes inert).

**Builder:**

```java
DreamArmorStandAnimator animator = new DreamArmorStandAnimator.Builder()
    .animatorID(UUID.randomUUID())
    .targetArmorStand(stand)
    .addFrame(frameA)
    .addFrame(frameB, 2) // repeat frameB twice
    .build();
```

* Validates `targetArmorStand` and non‑empty `frames`.

**Scheduling patterns:**

```java
// Every tick
Bukkit.getScheduler().runTaskTimer(plugin, animator::displayNextFrame, 0L, 1L);

// Slower (every 5 ticks)
Bukkit.getScheduler().runTaskTimer(plugin, animator::displayNextFrame, 0L, 5L);
```

**Pitfalls:**

* **Forgetting play():** Animator starts paused; call `play()` or just start ticking (the code applies only when not paused).
* **Long frames, small movement:** If start==end or t increments are tiny (long duration), change may be imperceptible.
* **Radians vs degrees:** Frame data uses `EulerAngle` (radians); `setPose` helper accepts degrees and converts for you.

---

## Practical Recipes

### Wave Animation (right arm)

```java
ArmorStandAnimationFrameData a = new ArmorStandAnimationFrameDataBuilder()
    .duration(10)
    .rightArm(new EulerAngle(0,0,0), new EulerAngle(0, Math.toRadians(40), 0))
    .build();
ArmorStandAnimationFrameData b = new ArmorStandAnimationFrameDataBuilder()
    .duration(10)
    .rightArm(new EulerAngle(0, Math.toRadians(40), 0), new EulerAngle(0,0,0))
    .build();
DreamArmorStandAnimator wave = new DreamArmorStandAnimator.Builder()
    .targetArmorStand(stand)
    .addFrame(a)
    .addFrame(b)
    .build();
wave.play();
```

### Spotlight NPC (glow toggle + name)

```java
DreamArmorStand.setCustomName(stand, "&6&lQuest Giver");
DreamArmorStand.setGlowing(stand, true);
```

### Equip Showpiece

```java
DreamArmorStand.equipArmorStand(stand, new ItemStack(Material.NETHERITE_SWORD), ArmorStandSlot.HAND);
DreamArmorStand.equipArmorStand(stand, new ItemStack(Material.NETHERITE_HELMET), ArmorStandSlot.HEAD);
```

---

## Troubleshooting & Checklist

* [ ] `IllegalArgumentException: durationTicks must be > 0` → fix builder `duration`.
* [ ] Animator never moves → call `play()` or ensure scheduler runs; verify frame start/end differ.
* [ ] Poses look wrong → confirm radians in `ArmorStandAnimationFrameData` and degrees for `setPose` helper.
* [ ] Name not colored → ensure `DreamMessageFormatter` supports your color codes and the audience has permission/visibility.
* [ ] Cloning didn’t copy equipment/poses → expected; see suggestions below.

---

## Suggestions (for your approval — no behavior changes made)

1. **Fix class name typo:** Rename `ArmorStandEqippedEvent` → `ArmorStandEquippedEvent` (and constructor) to improve clarity and IDE searchability.
2. **Event thread consistency:** Some events call `Bukkit.getPluginManager().callEvent(this)` directly; others use scheduler in other packages. Consider standardizing (e.g., always dispatch on main thread via `runTask`) for consistency.
3. **Clone enhancement:** Add optional copy of **equipment and all Euler poses** in `cloneArmorStand` (gated by a boolean flag or `CloneOptions` enum).
4. **Animator easing:** Add easing functions (e.g., cubic in/out) via `BiFunction<Double, Double, Double>` or strategy interface; default to linear.
5. **Animator completion callbacks:** Fire a `ArmorStandAnimationCompletedEvent` when the sequence loops or ends (configurable loop/once behavior).
6. **Null‑safety for names:** `cloneArmorStand` serializes `original.customName()` with `Objects.requireNonNull`; consider tolerating null and defaulting to empty string.
7. **Immutable frames:** Copy `frames` into an unmodifiable list upon `build()` to prevent external mutation.

If you’d like, I can draft these changes as separate PR‑ready diffs with doc comments and unit‑style tests.

---

## API Index

**ArmorStandAnimationFrameData**

* `getDurationTicks()`
* `getHeadStart()/getHeadEnd()` … `getRightLegStart()/getRightLegEnd()`

**ArmorStandAnimationFrameDataBuilder**

* `duration(long)`
* `head/body/leftArm/rightArm/leftLeg/rightLeg(EulerAngle,EulerAngle)`
* `build()`

**ArmorStandAnimatorData**

* `displayFrame(ArmorStand)`

**DreamArmorStand**

* `equipArmorStand(ArmorStand, ItemStack, ArmorStandSlot)`
* `setPose(ArmorStand, ArmorStandPose, float, float, float)`
* `toggleVisibility(ArmorStand)`
* `disableGravity(ArmorStand)` / `enableGravity(ArmorStand)`
* `setGlowing(ArmorStand, boolean)`
* `teleportArmorStand(ArmorStand, Location)`
* `setCustomName(ArmorStand, String)`
* `cloneArmorStand(ArmorStand)`

**DreamArmorStandAnimator**

* `displayNextFrame()`
* `pause()` / `play()` / `stop()`
* `Builder.animatorID(UUID)` / `targetArmorStand(ArmorStand)` / `addFrame(frame)` / `addFrame(frame,int)` / `build()`

**Events**

* `ArmorStandEqippedEvent` *(suggest: Equipped)*
* `ArmorStandGlowEvent`
* `ArmorStandPosedEvent`
* `ArmorStandSpawnEvent`

**Enums**

* `ArmorStandPose`
* `ArmorStandSlot`

---
