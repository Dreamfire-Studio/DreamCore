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

# DreamEnchantment — Beginner’s Guide & API

This guide explains how to use **DreamEnchantment** to create and manage custom enchantments in PaperMC. It covers registration, wrappers, item helpers, and implementation of custom enchantment logic.

---

## What DreamEnchantment Does

* Provides a **registry** for custom plugin enchantments.
* Wraps `IDreamEnchantment` into a Bukkit/Paper `Enchantment` object.
* Keeps both implementation and wrapper singletons.
* Includes utilities for applying enchantments to `ItemStack`s.
* Ensures compatibility with Paper 1.20.5+/1.21+ enchantment APIs.

---

## Quick Start

```java
// Define your enchantment implementation
public class FlameTouch implements IDreamEnchantment {
    private final NamespacedKey key = new NamespacedKey(MyPlugin.getInstance(), "flame_touch");

    @Override public @NotNull NamespacedKey getKey() { return key; }
    @Override public int getMaxLevel() { return 3; }
    @Override public int getStartLevel() { return 1; }
    @Override public boolean isTreasure() { return false; }
    @Override public boolean isCursed() { return false; }
    @Override public @NotNull Component displayName(int level) { return Component.text("Flame Touch " + level); }
    @Override public @NotNull Component description() { return Component.text("Sets blocks on fire when mined"); }
    // ... implement remaining methods
}

// Register in onEnable()
Enchantment wrapper = DreamEnchantmentRegistry.register(new FlameTouch());
```

---

## Core Classes

### **DreamEnchantmentRegistry**

Handles registration and lookup.

#### `register(IDreamEnchantment impl)`

Registers a new enchantment and returns its `Enchantment` wrapper.

* Returns existing wrapper if already registered.
* Guarantees singleton wrappers.

#### `getWrapper(NamespacedKey key)`

Returns the registered `Enchantment` wrapper, or `null` if not found.

#### `get(NamespacedKey key)`

Returns the registered `IDreamEnchantment` implementation.

#### `allWrappers()`

Returns an unmodifiable view of all wrappers.

#### `all()`

Returns an unmodifiable view of all implementations.

#### `findOn(ItemStack stack)`

Finds all Dreamfire enchantments on an item.

* Iterates through registered enchantments.
* Checks `itemMeta.hasEnchant(wrapper)`.
* Returns unmodifiable list of matching enchantments.

#### `add(ItemStack, NamespacedKey)`

Adds an enchantment by key to an item at level 1, ignoring level caps.

---

### **IDreamEnchantment**

Interface for custom enchantments. You must implement this.

Key categories:

* **Identity & i18n**

    * `getKey()` → stable key for registry.
    * `translationKey()` → modern translation key.
    * `getName()` / `getTranslationKey()` → legacy names.

* **Applicability**

    * `getSupportedItems()` → supported item types.
    * `getPrimaryItems()` → primary items (or null).
    * `getActiveSlotGroups()` → equipment slots.
    * `getItemTarget()` → legacy target.

* **Gameplay**

    * `getMaxLevel()` / `getStartLevel()`
    * `isTreasure()` / `isCursed()`
    * `isTradeable()` / `isDiscoverable()`
    * `getMinModifiedCost(int)` / `getMaxModifiedCost(int)`
    * `getAnvilCost()` / `getWeight()`
    * `getRarity()` (deprecated upstream)
    * `getDamageIncrease(level, category/type)`

* **Compatibility**

    * `conflictsWith(Enchantment other)`
    * `canEnchantItem(ItemStack item)`
    * `getExclusiveWith()` → mutually exclusive enchantments.

* **UI**

    * `displayName(level)` → Adventure component name.
    * `description()` → Adventure description.

* **Helpers**

    * `returnEnchantment()` → wraps into Bukkit/Paper `Enchantment`.
    * `addToItem(ItemStack, int, boolean)` → apply to an item.
    * `addToItem(ItemStack)` → shorthand for level 1.

---

### **DreamEnchantWrapper**

Wraps an `IDreamEnchantment` into a Bukkit/Paper `Enchantment`.

* Delegates all methods to the implementation.
* Ensures compatibility with Paper 1.20.5+/1.21+.
* Keeps legacy hooks (`getName`, `getItemTarget`, `getRarity`, `getDamageIncrease`).

---

## Usage Example

```java
// Register enchantment
enum MyPerms { ADMIN }

class DoubleDropEnch implements IDreamEnchantment {
    private final NamespacedKey key = new NamespacedKey(MyPlugin.getInstance(), "double_drop");
    @Override public @NotNull NamespacedKey getKey() { return key; }
    @Override public int getMaxLevel() { return 2; }
    @Override public int getStartLevel() { return 1; }
    @Override public @NotNull Component displayName(int level) { return Component.text("Double Drop " + level); }
    @Override public @NotNull Component description() { return Component.text("Chance to double mined drops"); }
    // ... implement other methods
}

Enchantment wrapper = DreamEnchantmentRegistry.register(new DoubleDropEnch());

// Apply to an item
ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
DreamEnchantmentRegistry.add(pickaxe, wrapper.getKey());
```

---

## Pitfalls & Notes

* **Wrapper creation** → `impl.returnEnchantment()` currently creates a new wrapper each call in `findOn`. This may be wasteful.
* **Not global** → Does not register enchantments into Mojang’s datapack registry.
* **Paper API volatility** → Some methods are marked deprecated but remain in Paper for now.

---

## Suggestions

1. Cache the wrapper per `IDreamEnchantment` to avoid repeated instantiations.
2. Add event hooks for when enchantments are applied or removed.
3. Add serialization helpers for saving enchantment data to items.
4. Extend `findOn` to include level lookups, not just existence.
5. Provide integration helpers for loot tables and crafting.