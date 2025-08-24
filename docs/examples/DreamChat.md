# DreamChat — Beginner’s Guide & API

This guide explains how to use **DreamChat** and **DreamMessageFormatter** to send formatted messages in Minecraft using [Kyori Adventure](https://docs.advntr.dev/) and optional [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/). It covers sending to players, console, permissions, and worlds, plus sanitizing messages for safe output.

---

## What DreamChat Does

* Sends messages with **MiniMessage formatting**.
* Supports **PlaceholderAPI expansion** for player context.
* Provides utility methods for sending to console, players, worlds, or permission-based audiences.
* Splits multi-line messages using the `FormatTags.SplitLine` token.

---

## Quick Start

```java
DreamChat.SendMessageToPlayer(player, "<green>Hello, <name>!", DreamMessageSettings.all());
DreamChat.BroadcastMessage("<yellow>Server restarting soon!", DreamMessageSettings.safeChat());
DreamChat.SendMessageToConsole("<red>[Debug] Something happened", DreamMessageSettings.plain());
```

---

## Core Methods (DreamChat)

### `SendMessageToConsole(String, DreamMessageSettings)`

Formats and sends a message to the console.

* Applies MiniMessage + PAPI (if enabled).
* Splits into multiple lines if the message contains `FormatTags.SplitLine`.

### `SendMessageToPlayer(Player, String, DreamMessageSettings)`

Sends a formatted message to a specific player.

* Uses `DreamMessageFormatter.format` with the player context.
* Expands PlaceholderAPI if enabled.

### `BroadcastMessage(String, DreamMessageSettings)`

Sends a message to **all online players**.

### `SendMessageToPerm(String, T permission, DreamMessageSettings)`

Sends a message to all players who have a specific **enum-based permission**.

* Integrates with `DreamLuckPerms` to resolve and check permissions.
* Example:

```java
DreamChat.SendMessageToPerm("<green>You have admin rights!", MyPermissions.ADMIN, DreamMessageSettings.all());
```

### `SendMessageToWorld(String, String, DreamMessageSettings)`

Sends a message to all players in a world by **name**.

* Case-insensitive world name check.

### `SendMessageToWorld(String, UUID, DreamMessageSettings)`

Sends a message to all players in a world by **UUID**.

* ⚠️ Currently uses `==` instead of `.equals` for UUID comparison → may fail if UUIDs are not reference-equal.

### `SplitMessage(String)` (private)

Splits a message into multiple lines using `FormatTags.SplitLine`.

---

## Core Methods (DreamMessageFormatter)

### `format(String, DreamMessageSettings)`

Formats a raw string into an Adventure `Component`.

* Applies MiniMessage if enabled.
* Strips unsupported tags if MiniMessage is disabled.

### `format(String, Player, DreamMessageSettings)`

Formats a raw string for a **player context**.

* Expands PlaceholderAPI placeholders.
* Respects MiniMessage settings.

### `format(Component, DreamMessageSettings)`

Formats an Adventure component.

* If MiniMessage is disabled, returns as-is.
* Otherwise, serializes to MiniMessage → applies sanitization → deserializes back.

### `centerMessage(String, int)`

Centers a plain string to a given width using spaces.

### `centerMessage(Component, int)`

Centers a component by serializing to plain text, then re-wrapping.

### `limitMessage(String, int)`

Truncates a string and appends `...` if longer than the max length.

### `limitMessage(Component, int)`

Truncates a component’s plain-text view and returns a new plain component.

### `capitalizeWords(String)`

Capitalizes the first letter of each word.

### `reverseMessage(String)`

Reverses a string.

### `placeholder(String, String)`

Creates a MiniMessage placeholder tag that replaces `<key>` with a **literal string**.

### `placeholder(String, Component)`

Creates a MiniMessage placeholder tag that replaces `<key>` with a **Component**.

---

## DreamMessageSettings Presets

* **`all()`** → placeholders + MiniMessage + colors + formatting + click/hover.
* **`safeChat()`** → placeholders + MiniMessage + colors/formatting, **no click/hover**.
* **`plain()`** → no placeholders, no MiniMessage, no colors.

---

## FormatTags

* **`SplitLine("<:::>")`** → token used to split multi-line messages.

```java
DreamChat.SendMessageToPlayer(player, "Line1" + FormatTags.SplitLine.tag + "Line2", DreamMessageSettings.all());
```

---

## Events

This system does **not dispatch Bukkit events** directly, but integrates with:

* **DreamLuckPerms** (for permission-based messaging).
* **PlaceholderAPI** (if installed).

---

## Pitfalls & Notes

* **UUID comparison bug** in `SendMessageToWorld(message, UUID, settings)` — replace `==` with `.equals`.
* **PlaceholderAPI must be installed** for PAPI placeholders to work.
* **MiniMessage sanitization** strips tags when disabled, preventing raw `<tags>` from leaking.
* **Centering is character-based** — not pixel-based.

---

## Suggestions

1. Fix UUID comparison in `SendMessageToWorld(UUID)` to use `.equals`.
2. Add async/batch messaging for large broadcasts to reduce lag spikes.
3. Add rich logging hooks for all chat sends.
4. Provide pixel-width centering (Adventure has APIs for measuring font width).
5. Add configurable message splitting (custom delimiter instead of `FormatTags.SplitLine`).