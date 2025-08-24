# DreamItemStacks & IDreamItemStack — Developer Guide with Examples

The **DreamItems** system provides a safe and stable way to build, identify, compare, and count custom items in Minecraft. It uses Persistent Data Containers (PDC) under the hood to ensure items retain their identity across restarts and sessions.

This guide is written for developers new to the system.

---

## 📦 Core Concepts

* **`IDreamItemStack`** → Your custom item definitions implement this interface.
* **`DreamItemStacks`** → Helper class for building, resolving, and comparing custom items.
* **Persistent Data Container (PDC)** → Used to embed a stable item ID inside the item’s metadata.

---

## 🔑 `DreamItemStacks.keyId(Plugin plugin)`

Generates the namespaced key used to store the optional custom item ID in the item’s PDC.

**Example:**

```java
NamespacedKey key = DreamItemStacks.keyId(plugin);
```

---

## 🛠️ Building Items

### `DreamItemStacks.build(Plugin plugin, IDreamItemStack def)`

Creates a new `ItemStack` from a definition and writes its ID (if any) into the PDC.

* Applies: name, lore, model data, unbreakable flag, item flags, attributes, PDC data, and enchantments.
* Stack amount is clamped to at least `1`.

**Example:**

```java
ItemStack wand = DreamItemStacks.build(plugin, new MagicWandDef());
```

---

## 📖 Reading & Resolving IDs

### `DreamItemStacks.readId(Plugin plugin, ItemStack stack)`

Reads the stored ID from an item’s PDC.

**Example:**

```java
Optional<String> id = DreamItemStacks.readId(plugin, stack);
```

---

### `DreamItemStacks.resolveById(Plugin plugin, ItemStack stack, Function<String, IDreamItemStack> lookup)`

Resolves an item definition by using a registry lookup function.

**Example:**

```java
Optional<IDreamItemStack> def = DreamItemStacks.resolveById(plugin, stack, id -> REGISTRY.get(id));
```

---

### `DreamItemStacks.resolveById(Plugin plugin, ItemStack stack, Collection<IDreamItemStack> registry)`

Resolves a definition by scanning a collection for a matching ID.

**Example:**

```java
Optional<IDreamItemStack> def = DreamItemStacks.resolveById(plugin, stack, ALL_DEFS);
```

---

## ⚖️ Equality & Counting

### `DreamItemStacks.isSame(Plugin plugin, ItemStack a, ItemStack b)`

Checks if two items are the same.

1. If both have PDC IDs → compares IDs.
2. Otherwise → falls back to `ItemStack#isSimilar`.

**Example:**

```java
boolean same = DreamItemStacks.isSame(plugin, a, b);
```

---

### `DreamItemStacks.count(Plugin plugin, Inventory inv, ItemStack probe)`

Counts how many items in the inventory match the probe (by ID or similarity).

**Example:**

```java
int apples = DreamItemStacks.count(plugin, player.getInventory(), new ItemStack(Material.APPLE));
```

---

### `DreamItemStacks.count(Inventory inv, Material material)`

Counts how many of a specific material are in the inventory.

**Example:**

```java
int cobble = DreamItemStacks.count(player.getInventory(), Material.COBBLESTONE);
```

---

## 🧾 Defining Items with `IDreamItemStack`

The `IDreamItemStack` interface is used to define custom items. Override only what you need.

### Key Methods

* **`id()`** → Stable optional ID for matching across restarts.
* **`displayName()`** → Custom Adventure `Component` name.
* **`type()`** → Base material.
* **`amount()`** → Default stack size.
* **`customModelData()`** → Optional model override.
* **`unbreakable()`** → Marks as unbreakable.
* **`lore()`** → Lore lines.
* **`flags()`** → Item flags (hide attributes, etc).
* **`enchantments()`** → Enchantments.
* **`attributeModifiers()`** → Attribute modifiers.
* **`writePdc()`** → Custom PDC data.

### Example Definition

```java
public final class HealingPotion implements IDreamItemStack {
    public Optional<String> id() { return Optional.of("healing_potion_t1"); }
    public Material type() { return Material.POTION; }
    public Component displayName() { return Component.text("§dLesser Healing"); }
    public int amount() { return 1; }
    public Map<Enchantment, Integer> enchantments() { return Map.of(Enchantment.MENDING, 1); }
    public void writePdc(Plugin plugin, PersistentDataContainer pdc) {
        pdc.set(new NamespacedKey(plugin, "heal"), PersistentDataType.INTEGER, 6);
    }
}
```

---

## 💡 Suggestions for Future Improvements

* Add a **pre-built registry manager** to centralize all custom item definitions.
* Provide a **`preSpawn` event** for items similar to entity events.
* Add helpers for **recipe registration** linked to `IDreamItemStack`.
* Consider making `isSame` configurable (strict mode vs loose similarity).
* Add caching for `resolveById(Collection)` for faster lookups in large registries.