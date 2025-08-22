/*  Copyright (c) Dreamfire Studios
 *  DocFX-friendly XML docs
 */

package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is removed from an <see cref="ItemStack"/>.
/// </summary>
/// <remarks>
/// Not cancellable. Fired after the value is removed and meta is reapplied to the stack.
/// </remarks>
@Getter
public class PersistentItemStackRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Item from which the key was removed.</summary>
    private final ItemStack itemStack;

    /// <summary>Key that was removed.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>Creates and dispatches the event.</summary>
    public PersistentItemStackRemovedEvent(ItemStack itemStack, NamespacedKey namespacedKey){
        this.itemStack = itemStack;
        this.namespacedKey = namespacedKey;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}