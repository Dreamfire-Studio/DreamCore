# DreamCore — Ideas for Future Core Modules

This document collects potential **new modules** to expand DreamCore’s capabilities across Java utility space and Paper plugin development. Each idea is written with the mindset of what would be useful for developers building plugins on top of DreamCore.

---

## 1. DreamConfigPlus

* **Purpose:** A next-level configuration system.
* **Features:**

    * Type-safe config classes (annotated fields → auto YAML binding).
    * Auto-migrations between config versions.
    * Live reload with event hooks.
    * Support for JSON, HOCON, MongoDB backends.
* **Why:** Many plugins duplicate config boilerplate. This reduces repetition.

---

## 2. DreamEventBus

* **Purpose:** A lightweight internal event bus for decoupled module communication.
* **Features:**

    * Annotations for auto-registering listeners.
    * Async dispatch options.
    * Priorities and filters.
* **Why:** Simplifies internal system interactions outside Bukkit’s global events.

---

## 3. DreamScheduler

* **Purpose:** High-level task scheduling utilities.
* **Features:**

    * Fluent API for delayed and repeating tasks.
    * Support for async tasks with completion callbacks on main thread.
    * Cron-style scheduling expressions.
* **Why:** Bukkit scheduler is verbose; this adds expressiveness and safety.

---

## 4. DreamPermissions

* **Purpose:** Centralized permission handling.
* **Features:**

    * Simple wrapper for Bukkit `hasPermission`.
    * Hierarchical permission nodes with wildcards.
    * Events when permissions are granted/removed.
* **Why:** Standardizes permission checks across modules.

---

## 5. DreamInventory

* **Purpose:** Simplify creation of custom GUIs.
* **Features:**

    * Builder for menus and buttons.
    * Click callbacks.
    * Pagination helpers.
* **Why:** Inventory menus are common in plugins but tedious to implement.

---

## 6. DreamChatPlus

* **Purpose:** Richer chat utilities.
* **Features:**

    * PlaceholderAPI integration.
    * MiniMessage templates with variables.
    * Chat channels (global, local, staff).
* **Why:** Extend DreamChat beyond formatting.

---

## 7. DreamEconomy

* **Purpose:** Lightweight, optional economy API.
* **Features:**

    * Player balances with persistence.
    * Vault integration (if present).
    * Simple events (BalanceChangeEvent).
* **Why:** Common need for minigames and survival plugins.

---

## 8. DreamScoreboard

* **Purpose:** Utility for scoreboards and tab lists.
* **Features:**

    * Animated scoreboards.
    * Placeholder integration.
    * Per-player scoreboard contexts.
* **Why:** Scoreboards are powerful but boilerplate-heavy.

---

## 9. DreamNPC

* **Purpose:** Manage non-player characters.
* **Features:**

    * Spawn/despawn NPCs with skins.
    * Simple interaction events.
    * Pathfinding helpers.
* **Why:** NPCs are central to many plugins.

---

## 10. DreamRegion

* **Purpose:** Define and manage regions in worlds.
* **Features:**

    * Cuboid and polygonal region definitions.
    * Region enter/exit events.
    * Region flags (pvp, block-break, fly).
* **Why:** Base for many game mechanics.

---

## 11. DreamLogging

* **Purpose:** Structured logging for plugins.
* **Features:**

    * Log levels (debug/info/warn/error).
    * JSON log output for integrations.
    * Per-module loggers.
* **Why:** Easier debugging and analytics.

---

## 12. DreamMetrics

* **Purpose:** Lightweight metrics collection.
* **Features:**

    * Execution timers and counters.
    * Export to Prometheus or bStats.
    * Built-in performance profiling tools.
* **Why:** Monitor plugin health.

---

## 13. DreamAI

* **Purpose:** Provide hooks for AI/ML assisted plugin features.
* **Features:**

    * Basic chatbot integration (OpenAI/LLMs).
    * NPC AI dialogue.
    * Adaptive difficulty scaling.
* **Why:** Forward-looking experimentation.

---

## 14. DreamTesting

* **Purpose:** Built-in testing framework for plugins.
* **Features:**

    * Unit test helpers for Bukkit mocks.
    * Integration tests with simulated servers.
* **Why:** Promote test-driven development.

---

## 15. DreamUtils (General Java)

