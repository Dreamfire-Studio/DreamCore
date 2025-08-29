/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 *   <li>Ensures inputs are normalized into a single {@link DreamPressedKeys} stream.</li>
 *   <li>Auto-registers {@link IDreamKeyPressed} specs from {@code DreamCore.DreamKeyPatternSpecs} on join.</li>
 *   <li>Removes all player bindings on quit to avoid leaks.</li>
 * </ul>
 * </remarks>
 * <example>
 * Registering the adapter in a plugin:
 * <code>
 * public class MyPlugin extends JavaPlugin {
 *     private DreamKeyBukkitAdapter adapter;
 *
 *     @Override
 *     public void onEnable() {
 *         IDreamKeyManager manager = new DreamKeyManagerImpl();
 *         adapter = new DreamKeyBukkitAdapter(manager);
 *         getServer().getPluginManager().registerEvents(adapter, this);
 *     }
 * }
 * </code>
 *
 * <para>
 * With this setup, pressing <c>SNEAK + SPRINT</c> could be detected by
 * key patterns you’ve registered in {@code DreamCore.DreamKeyPatternSpecs}.
 * </para>
 * </example>
 */
public final class DreamKeyBukkitAdapter implements Listener {

    private final IDreamKeyManager manager;

    /// <summary>Constructs the adapter with a key manager.</summary>
    /// <param name="manager">Manager responsible for handling key inputs and patterns.</param>
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        // IMPORTANT: Only process MAIN hand to avoid double fire (main + off-hand)
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player p = e.getPlayer();
        Action a = e.getAction();

        if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
            feed(p, DreamPressedKeys.LEFT_CLICK);
        } else if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            feed(p, DreamPressedKeys.RIGHT_CLICK);
        }
    }

    // --- Auto-bind pattern specs on join / cleanup on quit ---
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

    /// <summary>
    /// Helper to feed a translated key press into the manager.
    /// </summary>
    /// <param name="p">Player who pressed the key.</param>
    /// <param name="key">The normalized {@link DreamPressedKeys} enum value.</param>
    private void feed(Player p, DreamPressedKeys key) {
        manager.handleInput(p, key, Instant.now());
    }
}