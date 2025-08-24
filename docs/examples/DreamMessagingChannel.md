# DreamMessagingChannel — Developer Guide

The **DreamMessagingChannel** utilities simplify working with **BungeeCord plugin messaging** in Paper/Spigot. Instead of manually packing and parsing bytes, this system gives you helpers (`DreamPluginMessage`) and a base listener (`PluginMessageLibrary`) for common messaging flows.

---

## 📡 What is Plugin Messaging?

BungeeCord allows servers to exchange data through the special channel `BungeeCord`. You send a plugin message via a player’s connection, and Bungee delivers it to another server (or handles a subcommand).

DreamMessagingChannel wraps this into a clean API:

* **Send** messages with `DreamPluginMessage`.
* **Receive** messages with `PluginMessageLibrary`.

---

## 📨 Sending Messages

### Registering

Before sending or receiving, you must register your plugin:

```java
DreamPluginMessage.register(this, new MyMessageListener());
```

This registers outgoing + incoming channels.

Unregister later if needed:

```java
DreamPluginMessage.unregister(this);
```

---

### Connect a Player

Moves a player to another Bungee server.

```java
DreamPluginMessage.connect(plugin, player, "Hub");
```

---

### Request Player Count

Asks Bungee for the number of players on a server.

```java
DreamPluginMessage.requestPlayerCount(plugin, player, "Lobby1");
```

Response will be handled in your `PluginMessageLibrary.onPlayerCount` implementation.

---

### Forward a Payload

Send raw bytes to another server (or ALL servers).

```java
byte[] data = {1, 2, 3};
DreamPluginMessage.forward(plugin, player, "ALL", "MyChannel", data);
```

#### Forward a UTF String

Helper for simple text:

```java
DreamPluginMessage.forwardUtf(plugin, player, "ALL", "Chat", "Hello everyone!");
```

---

## 📥 Receiving Messages

To handle incoming plugin messages, extend `PluginMessageLibrary`.

### Example Listener

```java
public class MyMessageListener extends PluginMessageLibrary {
    private final JavaPlugin plugin;
    public MyMessageListener(JavaPlugin plugin) { this.plugin = plugin; }

    @Override
    protected JavaPlugin getOwningPlugin() { return plugin; }

    @Override
    protected void onPlayerCount(String server, int count) {
        plugin.getLogger().info(server + " has " + count + " players online.");
    }

    @Override
    protected void onForward(String subChannel, byte[] payload, Player receiver) {
        try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(payload))) {
            String msg = din.readUTF();
            plugin.getLogger().info("Forward received on " + subChannel + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Overridable Hooks

* `onPlayerCount(server, count)` → handles `PlayerCount` responses.
* `onForward(subChannel, payload, player)` → handles broadcast forwards.
* `onForwardToPlayer(subChannel, payload, player)` → handles targeted forwards.
* `onUnknownSubchannel(sub, raw, player)` → fallback for unrecognized subcommands.

---

## 🛠️ Preflight Checks

All sending methods call an internal `preflight` check to ensure:

* Plugin is not null.
* Player is not null.
* Outgoing channel is registered.

If checks fail, a warning is logged and the message is not sent.

---

## ✅ Summary

As a developer:

* Call **register()** once in `onEnable` with your listener.
* Use **connect**, **requestPlayerCount**, **forward**, or **forwardUtf** to send messages.
* Extend **PluginMessageLibrary** to handle responses.
* Always test payload handling with care, especially when parsing custom data.

---

## 💡 Suggestions for Future Changes

* Add **async response futures** (e.g., `CompletableFuture` for PlayerCount).
* Provide **built-in text/JSON codecs** instead of raw byte parsing.
* Add **automatic channel namespace management** to avoid collisions.
* Integrate with **DreamChat** for cross-server chat relays.
* Add support for **batch forwarding** multiple messages efficiently.