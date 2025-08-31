/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 */
package com.dreamfirestudios.dreamcore.DreamKeyPressed;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

import java.time.Instant;

/**
 * <summary>
 * Bridges Bukkit input events to {@link DreamPressedKeys} and forwards them to an {@link IDreamKeyManager}.
 * Also handles player join/quit to auto-bind registered key patterns.
 * </summary>
 * <remarks>
 * <ul>
 *   <li>Listens to player movement, inventory, and interaction events.</li>
 *   <li>Normalizes inputs into a single {@link DreamPressedKeys} stream.</li>
 *   <li>Auto-registers {@link IDreamKeyPressed} specs from {@code DreamCore.DreamKeyPatternSpecs} on join.</li>
 *   <li>Removes all player bindings on quit to avoid leaks.</li>
 *   <li>Right-click echo: if the player is <em>currently sneaking</em> and performs a RIGHT_CLICK with the main hand,
 *       a synthetic {@link DreamPressedKeys#SNEAK} is emitted at the same timestamp to support AllAtOnce chords.</li>
 * </ul>
 * </remarks>
 * <example>
 * Registering the adapter:
 * <code>
 * public final class MyPlugin extends JavaPlugin {
 *     private DreamKeyBukkitAdapter adapter;
 *     @Override public void onEnable() {
 *         IDreamKeyManager manager = new DreamKeyManager();
 *         adapter = new DreamKeyBukkitAdapter(manager);
 *         getServer().getPluginManager().registerEvents(adapter, this);
 *     }
 * }
 * </code>
 * </example>
 */
public final class DreamKeyBukkitAdapter implements Listener {

    private final IDreamKeyManager manager;

    /**
     * <summary>Constructs the adapter with a key manager.</summary>
     * <param name="manager">Manager responsible for handling key inputs and patterns.</param>
     */
    public DreamKeyBukkitAdapter(final IDreamKeyManager manager) {
        this.manager = manager;
    }

    // --- Movement ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) feed(e.getPlayer(), DreamPressedKeys.SNEAK);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onSprint(PlayerToggleSprintEvent e) {
        if (e.getPlayer().isSprinting()) feed(e.getPlayer(), DreamPressedKeys.SPRINT);
    }

    // --- Inventory / Hotbar ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player p) feed(p, DreamPressedKeys.INVENTORY_OPEN);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onItemHeld(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        switch (e.getNewSlot()) {
            case 0 -> feed(p, DreamPressedKeys.HOTBAR_1);
            case 1 -> feed(p, DreamPressedKeys.HOTBAR_2);
            case 2 -> feed(p, DreamPressedKeys.HOTBAR_3);
            case 3 -> feed(p, DreamPressedKeys.HOTBAR_4);
            case 4 -> feed(p, DreamPressedKeys.HOTBAR_5);
            case 5 -> feed(p, DreamPressedKeys.HOTBAR_6);
            case 6 -> feed(p, DreamPressedKeys.HOTBAR_7);
            case 7 -> feed(p, DreamPressedKeys.HOTBAR_8);
            case 8 -> feed(p, DreamPressedKeys.HOTBAR_9);
            default -> { /* no-op */ }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onSwap(PlayerSwapHandItemsEvent e) {
        feed(e.getPlayer(), DreamPressedKeys.SWAP_HANDS);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent e) {
        feed(e.getPlayer(), DreamPressedKeys.DROP_ITEM);
    }

    // --- Clicks ---

    /**
     * <summary>
     * Normalizes left/right click to {@link DreamPressedKeys} and forwards to the manager.
     * If the player is currently sneaking and performs a RIGHT_CLICK with the main hand,
     * a synthetic {@link DreamPressedKeys#SNEAK} is also emitted at the same timestamp.
     * </summary>
     * <remarks>
     * Processes only {@link EquipmentSlot#HAND} to avoid double-firing with off-hand.
     * The sneak echo is intentionally limited to RIGHT_CLICK to minimize side-effects.
     * </remarks>
     * <param name="e">Bukkit player interaction event.</param>
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        final Action action = e.getAction();

        EquipmentSlot hand = e.getHand();
        if (hand == null) hand = EquipmentSlot.HAND;

        final boolean isBlockClick = action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK;
        if (isBlockClick && hand == EquipmentSlot.OFF_HAND) return;

        final Player p = e.getPlayer();
        final Instant ts = Instant.now();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            feed(p, DreamPressedKeys.LEFT_CLICK, ts);
            if (p.isSneaking()) {
                feed(p, DreamPressedKeys.SNEAK, ts);
            }
            return;
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            feed(p, DreamPressedKeys.RIGHT_CLICK, ts);
            if (p.isSneaking()) {
                feed(p, DreamPressedKeys.SNEAK, ts);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var playerId = e.getPlayer().getUniqueId();
        for (var spec : com.dreamfirestudios.dreamcore.DreamCore.DreamKeyPatternSpecs) {
            if (spec instanceof IDreamKeyPressed listener) {
                manager.register(playerId, spec, listener);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        manager.unregisterAll(e.getPlayer().getUniqueId());
    }

    // --- Helpers ---

    /**
     * <summary>Helper to feed a translated key press into the manager.</summary>
     * <param name="p">Player who pressed the key.</param>
     * <param name="key">The normalized {@link DreamPressedKeys} enum value.</param>
     */
    private void feed(Player p, DreamPressedKeys key) {
        manager.handleInput(p, key, Instant.now());
    }

    /**
     * <summary>Helper to feed a translated key press into the manager with an explicit timestamp.</summary>
     * <param name="p">Player who pressed the key.</param>
     * <param name="key">Normalized key enum.</param>
     * <param name="at">Timestamp to forward to the manager (kept consistent for chords).</param>
     */
    private void feed(Player p, DreamPressedKeys key, Instant at) {
        manager.handleInput(p, key, at);
    }
}