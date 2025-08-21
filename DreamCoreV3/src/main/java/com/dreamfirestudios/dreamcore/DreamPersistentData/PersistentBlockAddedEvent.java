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
/// This event is not cancellable.
/// Use it to react when custom metadata is stored on a block via a <see cref="NamespacedKey"/>.
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

    /// <summary>
    /// The block that received the persistent data entry.
    /// </summary>
    private final Block block;

    /// <summary>
    /// The namespaced key under which the value was stored.
    /// </summary>
    private final NamespacedKey namespacedKey;

    /// <summary>
    /// The value that was stored for the key.
    /// </summary>
    private final Object value;

    /// <summary>
    /// Creates a new <see cref="PersistentBlockAddedEvent"/>.
    /// </summary>
    /// <param name="block">The target block.</param>
    /// <param name="namespacedKey">The key used to store the value.</param>
    /// <param name="value">The value stored.</param>
    public PersistentBlockAddedEvent(Block block, NamespacedKey namespacedKey, Object value) {
        this.block = block;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the handler list for this event.
    /// </summary>
    /// <returns>The static handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <inheritdoc />
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}