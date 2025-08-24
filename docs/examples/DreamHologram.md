# DreamHologram — Developer Guide with Examples

The **DreamHologram** system provides a flexible way to create, edit, and manage floating multi-line holograms in Minecraft using stacked, invisible ArmorStands. Each line is represented by an Adventure `Component`, ensuring modern text formatting support.

This guide explains each function in detail for new developers.

---

## 📦 Core Class: DreamHologram

### `isArmorStand(ArmorStand armorStand)`

Checks whether a given ArmorStand belongs to this hologram.

* Returns `true` if owned, otherwise `false`.

> ✅ Use this to filter out only hologram entities when listening to entity events.

---

### `size()`

Returns the current number of lines in the hologram.

**Example:**

```java
int lines = hologram.size();
```

> ✅ Use this when iterating over lines or validating input indexes.

---

### `line(int index)`

Gets the text component for a specific line.

* Returns `null` if the index is out of bounds.

**Example:**

```java
Component topLine = hologram.line(0);
```

> ✅ Use this to read back line contents.

---

### `addNewLine(int index)`

Inserts a new line at the given index.

* Spawns a new invisible ArmorStand at the correct Y-offset.
* Shifts lines beneath downwards.
* Fires `HologramAddLineEvent` and `HologramUpdateEvent`.

**Example:**

```java
hologram.addNewLine(0); // Insert new top line
```

> ✅ Use this for dynamically adding lines to GUIs or live displays.

---

### `editLine(int index)`

Updates the text of an existing line.

* Re-applies the line generator to update content.
* Fires `HologramEditLineEvent`.

**Example:**

```java
hologram.editLine(1); // Refresh the 2nd line’s text
```

> ✅ Useful for live scoreboards, timers, or animations.

---

### `removeLine(int index)`

Removes a line at the specified index.

* Deletes the ArmorStand.
* Re-stacks remaining lines to close the gap.
* Fires `HologramRemoveLineEvent` and `HologramUpdateEvent`.

**Example:**

```java
hologram.removeLine(2);
```

> ✅ Use this when removing outdated information.

---

### `updateHologram()`

Re-teleports all lines to their correct positions and re-applies flags.

* Fires `HologramUpdateEvent`.

**Example:**

```java
hologram.updateHologram();
```

> ✅ Use this after moving a hologram or adjusting line spacing.

---

### `displayNextFrame()`

Forces all lines to re-generate their text.

* Useful for animations or dynamically updating content.

**Example:**

```java
hologram.displayNextFrame();
```

> ✅ Use in scheduled tasks for animated holograms.

---

### `deleteHologram()`

Deletes the hologram entirely.

* Removes all ArmorStands.
* Clears internal list.
* Unregisters from `DreamCore.DreamHolograms`.
* Fires `HologramDeleteEvent`.

**Example:**

```java
hologram.deleteHologram();
```

> ✅ Use this for cleanup when unloading plugins or worlds.

---

### Builder: `DreamHologram.HologramBuilder`

The builder provides a fluent API to create holograms.

**Options:**

* `hologramName(String name)` — Set a custom identifier.
* `lines(int linesToAdd)` — Number of lines to spawn initially.
* `visible(boolean visible)` — Whether stands themselves are visible.
* `customNameVisible(boolean visible)` — Show/hide text.
* `useGravity(boolean gravity)` — Enable/disable gravity.
* `gapBetweenLines(float gap)` — Adjust vertical spacing.

**Create Example:**

```java
DreamHologram hologram = new DreamHologram.HologramBuilder()
        .hologramName("Scoreboard")
        .lines(3)
        .gapBetweenLines(-0.3f)
        .create(location, i -> Component.text("Line " + i));
```

> ✅ Use the builder for consistent setup and automatic registration.

---

## 🎯 Events

All events extend Bukkit’s `Event` class and are fired synchronously unless otherwise noted.

### `HologramAddLineEvent`

* Fired after inserting a new line.
* Includes `hologram` and `line`.

### `HologramEditLineEvent`

* Fired after editing an existing line.
* Includes `hologram`, `index`, and `line`.

### `HologramRemoveLineEvent`

* Fired after removing a line.
* Includes `hologram` and `index`.

### `HologramUpdateEvent`

* Fired after a hologram has been restacked or globally updated.

### `HologramDeleteEvent`

* Fired after a hologram is deleted.
* Includes `hologram`.

### `HologramSpawnEvent`

* Fired after initial creation via the builder.
* Includes `hologram`.

---

## 💡 Suggestions for Future Improvements

* Add async-safe creation for heavy hologram setups.
* Support per-line metadata or click actions.
* Add persistence (save holograms to disk or config).
* Add batch update API to modify multiple lines at once.
* Consider optimizing re-stack logic to skip unchanged lines.
* Provide helper methods for animations (scrolling, fading, cycling).