# DreamPersistentData — Developer Guide

The **DreamPersistentData** utilities make it easy to work with Bukkit’s `PersistentDataContainer` system. Instead of manually handling `NamespacedKey` and container lookups, this system gives you type-safe helpers for **Blocks**, **Entities**, and **ItemStacks**, plus **events** to listen for changes.

---

## 📦 Overview

* **DreamPersistentBlock** → Persistent data on tile entities (`Chest`, `Furnace`, etc.).
* **DreamPersistentEntity** → Persistent data on entities (`Player`, `Zombie`, etc.).
* **DreamPersistentItemStack** → Persistent data on item metadata.
* **Events** → Fired whenever keys are added or removed.
* **PersistentDataTypes** → Enum wrapper around Bukkit’s built-in types.

All helpers:

* Validate keys before use.
* Support typed `Get`, `Has`, `Add`, and `Remove`.
* Provide `CloneData` utilities.
* Support `AddExpiring` entries (auto-remove after delay).

---

## 🔑 Key Validation

### `isValidKey(String key)`

Ensures keys match Bukkit’s `NamespacedKey` format (`[a-z0-9/._-]{1,256}`).

```java
boolean ok = DreamPersistentBlock.isValidKey("upgrade_level");
```

Always validate keys before persisting data.

---

## 🧱 DreamPersistentBlock (Tile Entities)

### Get Container

```java
PersistentDataContainer container = DreamPersistentBlock.ReturnPersistentDataContainer(chestBlock);
```

Throws `IllegalArgumentException` if block is not a `TileState`.

### Get Data

```java
Integer level = DreamPersistentBlock.Get(plugin, chest, "upgrade_level", PersistentDataType.INTEGER);
```

### Has Data

```java
boolean exists = DreamPersistentBlock.Has(plugin, chest, PersistentDataType.STRING, "owner");
```

### Add Data

```java
DreamPersistentBlock.Add(plugin, chest, PersistentDataType.STRING, "owner", player.getName());
```

Triggers `PersistentBlockAddedEvent`.

### Remove Data

```java
DreamPersistentBlock.Remove(plugin, chest, "owner");
```

Triggers `PersistentBlockRemovedEvent`.

### Clone Data

```java
DreamPersistentBlock.CloneData(sourceChest, targetChest);
```

### Add Expiring Data

```java
DreamPersistentBlock.AddExpiring(plugin, chest, PersistentDataType.BOOLEAN, "glowing", true, 5000);
```

Automatically removes key after 5 seconds.

---

## 🧍 DreamPersistentEntity (Entities)

### Get Container

```java
PersistentDataContainer container = DreamPersistentEntity.ReturnPersistentDataContainer(zombie);
```

### Get Data

```java
String tag = DreamPersistentEntity.Get(zombie, "owner", PersistentDataType.STRING);
```

### Has Data

```java
boolean tagged = DreamPersistentEntity.Has(plugin, zombie, PersistentDataType.STRING, "owner");
```

### Add Data

```java
DreamPersistentEntity.Add(plugin, zombie, PersistentDataType.INTEGER, "charges", 3);
```

Fires `PersistentEntityAddedEvent`.

### Remove Data

```java
DreamPersistentEntity.Remove(plugin, zombie, "charges");
```

Fires `PersistentEntityRemovedEvent`.

### Clone Data

```java
DreamPersistentEntity.CloneData(zombie, husk);
```

### Add Expiring Data

```java
DreamPersistentEntity.AddExpiring(plugin, zombie, PersistentDataType.BOOLEAN, "temporary", true, 10000);
```

Removes after 10 seconds.

---

## 🪙 DreamPersistentItemStack (Item Metadata)

### Get Container

```java
PersistentDataContainer container = DreamPersistentItemStack.ReturnPersistentDataContainer(stack);
```

### Get Data

```java
Integer level = DreamPersistentItemStack.Get(plugin, sword, "level", PersistentDataType.INTEGER);
```

### Has Data

```java
boolean hasTag = DreamPersistentItemStack.Has(plugin, sword, PersistentDataType.STRING, "rarity");
```

### Add Data

```java
DreamPersistentItemStack.Add(plugin, sword, PersistentDataType.STRING, "rarity", "legendary");
```

Fires `PersistentItemStackAddedEvent`.

### Remove Data

```java
DreamPersistentItemStack.Remove(plugin, sword, "rarity");
```

Fires `PersistentItemStackRemovedEvent`.

### Clone Data

```java
DreamPersistentItemStack.CloneData(templateItem, resultItem);
```

### Add Expiring Data

```java
DreamPersistentItemStack.AddExpiring(plugin, sword, PersistentDataType.INTEGER, "temp_uses", 1, 2000);
```

Removes key after 2 seconds.

---

## 📢 Events

Each `Add`/`Remove` call fires an event immediately:

* **Blocks**

    * `PersistentBlockAddedEvent`
    * `PersistentBlockRemovedEvent`
* **Entities**

    * `PersistentEntityAddedEvent`
    * `PersistentEntityRemovedEvent`
* **ItemStacks**

    * `PersistentItemStackAddedEvent`
    * `PersistentItemStackRemovedEvent`

### Example Listener

```java
@EventHandler
public void onPersistentBlockAdded(PersistentBlockAddedEvent e) {
    getLogger().info("Added key " + e.getNamespacedKey() + " to block at " + e.getBlock().getLocation());
}
```

---

## 🗂️ PersistentDataTypes Enum

Wrapper around Bukkit’s types for grouped dumping.

```java
for (PersistentDataTypes type : PersistentDataTypes.values()) {
    getLogger().info("Type: " + type.name());
}
```

---

## ✅ Summary

DreamPersistentData provides:

* Safe, validated persistent data APIs.
* Support for **Blocks**, **Entities**, and **ItemStacks**.
* `CloneData` and `AddExpiring` utilities.
* Events for monitoring changes.

---

## 💡 Suggestions for Future Changes

* Add **asynchronous safe wrappers** for bulk operations.
* Provide **JSON (Gson/fastjson) serialization** helpers for complex objects.
* Add **namespacing utilities** to auto-prefix keys by plugin.
* Add **query utilities** (e.g., list all keys on entity).
* Support **versioned data migration** similar to DreamConfig.