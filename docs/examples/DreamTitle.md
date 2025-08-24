# DreamTitle — Beginner-Friendly Guide (Adventure Titles for Bukkit/Spigot/Paper)

> **What is this?**
> `DreamTitle` is a tiny helper around the Kyori **Adventure** API that lets you show Title/Subtitles to players using **MiniMessage** strings. It also plugs into your existing `DreamMessageFormatter` so placeholders, colors, and per-player formatting Just Work™.

---

## What You Get

* **Two entry points**:

    * `sendTitleToPlayer(Player, String, String, int, int, int)` → send a title to a single player.
    * `sendTitleToPlayer(String, String, int, int, int)` → 🚨 sends to **all** online players (the name is misleading; see Notes below).
* **MiniMessage input**

    * e.g. `"<gold>Welcome"`, `"<gray>Have fun"`
* **Timing as seconds**

    * `fadeIn`, `stay`, `fadeOut` are seconds → converted to `Duration` under the hood.
* **Per-player formatting** via `DreamMessageFormatter.format(..., player, DreamMessageSettings.all())`.

> **Threading:** Calls should be made on the **main server thread** (Bukkit’s `player.showTitle(...)` is not thread-safe).

---

## Quick Start

```java
// Send to a single player
DreamTitle.sendTitleToPlayer(
    player,
    "<green>Level Up!",
    "<gray>You reached <yellow>10<gray>.",
    1, 3, 1 // fadeIn, stay, fadeOut (seconds)
);
```

```java
// Broadcast to everyone online
DreamTitle.sendTitleToPlayer(
    "<gold>Welcome!",
    "<gray>Have fun on the server",
    1, 3, 1
);
```

---

## API Reference

### `sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut)`

**Purpose:** Show a title/subtitle to **one** player.

**Parameters**

* `player`: Target player (method returns immediately if `null`).
* `title`: MiniMessage for the main title. `null` → empty.
* `subtitle`: MiniMessage for the subtitle line. `null` → empty.
* `fadeIn`, `stay`, `fadeOut`: Seconds (ints) converted to `Duration.ofSeconds(...)`.

**Behavior**

* Both `title` and `subtitle` are formatted through `DreamMessageFormatter` using `DreamMessageSettings.all()` to ensure consistent color/placeholder handling.
* A `Title.Times` is created with the provided durations and delivered with `player.showTitle(...)`.

**Example**

```java
// Simple green title, no subtitle
DreamTitle.sendTitleToPlayer(player, "<green>Level Up!", "", 1, 2, 1);
```

**Tips**

* If your MiniMessage uses placeholders (e.g., `<player>`), ensure your `DreamMessageFormatter` resolves them for the provided `player`.
* Avoid negative timing values; they will be passed to `Duration.ofSeconds(...)` and can throw if extremely large in magnitude. Prefer non-negative small integers.

---

### `sendTitleToPlayer(String title, String subtitle, int fadeIn, int stay, int fadeOut)`

**Purpose:** Broadcast a title/subtitle to **all online players**.

**Parameters**

* `title`, `subtitle`, `fadeIn`, `stay`, `fadeOut` — same semantics as above.

**Behavior**

* Iterates `Bukkit.getOnlinePlayers()` and forwards to the single-player overload for each player.
* Each player’s message is formatted **per-player** using `DreamMessageFormatter` (so placeholders like player name will be correct per recipient).

**Example**

```java
// Broadcast a welcome banner to everyone online
DreamTitle.sendTitleToPlayer("<gold>Welcome!", "<gray>Have fun", 1, 3, 1);
```

**Note on Naming**

* The method name implies it targets a single player, but it actually **broadcasts**. Consider renaming to `sendTitleToAll(...)` in a future release (see Suggestions) to reduce confusion.

---

## MiniMessage Cheatsheet (Common Tags)

* Colors: `<red>`, `<green>`, `<blue>`, `<gold>`, `<gray>`, etc.
* Decorations: `<bold>`, `<italic>`, `<underlined>`, `<strikethrough>`.
* Reset: `<reset>`.
* Gradients: `<gradient:#FF0000:#FFFF00>Text</gradient>`.
* Placeholders: e.g., your formatter might replace `<player>` with the player’s display name.

> **Heads up:** If you are copying raw `<`/`>` strings into YAML, make sure your YAML quoting is correct.

---

## Usage Patterns

### Welcome Titles on Join

```java
@EventHandler
public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    DreamTitle.sendTitleToPlayer(
        p,
        "<gold>Welcome, <yellow>" + p.getName() + "</yellow>",
        "<gray>Enjoy your stay!",
        1, 3, 1
    );
}
```

### Round Start / End Banners

```java
public void showRoundStart(Collection<Player> players) {
    for (Player p : players) {
        DreamTitle.sendTitleToPlayer(p, "<green>Round Start!", "<gray>Good luck", 1, 2, 1);
    }
}

public void showRoundEndWin(Player winner) {
    DreamTitle.sendTitleToPlayer(
        "<gold>Victory!",
        "<gray>Champion: <yellow>" + winner.getName(),
        1, 3, 1
    );
}
```

### Cooldown / Ability Feedback

```java
public void showAbilityReady(Player p, String abilityName) {
    DreamTitle.sendTitleToPlayer(
        p,
        "<green>" + abilityName + " Ready!",
        "<gray>Press your key to use it",
        1, 2, 1
    );
}
```

---

## Edge Cases & Best Practices

* **Null player** → The single-target method returns without action. Always ensure the player is still online before calling.
* **Large/Negative timings** → Prefer small non-negative values; timing is in **seconds** (int) → `Duration`.
* **Threading** → Call only from the **main server thread**. If you must call from async code, schedule a sync task first.
* **Spam Protection** → Titles are visually intrusive; avoid spamming in rapid succession. Group or rate-limit where possible.
* **Localization** → If you localize, resolve localized MiniMessage strings **before** calling `DreamTitle` and still pass through `DreamMessageFormatter` for consistent styling.

---

## Troubleshooting

* **Nothing shows**: Are you calling on the main thread? Is the player still online? Are your timings reasonable (non-negative, not zero stay)?
* **Text shows raw tags**: Ensure MiniMessage syntax is correct and that your `DreamMessageFormatter` actually parses MiniMessage for the messages you send.
* **Odd colors/formatting**: Conflicting resets or nested tags — try simplifying the message to isolate the issue.

---

## Suggestions / Future Enhancements

* **Rename broadcast method**: `sendTitleToPlayer(String, String, int, int, int)` → `sendTitleToAll(...)` to reflect actual behavior.
* **Parameter Validation**: Clamp `fadeIn/stay/fadeOut` to `≥ 0` and treat `null` strings as empty explicitly (already done) — consider logging when values are out of expected bounds.
* **Overloads with `Duration`**: Provide `Duration`-based overloads for more precise timing and readability.
* **Preset Styles**: Add helpers like `sendSuccess(...)`, `sendWarning(...)`, `sendError(...)` that apply standard colors/icons.
* **Builder API**: `DreamTitle.builder().title("...").subtitle("...").fadeIn(1).stay(3).fadeOut(1).to(player)` for composability.
* **Audience Support**: Accept `Audience`/`Collection<? extends Audience>` for more flexible targeting beyond `Player`.
* **Rate Limiting**: Optional per-player cooldown to avoid flicker/spam when multiple systems send titles.