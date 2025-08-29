# DreamKeyPressed System Documentation

## Overview

The **DreamKeyPressed** system provides an abstraction layer for detecting complex player key inputs in Bukkit/Spigot-based Minecraft servers. It normalizes raw Bukkit events into a consistent set of **`DreamPressedKeys`** and evaluates them against defined **patterns** (sequences, chords, or all-at-once presses).

This enables plugin developers to design advanced input combos such as double-taps, held chords, or action-based triggers that are portable and easy to extend.

---

## Core Concepts

### 1. Pressed Keys (`DreamPressedKeys`)

Represents normalized input actions:

* Movement: `SNEAK`, `SPRINT`
* Inventory: `DROP_ITEM`, `SWAP_HANDS`, `INVENTORY_OPEN`
* Hotbar selection: `HOTBAR_1` .. `HOTBAR_9`
* Clicks: `LEFT_CLICK`, `RIGHT_CLICK`

### 2. Step Types (`DreamStepKind`)

* `SINGLE` – One key input (e.g., `SNEAK`).
* `CHORD` – Multiple keys within a spread window (e.g., `LEFT_CLICK + RIGHT_CLICK`).

### 3. Pattern Types (`DreamPressedType`)

* `InOrder` – Keys pressed sequentially with optional timing.
* `AllAtOnce` – All required keys pressed together within a spread window.

### 4. Timing (`IDreamTimingSpec`)

Constraints for when a step can occur:

* `minDelayFromPrevious` – Minimum wait before pressing.
* `maxDelayFromPrevious` – Maximum time allowed.
* `requiredHold` – How long the key must be held.
* `maxChordSpread` – Max window for chord detection.

Use `DreamTimingDefault` for building specs.

### 5. Conditions (`IDreamConditionsSpec`)

Additional restrictions for a step or pattern:

* Location (`DreamLocationConstraint`): `ON_GROUND`, `IN_AIR`, `IN_WATER`
* Posture: sneaking/sprinting requirements.
* Hand conditions (`IDreamHandCondition`): specific materials required/banned.

### 6. Pattern Spec (`IDreamKeyPatternSpec`)

Defines an entire key combo pattern:

* Step list (`steps`)
* Type (`pressedType`)
* Timeout window (`totalTimeout`)
* Failure/reset behavior
* One-shot vs. reusable
* Cooldown

### 7. Manager (`IDreamKeyManager`)

Evaluates patterns against input streams. Default implementation is `DreamKeyManager`.

Responsibilities:

* Register/unregister patterns per player.
* Handle incoming inputs.
* Manage cooldowns, timeouts, and re-arming.

### 8. Listener (`IDreamKeyPressed`)

Callbacks invoked on progress:

* `PartialComplete(int index)`
* `FailedAction()`
* `ActionComplete()`
* `TimeWindowUpdate(UUID, Duration remaining, Duration total, int stepIndex)`
* `OnCooldown(UUID, Duration remaining)`

---

## Usage Examples

### Example 1 – In-Order Combo

```java
IDreamKeyPatternSpec dashPattern = DreamKeyPatternBuilder.create()
    .inOrder()
    .stepWithTiming(DreamPressedKeys.SNEAK, DreamTimingDefault.window(null, Duration.ofMillis(250)))
    .stepWithTiming(DreamPressedKeys.LEFT_CLICK, DreamTimingDefault.window(null, Duration.ofMillis(250)))
    .chordWithTiming(Set.of(DreamPressedKeys.RIGHT_CLICK, DreamPressedKeys.SWAP_HANDS),
                     DreamTimingDefault.chordSpread(Duration.ofMillis(150)))
    .totalTimeout(Duration.ofSeconds(4))
    .firstTimeOnly(false)
    .resetOnFailure(true)
    .build();
```

### Example 2 – All-at-Once Trigger

```java
IDreamKeyPatternSpec blinkPattern = DreamKeyPatternBuilder.create()
    .allAtOnce()
    .chord(DreamPressedKeys.SNEAK, DreamPressedKeys.HOTBAR_1, DreamPressedKeys.SWAP_HANDS)
    .firstTimeOnly(true)
    .build();
```

### Example 3 – Listener

```java
class DashListener implements IDreamKeyPressed {
    @Override public void ActionComplete() {
        // Apply dash effect
    }

    @Override public void PartialComplete(int stepIdx) {
        // Show visual indicator for progress
    }

    @Override public void FailedAction() {
        // Play failure sound
    }

    @Override public void TimeWindowUpdate(UUID pid, Duration remaining, Duration total, int stepIdx) {
        // Update HUD with countdown
    }

    @Override public void OnCooldown(UUID pid, Duration remaining) {
        // Notify player of cooldown
    }
}
```

### Example 4 – Full Binding Class

```java
@PulseAutoRegister
public final class SneakRightClickBinding implements IDreamKeyPatternSpec, IDreamKeyPressed {

    @Override public boolean worksInInventory() { return false; }
    @Override public DreamPressedType pressedType() { return DreamPressedType.AllAtOnce; }
    @Override public boolean firstTimeOnly() { return false; }
    @Override public Duration cooldown() { return Duration.ofSeconds(2); }

    @Override
    public List<IDreamKeyStepSpec> steps() {
        return List.of(
            DreamChordDefault.of(
                Set.of(DreamPressedKeys.SNEAK, DreamPressedKeys.RIGHT_CLICK),
                DreamTimingDefault.chordSpread(Duration.ofMillis(500))
            )
        );
    }

    @Override public void ActionComplete() { Bukkit.broadcastMessage("ActionComplete"); }
    @Override public void FailedAction()   { Bukkit.broadcastMessage("FailedComplete"); }

    @Override
    public void OnCooldown(UUID playerId, Duration remaining) {
        Bukkit.broadcastMessage("Cooldown remaining: " + remaining.toString());
    }
}
```

---

## Flow Diagram (Conceptual)

```
[Bukkit Event] → [DreamKeyBukkitAdapter] → [DreamPressedKeys]
     → [DreamKeyManager.handleInput()] → [Attempt Tracking]
         ├─ Timing/Conditions Check
         ├─ Step Progression
         ├─ Chord/All-at-Once Evaluation
         └─ Listener Callbacks (Partial, Complete, Fail, Cooldown)
```

---

## Quick Reference

* **Enums**: `DreamPressedKeys`, `DreamStepKind`, `DreamPressedType`, `DreamLocationConstraint`
* **Specs**: `IDreamKeyStepSpec`, `IDreamTimingSpec`, `IDreamConditionsSpec`, `IDreamHandCondition`
* **Patterns**: `IDreamKeyPatternSpec`, built with `DreamKeyPatternBuilder`
* **Manager**: `IDreamKeyManager` (default `DreamKeyManager`)
* **Listeners**: Implement `IDreamKeyPressed`

---

## Notes

* Default chord spread is \~100ms if not specified.
* One-shot patterns (`firstTimeOnly=true`) auto-unregister on success.
* Tick manager periodically (`manager.tick()`) for timeout handling.
* Works with Bukkit events but can be extended for other input adapters.