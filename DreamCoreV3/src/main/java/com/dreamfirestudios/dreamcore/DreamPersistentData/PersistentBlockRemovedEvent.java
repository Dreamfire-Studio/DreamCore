package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is removed from a <see cref="Block"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to react when custom metadata identified by a <see cref="NamespacedKey"/> is cleared from a block.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentBlockRemoved(PersistentBlockRemovedEvent e) {
///     Block b = e.getBlock();
///     getLogger().info("Removed key " + e.getNamespacedKey() + " from block at " + b.getLocation());
/// }
/// ```
/// </example>
@Getter
public class PersistentBlockRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The block from which the persistent data entry was removed.
    /// </summary>
    private final Block block;

    /// <summary>
    /// The namespaced key that was removed from the block.
    /// </summary>
    private final NamespacedKey namespacedKey;

    /// <summary>
    /// Initializes a new <see cref="PersistentBlockRemovedEvent"/>.
    /// </summary>
    /// <param name="block">The target block.</param>
    /// <param name="namespacedKey">The key that was removed.</param>
    public PersistentBlockRemovedEvent(Block block, NamespacedKey namespacedKey){
        this.block = block;
        this.namespacedKey = namespacedKey;
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