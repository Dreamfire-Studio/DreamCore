# DreamTextDisplay — Developer Guide

The **DreamTextDisplay** system makes it easy to spawn and configure floating text entities (`TextDisplay`) in Minecraft. Instead of calling raw Bukkit APIs with lots of boilerplate, you can use the provided **builder pattern** to quickly define position, scale, colors, and styling, then spawn the entity.

---

## Why Use DreamTextDisplay?

* ✍️ **MiniMessage support** out of the box (via `DreamMessageFormatter`).
* ⚙️ Fluent **builder API** for clean, readable setup.
* 🎨 Full control over **scale, rotation, billboard mode, glow, background, and opacity**.
* 💡 **Brightness control** (block/sky light levels).
* 🖼️ **Display sizing** (width, height, line wrapping).
* 📡 Fires an event (`TextDisplaySpawnEvent`) after spawning for easy integration.

---

## Getting Started

### Basic Example

```java
TextDisplay td = DreamTextDisplay.textDisplay()
    .location(player.getLocation().add(0, 2, 0))
    .itemScale(1.5)
    .backgroundColor(Color.fromRGB(0, 0, 0))
    .textOpacity(200)
    .spawn("<green>Hello, World!</green>");
```

This spawns a glowing green floating label above the player.

---

## Builder Functions

### 1. `world(World world)`

Sets the world where the text display will spawn.

```java
.textDisplay().world(Bukkit.getWorld("world"))
```

### 2. `location(Location location)`

Sets the exact spawn location. The world is inferred from the location.

```java
.location(player.getLocation().add(0, 2, 0))
```

### 3. `itemScale(double scale)`

Uniform scale for the text. Must be greater than `0`.

```java
.itemScale(2.0) // double-sized text
```

### 4. `leftRotation(Vector3f rotation)`

Convenience setter for tilt/yaw/roll using vector components.

```java
.leftRotation(new Vector3f(0f, 45f, 0f)) // yaw 45 degrees
```

### 5. `viewRange(float range)`

How far away players can see the display. `0.1 ≈ 16 blocks`.

```java
.viewRange(0.2f) // visible from ~32 blocks away
```

### 6. `shadowRadius(float radius)` & `shadowStrength(float strength)`

Configure the text’s shadow. Larger radius = softer shadow.

```java
.shadowRadius(0.5f)
.shadowStrength(3f)
```

### 7. `displayWidth(float width)` & `displayHeight(float height)`

Controls how text is wrapped and clipped.

```java
.displayWidth(80f).displayHeight(20f)
```

### 8. `billboard(Display.Billboard mode)`

Controls how the text faces players.

* `CENTER` → Faces all directions.
* `FIXED` → Static rotation.
* `VERTICAL` → Locks vertically.

```java
.billboard(Display.Billboard.CENTER)
```

### 9. `itemGlowColor(Color color)`

Sets a glowing outline color for the text.

```java
.itemGlowColor(Color.AQUA)
```

### 10. `backgroundColor(Color color)`

Sets the rectangular background fill color.

```java
.backgroundColor(Color.BLACK)
```

### 11. `lineWidth(int width)`

How many pixels before text wraps to a new line.

```java
.lineWidth(100)
```

### 12. `itemBrightness(Display.Brightness brightness)`

Overrides natural brightness.

```java
.itemBrightness(new Display.Brightness(15, 15)) // max sky & block light
```

### 13. `textOpacity(int opacity)`

Controls transparency (0 = invisible, 255 = fully opaque).

```java
.textOpacity(128) // 50% transparent
```

---

## Spawning the Display

```java
TextDisplay td = DreamTextDisplay.textDisplay()
    .location(new Location(world, 100, 65, -50))
    .itemScale(1.2)
    .spawn("<yellow>Teleporting Soon!</yellow>");
```

This will create a floating yellow label at the given coordinates.

---

## Event: `TextDisplaySpawnEvent`

After a display is spawned, the event is fired so other plugins can modify it.

```java
@EventHandler
public void onSpawn(TextDisplaySpawnEvent e) {
    e.getTextDisplay().text(Component.text("Overridden Text!"));
}
```

---

## Best Practices

* Always set a **world & location** explicitly when possible.
* Use **MiniMessage** tags (`<red>`, `<bold>`, etc.) for consistent styling.
* Keep **view ranges** modest to avoid rendering overhead.
* Pair with **DreamStopwatch** or **DreamTeleport** for cinematic UI effects.

---

## Suggestions for Future Improvements

* 🖼 Add **image-to-text rendering** support.
* 🔄 Add methods for **animated text updates**.
* 🎥 Add support for **per-player displays** (private holograms).
* 📦 Add integration with **DreamPlaceholder** for dynamic text.
* 🧩 Add chained builders for **timed despawn** or auto-removal.

---