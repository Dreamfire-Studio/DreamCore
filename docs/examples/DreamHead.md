# DreamHead — Developer Guide with Examples

The **DreamHead** utility simplifies working with player heads in Minecraft. It supports fetching heads by player, UUID, or custom textures, and provides helper methods for applying names or verifying items. Heads are often used in GUIs, cosmetic systems, leaderboards, or rewards.

This guide explains each function in detail for developers new to the system.

---

## ⚙️ Core Functions

### `returnPlayerHead(OfflinePlayer player)`

Creates a head item for a given offline player.

* Returns an `ItemStack` with the correct skin.
* If the player is `null`, returns `null`.

**Example:**

```java
ItemStack head = DreamHead.returnPlayerHead(Bukkit.getOfflinePlayer("Notch"));
```

> ✅ Use this when you already have an `OfflinePlayer` reference.

---

### `returnPlayerHead(String uuid)`

Creates a head item using a player’s UUID string.

* If the string is not a valid UUID, returns `null`.
* Uses `OfflinePlayer` lookup internally.

**Example:**

```java
ItemStack head = DreamHead.returnPlayerHead("550e8400-e29b-41d4-a716-446655440000");
```

> ✅ Use this if you only have a UUID string instead of a player object.

---

### `returnPlayerHead(String name, int amount, String url)`

Creates a custom player head with a display name, item stack amount, and either:

* A **player name** (if string length < 16).
* A **custom texture hash** from Mojang’s texture servers.

If using a custom texture:

* A `GameProfile` is created.
* Texture data is encoded and applied to the skull’s metadata.

**Example:**

```java
ItemStack head = DreamHead.returnPlayerHead("Epic Head", 1, "a1b2c3d4e5f6...");
```

> ✅ Use this to generate heads for GUIs, cosmetics, or custom rewards.

---

### `returnCustomTextureHead(String url)`

Creates a custom player head purely from a texture hash or URL.

* Convenience wrapper for `returnPlayerHead("", 1, url)`.

**Example:**

```java
ItemStack head = DreamHead.returnCustomTextureHead("a1b2c3d4e5f6...");
```

> ✅ Use this if you only care about the skin and not the name or amount.

---

### `applyCustomName(ItemStack skull, String name)`

Applies a custom display name to an existing skull item.

* Uses Kyori Adventure `Component.text(name)` for proper formatting.

**Example:**

```java
ItemStack head = DreamHead.applyCustomName(existingHead, "Legendary Head");
```

> ✅ Use this to make GUI labels or cosmetic items more descriptive.

---

### `isPlayerHead(ItemStack item)`

Checks if an item is a player head.

* Returns `true` if the type is `Material.PLAYER_HEAD`.

**Example:**

```java
boolean isHead = DreamHead.isPlayerHead(item);
```

> ✅ Use this to validate items in inventories or crafting recipes.

---

## 🧩 Internal Helpers

### `isUUID(String s)`

Private helper to validate whether a string can be parsed as a `UUID`.

* Used internally in `returnPlayerHead(String uuid)`.

---

## 💡 Suggestions for Future Improvements

* Add **caching** for texture profiles to avoid repeated reflection calls.
* Provide async methods to fetch player heads for online players.
* Add a utility for **base64 → texture hash conversion** for more flexibility.
* Expose `isUUID` as public if developers want quick validation elsewhere.
* Allow applying **lore text** in the same utility methods.
* Consider wrapping head creation in a builder (`DreamHeadBuilder`) for clarity.