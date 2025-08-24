# DreamPlaceholder — Developer Guide

DreamPlaceholder is DreamCore’s wrapper around **PlaceholderAPI** that makes it easy to register and manage your own custom placeholders. If you are writing a plugin that needs dynamic values like player balances, ranks, or server stats inside chat or GUIs, this system is designed to make that simple and type-safe.

---

## Why Use DreamPlaceholder?

* Built on **PlaceholderAPI**, so all your placeholders integrate seamlessly with other plugins.
* Clean **registry system** using `DreamPlaceholderManager`.
* Each placeholder has its own **provider class** implementing `IDreamPlaceholder`.
* **Argument support**: you can pass parameters inside the placeholder (e.g., `%dreamcore_balance:gold%`).
* Handles **safe fallbacks** — unknown keys never crash, they just resolve to `""`.

---

## Core Classes

### 1. `DreamPlaceholderManager`

Central expansion/registry that ties into PlaceholderAPI.

```java
DreamPlaceholderManager mgr = new DreamPlaceholderManager(
    "dreamcore",                // identifier: %dreamcore_key%
    "Dreamfire Studios",        // author
    "1.0.0"                     // version
);
```

#### Constructor

```java
DreamPlaceholderManager(String identifier, String author, String version)
```

* `identifier`: used in the placeholder token (e.g. `%identifier_key%`).
* `author`: shown in PlaceholderAPI metadata.
* `version`: version string for API compatibility.

#### Expansion Metadata

* `getIdentifier()` → identifier string.
* `getAuthor()` → author string.
* `getVersion()` → version string.
* `persist()` → always returns `true` (placeholders survive `/papi reload`).
* `canRegister()` → always `true`.

#### Registering Providers

```java
boolean success = mgr.register(new BalancePlaceholder());
```

Registers a new `IDreamPlaceholder`. Returns `false` if another provider with the same key is already registered.

#### Unregistering Providers

```java
mgr.unregister("balance");
```

Removes a placeholder provider by its key.

#### Clearing All Providers

```java
mgr.clear();
```

Removes every registered placeholder provider.

#### Request Handling

Handled internally via PlaceholderAPI hooks:

* `onRequest(OfflinePlayer, String)`
* `onPlaceholderRequest(Player, String)`

You don’t normally call these yourself — they’re invoked when other plugins resolve placeholders.

### 2. `IDreamPlaceholder`

Interface you implement to create new placeholders.

```java
public final class BalancePlaceholder implements IDreamPlaceholder {
    @NotNull
    public String key() {
        return "balance";
    }

    @NotNull
    public String resolve(@Nullable OfflinePlayer player, @NotNull String[] args) {
        if (player == null) return "0";
        // Imagine we query some balance system here:
        return "100";
    }
}
```

#### Methods

* `String key()` → root key used in placeholder.
* `String resolve(OfflinePlayer player, String[] args)` → return the resolved value.

---

## Example Usage

### Registering Placeholders

```java
DreamPlaceholderManager mgr = new DreamPlaceholderManager("dreamcore", "Dreamfire", "1.0.0");
mgr.register(new BalancePlaceholder());
mgr.register(new RankPlaceholder());

mgr.register(); // ties into PlaceholderAPI
```

### Using Placeholders

In configs or chat:

```
Welcome %dreamcore_balance% coins, rank: %dreamcore_rank%!
```

With arguments:

```
%dreamcore_balance:gold% → resolves gold balance.
%dreamcore_balance:diamonds% → resolves diamond balance.
```

---

## Error Handling

* Unknown keys return an **empty string**.
* Exceptions in your provider are caught and logged.
* All lookups are **O(1)** thanks to `ConcurrentHashMap`.

---

## Suggestions for Future Improvements

* **Async evaluation**: allow providers to mark themselves as async for heavy database lookups.
* **Placeholder groups**: batch-register multiple providers in one object.
* **Debugging tools**: command to list all registered DreamCore placeholders.
* **Cache layer**: support optional short-lived caching for expensive placeholders.
* **Parameter parser**: support structured arguments (`key:type=value`) instead of only `:` or `_` splitting.

---