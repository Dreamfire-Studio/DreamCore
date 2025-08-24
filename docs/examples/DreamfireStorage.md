# DreamfireStorage — Developer Guide with Examples

The **DreamfireStorage** system is a lightweight, plugin-scoped storage manager for both global (server-wide) and per-player contexts. It allows you to attach temporary or runtime values to keys without needing to build your own persistence layer.

This guide walks through each function in detail, from the perspective of someone new to the system.

---

## 📦 DreamfireStorageManager

### `getData(Object key, UUID uuid)`

Retrieves the **storage wrapper** (`DreamfireStorageObject<T>`) for a given key.

* `key`: Any object, converted internally to a string.
* `uuid`: Player UUID. Pass `null` for server/global storage.
* Returns `DreamfireStorageObject<T>` or `null`.

> ✅ Use this if you want access to the wrapper itself (with metadata or immutability), not just the value.

---

### `getValue(Object key, UUID uuid)`

Retrieves the **stored value** directly (not the wrapper).

* Returns the value, or `null` if not present.

> ✅ Use this for quick lookups where you only need the raw value.

**Example:**

```java
Integer cooldown = manager.getValue("cooldown", player.getUniqueId());
```

---

### `storeData(Object key, DreamfireStorageObject<T> obj, UUID uuid)`

Stores a wrapped value under the given key.

* Fires a `StorageObjectAddedEvent` **before** insertion.
* If the event is cancelled, the object is not stored.
* Returns the same wrapper you passed in.

**Example:**

```java
manager.storeData("state", new DreamfireStorageObject<>("RUNNING"), null);
```

> ✅ Use this to register stateful runtime values. Events allow plugins to veto changes.

---

### `containsData(Object key, UUID uuid)`

Checks whether a given key exists in the namespace.

* Returns `true` if present, otherwise `false`.

**Example:**

```java
if (manager.containsData("cooldown", id)) {
    // Player has a cooldown
}
```

> ✅ Use this for guard checks before accessing data.

---

### `removeData(Object key, UUID uuid)`

Removes a stored object if present.

* Fires `StorageObjectRemovedEvent` **after** removal.
* Returns the removed object, or `null` if none was present.

**⚠️ Note:** Because the event is fired after removal, cancelling it does not restore the data.

**Example:**

```java
DreamfireStorageObject<?> removed = manager.removeData("state", null);
if (removed != null) getLogger().info("State removed");
```

> ✅ Use this when cleaning up temporary values.

---

## 📄 DreamfireStorageObject

A record wrapper around a single value.

* Provides immutability and simple access via `.storageData()`.

**Example:**

```java
DreamfireStorageObject<Integer> obj = new DreamfireStorageObject<>(42);
int val = obj.storageData(); // 42
```

> ✅ Use this wrapper whenever storing values in the manager.

---

## 🎯 Events

### `StorageObjectAddedEvent`

* Fired **before** a value is stored.
* **Cancellable** — cancelling prevents the store operation.
* Runs synchronously on the Bukkit main thread.

**Example:**

```java
@EventHandler
public void onAdd(StorageObjectAddedEvent e) {
    if (e.getStorageObject().storageData() instanceof Integer i && i < 0) {
        e.setCancelled(true); // Block negative numbers
    }
}
```

---

### `StorageObjectRemovedEvent`

* Fired **after** a value is removed.
* **Cancellable**, but does not restore the object (due to current design).

**Example:**

```java
@EventHandler
public void onRemoved(StorageObjectRemovedEvent e) {
    getLogger().info("Removed: " + e.getStorageObject());
}
```

---

## 💡 Suggestions for Future Improvements

* Consider changing removal logic: fire `StorageObjectRemovedEvent` **before** removal to allow proper cancellation.
* Add `clearAll(UUID uuid)` to reset a player’s entire storage namespace.
* Provide persistence hooks for saving/loading values to disk or database.
* Replace `Object key` → `String` conversion with a dedicated `StorageKey` type for type-safety.
* Add `onReplace` event to handle overwriting existing keys.
* Document thread-safety (currently assumes single-threaded Bukkit use).