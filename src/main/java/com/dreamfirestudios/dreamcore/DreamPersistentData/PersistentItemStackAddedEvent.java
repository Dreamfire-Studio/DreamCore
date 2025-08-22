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
/// Event fired when a persistent data entry is added to an <see cref="ItemStack"/>.
/// </summary>
/// <remarks>
/// Not cancellable. Fired after the item meta has been persisted back onto the stack.
/// </remarks>
@Getter
public class PersistentItemStackAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Item that received the entry.</summary>
    private final ItemStack itemStack;

    /// <summary>Key under which the value was stored.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>The stored value.</summary>
    private final Object value;

    /// <summary>Creates and dispatches the event.</summary>
    public PersistentItemStackAddedEvent(ItemStack itemStack, NamespacedKey namespacedKey, Object value){
        this.itemStack = itemStack;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}