* **Purpose:** Cross-cutting helpers.
* **Features:**

    * Collections/Stream utilities.
    * String/Number parsing.
    * Reflection and annotation scanners.
* **Why:** Avoid reinventing common utilities in each module.

---

## Suggestions / Future Enhancements

* Organize modules into categories (World, Player, Utility, Systems).
* Provide a unified `DreamCore API` interface to fetch modules.
* Add Gradle/Maven templates to bootstrap plugins using DreamCore.
* Provide documentation site with recipes (e.g., “make a scoreboard in 10 lines”).
* Bundle optional modules (economy, region, npc) as add-ons to keep core lean.
* Create example plugins that demonstrate integrating multiple modules together.

# DreamCore — Ideas for Future Core Modules

This document collects potential **new modules** to expand DreamCore’s capabilities across Java utility space and Paper plugin development. Each idea is written with the mindset of what would be useful for developers building plugins on top of DreamCore.

---

## 1. DreamConfigPlus

*(…existing content…)*

## 15. DreamUtils (General Java)

*(…existing content…)*

---

## 16. DreamParticles

* **Purpose:** Simplify particle effects.
* **Features:**

    * Fluent API for spawning particle shapes (lines, circles, spheres).
    * Animated particle sequences.
    * Per-player or global effects.
* **Why:** Particle visuals are a common gameplay feedback tool.

---

## 17. DreamCommands

* **Purpose:** Framework for commands.
* **Features:**

    * Annotation-based command registration.
    * Automatic tab-completion.
    * Permission and cooldown integration.
* **Why:** Avoid repetitive Bukkit command boilerplate.

---

## 18. DreamItems

* **Purpose:** Advanced item builder utilities.
* **Features:**

    * Fluent API for items with lore, enchants, attributes.
    * Custom NBT and metadata helpers.
    * Preset item factories for common cases.
* **Why:** Speeds up custom item creation.

---

## 19. DreamTeams

* **Purpose:** Team/party management.
* **Features:**

    * Create, join, leave teams.
    * Team chat.
    * Team-based scoreboards and objectives.
* **Why:** Foundation for minigames.

---

## 20. DreamPathfinding

* **Purpose:** Higher-level entity AI movement.
* **Features:**

    * Pathfinding to locations or entities.
    * Patrol routes.
    * Simple APIs for chase/flee behaviors.
* **Why:** Give entities richer AI without rewriting NMS.

---

## 21. DreamStorage

* **Purpose:** Persistent data abstraction.
* **Features:**

    * Wraps Bukkit’s PersistentDataContainer.
    * JSON/YAML/Database adapters.
    * Type-safe getters/setters.
* **Why:** Unified, safer data persistence layer.

---

## 22. DreamWeather

* **Purpose:** World weather control.
* **Features:**

    * Force weather states (clear/rain/thunder).
    * Scheduled weather cycles.
    * Per-player fake weather.
* **Why:** Adds environmental control to world management.

---

## 23. DreamKits

* **Purpose:** Manage kits/loadouts.
* **Features:**

    * Define kits in configs.
    * Give/take kits to players.
    * Cooldowns and permissions.
* **Why:** Widely used in PvP and minigames.

---

## 24. DreamPlaceholders

* **Purpose:** Native placeholder system.
* **Features:**

    * Register custom placeholders easily.
    * Built-in integration with PlaceholderAPI.
    * Context-aware resolution (player/world).
* **Why:** Simplifies dynamic text in chat, GUIs, scoreboards.

---

## 25. DreamAnimations

* **Purpose:** Reusable animation engine.
* **Features:**

    * Animate titles, scoreboards, holograms.
    * Keyframe-based system.
    * Easing functions (linear, ease-in, bounce).
* **Why:** Adds polish and dynamic feel.

---

## Suggestions / Future Enhancements

* Organize modules into categories (World, Player, Utility, Systems).
* Provide a unified `DreamCore API` interface to fetch modules.
* Add Gradle/Maven templates to bootstrap plugins using DreamCore.
* Provide documentation site with recipes (e.g., “make a scoreboard in 10 lines”).
* Bundle optional modules (economy, region, npc) as add-ons to keep core lean.
* Create example plugins that demonstrate integrating multiple modules together.
* Add a **plugin marketplace integration** for sharing DreamCore modules.
* Provide a **DreamCore DevTools** module with hot-reload, debugging, and profiling utilities.