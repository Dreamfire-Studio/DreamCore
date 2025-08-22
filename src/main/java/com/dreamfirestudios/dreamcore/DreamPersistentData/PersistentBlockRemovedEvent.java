/*  Copyright (c) Dreamfire Studios
 *  DocFX-friendly XML docs
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
/// Event fired when a persistent data entry is removed from a <see cref="Block"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable. Fired after the value is removed and <c>TileState#update()</c> is called.
/// </remarks>
@Getter
public class PersistentBlockRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Block from which the key was removed.</summary>
    private final Block block;

    /// <summary>Key that was removed.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>Creates and dispatches the event.</summary>
    public PersistentBlockRemovedEvent(Block block, NamespacedKey namespacedKey){
        this.block = block;
        this.namespacedKey = namespacedKey;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}