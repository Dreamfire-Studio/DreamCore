/*  Copyright (c) Dreamfire Studios
 *  This file is part of DreamfireV2 (industry-level code quality initiative).
 *  Style: DocFX-friendly XML docs, consistent with ChaosGalaxyTCG headers.
 */

package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is added to a <see cref="Block"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable. Fired immediately after the value is set and <c>TileState#update()</c> is called.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentBlockAdded(PersistentBlockAddedEvent e) {
///     Block b = e.getBlock();
///     getLogger().info("Added key " + e.getNamespacedKey() + " to block at " + b.getLocation());
/// }
/// ```
/// </example>
@Getter
public class PersistentBlockAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Block that received the entry.</summary>
    private final Block block;

    /// <summary>Key under which the value was stored.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>The stored value.</summary>
    private final Object value;

    /// <summary>
    /// Initializes a new event and calls it through the plugin manager.
    /// </summary>
    /// <param name="block">Target block.</param>
    /// <param name="namespacedKey">Stored key.</param>
    /// <param name="value">Stored value.</param>
    public PersistentBlockAddedEvent(Block block, NamespacedKey namespacedKey, Object value) {
        this.block = block;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}