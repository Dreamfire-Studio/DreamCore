# DreamInventory — Developer Guide with Examples

The **DreamInventory** utility provides safe, Paper-friendly ways to work with inventories, items, and equipment. Instead of directly manipulating raw `ItemStack` arrays, it introduces the `ItemRef` record and `ItemLocation` enum to make inventory scanning and editing more reliable.

This guide explains each function in detail for developers new to the system.

---

## 📦 Core Class: DreamInventory

### `scan(LivingEntity entity)`

Scans a living entity’s **armor and hands** and returns a list of `ItemRef`s.

* Includes all worn armor.
* Includes both hands (main and off).
* Returns an empty list if the entity is `null` or has no equipment.

**Example:**

```java
List<ItemRef> equipped = DreamInventory.scan(zombie);
```

> ✅ Use this for mobs, NPCs, or players when you only care about gear, not backpacks.

---

### `scan(PlayerInventory inv)`

Scans a **full player inventory** (backpack, armor, hands).

* Backpack slots follow `PlayerInventory#getContents()` order.
* Armor slots included.
* Both hands included.

**Example:**

```java
List<ItemRef> allItems = DreamInventory.scan(player.getInventory());
```

> ✅ Use this to gather every item a player has.

---

### `totalCount(PlayerInventory inv, ItemStack probe)`

Counts how many total items match the `probe` in the player’s inventory.

* Uses `ItemStack#isSimilar` to compare.

**Example:**

```java
int arrows = DreamInventory.totalCount(player.getInventory(), new ItemStack(Material.ARROW));
```

> ✅ Use this to count resources or check quest requirements.

---

### `isBroken(ItemStack item)`

Checks whether an item is fully broken (at max durability).

* Returns `true` if the item’s damage ≥ max durability.
* Non-damageable items always return `false`.

**Example:**

```java
if (DreamInventory.isBroken(sword)) {
    player.sendMessage("Your sword is broken!");
}
```

> ✅ Use this for durability-based mechanics.

---

### `findByDisplayName(PlayerInventory inv, String plainName)`

Finds the first item whose display name matches the given plain string.

* Case-insensitive.
* Uses Adventure `PlainTextComponentSerializer` for comparison.

**Example:**

```java
ItemStack key = DreamInventory.findByDisplayName(player.getInventory(), "Dungeon Key");
```

> ✅ Use this for custom items with unique names.

---

### `findByDisplayName(PlayerInventory inv, Component nameComponent)`

Same as above, but takes a `Component` instead of plain text.

**Example:**

```java
ItemStack key = DreamInventory.findByDisplayName(player.getInventory(), Component.text("Dungeon Key"));
```

---

### `hasAtLeast(PlayerInventory inv, ItemStack probe, int amount)`

Checks if the inventory has at least `amount` of the probe item.

**Example:**

```java
if (DreamInventory.hasAtLeast(player.getInventory(), new ItemStack(Material.DIAMOND), 10)) {
    // Player has enough diamonds
}
```

---

### `removeAmount(PlayerInventory inv, ItemStack probe, int amount)`

Removes up to `amount` of the probe item.

* Returns the actual number removed.
* Works across multiple stacks.

**Example:**

```java
int removed = DreamInventory.removeAmount(player.getInventory(), new ItemStack(Material.GOLD_INGOT), 5);
```

---

### `removeAllSimilar(PlayerInventory inv, ItemStack probe)`

Removes all stacks that are similar to the probe.

* Returns the total number of items removed.

**Example:**

```java
int cleared = DreamInventory.removeAllSimilar(player.getInventory(), new ItemStack(Material.ROTTEN_FLESH));
```

---

### `addItem(PlayerInventory inv, ItemStack stack)`

Attempts to add a stack to the player’s inventory.

* Returns `true` if fully added.
* Returns `false` if partial or rejected.

**Example:**

```java
boolean success = DreamInventory.addItem(player.getInventory(), new ItemStack(Material.EMERALD, 32));
```

---

### `swap(Inventory inv, int slot1, int slot2)`

Swaps two slots in an inventory.

* Throws `IllegalArgumentException` if indices are invalid.

**Example:**

```java
DreamInventory.swap(player.getInventory(), 0, 8);
```

> ✅ Use this for GUI systems or quick inventory rearrangements.

---

### `ensureMainHand(Player player, ItemStack stack)`

Ensures the player’s main hand contains the given stack.

* Only updates if the current item is not similar.

**Example:**

```java
DreamInventory.ensureMainHand(player, new ItemStack(Material.TORCH));
```

---

### `findFirst(PlayerInventory inv, Predicate<ItemStack> predicate)`

Finds the first item matching a custom condition.

* Returns `null` if none found.

**Example:**

```java
ItemStack pickaxe = DreamInventory.findFirst(player.getInventory(),
    s -> s.getType() == Material.DIAMOND_PICKAXE);
```

> ✅ Use this for flexible searches beyond names or similarity.

---

## 🧩 Supporting Types

### `ItemRef`

Immutable record storing:

* `location`: Logical location (`ItemLocation`).
* `slot`: Slot index.
* `stack`: The item itself.

### `ItemLocation`

Enum that categorizes where the item came from:

* `ENTITY_MAIN_HAND`
* `ENTITY_OFF_HAND`
* `ENTITY_ARMOR`
* `ENTITY_INVENTORY`
* `BROKEN_ITEM`
* `CONTAINER` *(future)*
* `DROPPED_ITEM` *(future)*

---

## 💡 Suggestions for Future Improvements

* Add `scan(Inventory)` for chest and container support.
* Provide async-safe bulk operations for large playerbases.
* Add lore-based search helpers (`findByLore`).
* Introduce a fluent API for chained inventory queries (e.g., `DreamInventory.query().whereType(Material.DIAMOND).count()`).
* Consider adding event hooks (e.g., before/after remove).