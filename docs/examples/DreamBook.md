# DreamBook — Beginner’s Guide & API

The **DreamBook** system provides server‑controlled written books with viewer tracking, page management, and a set of Bukkit events. You can open/close books for players, update their contents dynamically, and react to events like pages changing or viewers being added/removed.

---

## What DreamBook Does

* Creates custom written books with formatted author, title, and pages.
* Opens/closes books for players (optionally with duration).
* Tracks who is currently viewing a book.
* Fires Bukkit events on open/close, page updates, and viewer changes.

---

## Quick Start

```java
DreamBook book = new DreamBook.BookBuilder("Server", "Welcome Guide")
    .bookPages(
        "Welcome to the server!",
        "Use /help to get started."
    )
    .createBook();

book.openBook(player, 10); // Open for 10 seconds
```

---

## Core Methods (DreamBook)

### `boolean isPlayerInBook(Player player)`

Checks if a player is currently viewing this book.

### `void openBook(Player player)`

Opens the book for the given player.

* Fires `DreamBookOpenEvent` (cancellable).
* Fires `DreamBookViewerAddedEvent` if player is added to viewers.

### `void openBook(Player player, int durationInSeconds)`

Opens the book for a fixed duration, then closes it.

* Uses `BukkitScheduler` to auto‑close after the duration.

### `void closeBook(Player player)`

Closes the book for a player and removes them from viewers.

* Fires `DreamBookCloseEvent`.
* Fires `DreamBookViewerRemovedEvent` if successfully removed.

### `void playerQuit(Player player)`

Helper to ensure a player’s book session is cleaned up on quit.

---

## Page Management

### `List<String> getPagesView()`

Returns an immutable snapshot of current pages.

### `void setPages(List<String> newPages)`

Replaces all pages with the given list.

* Fires `DreamBookPagesUpdatedEvent`.

### `void addPages(String... additionalPages)`

Appends additional pages.

* Pages are formatted via `DreamMessageFormatter`.
* Fires `DreamBookPagesUpdatedEvent`.

### `void clearPages()`

Clears all pages.

* Fires `DreamBookPagesUpdatedEvent` with empty list.

### `void displayNextFrame()`

Reserved for future animation logic. Currently a no‑op.

---

## Builder API

### `BookBuilder(String author, String title)`

Creates a new builder with author and title (both formatted).

### Options

* `.generation(BookMeta.Generation)` — set book generation.
* `.bookPages(String...)` — add formatted pages.
* `.bookPages(TextComponent...)` — **deprecated** legacy Spigot API.

### `DreamBook createBook()`

Builds the DreamBook, registers it in `DreamCore.DreamBooks`, and tags it for persistence.

---

## Events

Events are fired **immediately from constructors**, so instantiating an event calls `PluginManager#callEvent(this)`.

* **DreamBookOpenEvent** — when a book is about to open (cancellable).
* **DreamBookCloseEvent** — when a book is closed.
* **DreamBookPagesUpdatedEvent** — when pages are changed.
* **DreamBookViewerAddedEvent** — when a player is added as a viewer (cancellable).
* **DreamBookViewerRemovedEvent** — when a player is removed as a viewer (cancellable).

**Example Listener:**

```java
@EventHandler
public void onBookOpen(DreamBookOpenEvent e) {
    if (e.getPlayer().hasPermission("no.books")) {
        e.setCancelled(true);
    }
}
```

---

## Usage Flow

1. Create with `BookBuilder`.
2. Call `createBook()`.
3. Open with `openBook(player)` or `openBook(player, seconds)`.
4. Update pages as needed (`setPages`, `addPages`, `clearPages`).
5. Handle events for custom logic.

---

## Pitfalls & Notes

* **Event timing** — events fire from constructors, so they trigger immediately.
* **Viewer tracking** — players are tracked by UUID in an internal list.
* **Auto‑close** — only `openBook(player, seconds)` auto‑closes; otherwise you must call `closeBook()`.
* **Formatting** — all string inputs are formatted via `DreamMessageFormatter`.
* **Persistence** — books are tagged with a persistent NBT key for re‑identification.

---

## Suggestions

1. Return the created `DreamBook` from `createBook()` consistently (currently it does, but ensure clear docs).
2. Add support for **interactive pages** (clickable actions, hover tooltips) beyond plain text.
3. Provide a **page flip event** if client API allows.
4. Add a `getViewers()` accessor to expose the full viewer list.
5. Add **async persistence** for large books to avoid main thread NBT cost.
6. Consider `closeAllViewers()` helper to clear books on plugin shutdown.