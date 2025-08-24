# DreamJava Core — Developer Guide

The **DreamJava Core** provides the foundational system that powers the Dreamfire ecosystem. At its heart are tools for **automatic component registration**, **class discovery**, and **stable instance identification**. This guide will walk you through the core functions, explain how they work, and show you how to use them as a new plugin developer.

---

## 📦 DreamClassAPI — Automatic Registration Hub

### What it does

The `DreamClassAPI` automatically discovers and registers classes in your plugin that are annotated with `@PulseAutoRegister`. It checks which core interfaces these classes implement (loops, listeners, recipes, placeholders, etc.) and wires them into DreamCore automatically.

This means you can focus on writing functionality, without worrying about registering it manually.

### Typical Use

Call this once inside your plugin’s `onEnable()` method:

```java
@Override
public void onEnable() {
    DreamClassAPI.RegisterClasses(this);
}
```

DreamClassAPI will:

1. Scan your JAR for classes with `@PulseAutoRegister`
2. Create an instance of each class
3. Register it according to its type (listener, recipe, placeholder, etc.)
4. Log success to console

---

### Registration Types

#### 🔁 Loops — `IDreamLoop`

Used to run repeating tasks (similar to Bukkit schedulers).

```java
@PulseAutoRegister
public class AutoSaveLoop implements IDreamLoop { /* ... */ }
```

Registered with:

```java
DreamClassAPI.RegisterPulseLoop(plugin, new AutoSaveLoop());
```

#### 📦 Packet Adapters — `PacketAdapter`

Integrates with ProtocolLib to handle raw packet events.

```java
@PulseAutoRegister
public class ChatFilter extends PacketAdapter { /* ... */ }
```

#### 🎧 Listeners — `Listener`

Registers Bukkit listeners automatically.

```java
@PulseAutoRegister
public class JoinListener implements Listener { /* ... */ }
```

#### ✨ Enchantments — `IDreamEnchantment`

Custom enchantments with full DreamCore integration.

```java
@PulseAutoRegister
public class FrostbiteEnchant implements IDreamEnchantment { /* ... */ }
```

#### 🪙 Placeholders — `IDreamPlaceholder`

Adds dynamic placeholders.

```java
@PulseAutoRegister
public class ServerTPSPlaceholder implements IDreamPlaceholder { /* ... */ }
```

#### 🛠️ Recipes — `IDreamRecipe`

Adds custom crafting recipes.

```java
@PulseAutoRegister
public class SuperPickaxeRecipe implements IDreamRecipe { /* ... */ }
```

#### 🎲 Variable Tests — `DreamVariableTest`

Registers logic for validating variables.

```java
@PulseAutoRegister
public class BoolTest extends DreamVariableTest { /* ... */ }
```

#### 🔌 Plugin Messaging — `PluginMessageLibrary`

Registers plugin message channels (BungeeCord, Velocity, etc.).

```java
@PulseAutoRegister
public class BungeeBridge implements PluginMessageLibrary { /* ... */ }
```

---

## 🆔 DreamClassID — Unique Instance IDs

### What it does

Provides a permanent **UUID identifier** for every instance of your class. Perfect for identifying holograms, loops, or other runtime objects in registries.

### Typical Use

```java
public final class MyHologram extends DreamClassID { }

UUID id = new MyHologram().getClassID();
```

Now every hologram has a stable, unique ID.

---

## 🔍 DreamfireJavaAPI — Class Discovery

### What it does

Scans your plugin JAR for classes annotated with `@PulseAutoRegister` and returns them to `DreamClassAPI`. It filters only top-level classes inside your plugin package.

### Typical Use

```java
List<Class<?>> autoClasses = DreamfireJavaAPI.getAutoRegisterClasses(plugin);
```

This is used internally, but you can also use it for **custom discovery workflows** if needed.

---

## 🏷️ PulseAutoRegister — The Marker Annotation

### What it does

Marks a class to be automatically discovered and registered.

### Example

```java
@PulseAutoRegister
public final class MyListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Handle player join
    }
}
```

That’s all you need — `DreamClassAPI` does the rest.

---

## ✅ Summary

As a plugin developer:

* Use `@PulseAutoRegister` on your components
* Call `DreamClassAPI.RegisterClasses(this)` in `onEnable()`
* Implement any supported interface (Loop, Listener, Placeholder, etc.)
* Enjoy automatic registration and logging

---

## 💡 Suggestions for Future Changes

* Add support for **ScriptableObjects**-like configs for registration metadata.
* Introduce **Enums** for registration types instead of relying on `instanceof` checks.
* Allow **priority ordering** for auto-registered loops/listeners.
* Provide **unregister methods** for dynamically disabling features.
* Add **unit tests** for the discovery process.