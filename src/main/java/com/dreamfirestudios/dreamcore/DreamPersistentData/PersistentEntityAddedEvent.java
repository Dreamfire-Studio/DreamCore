/*  Copyright (c) Dreamfire Studios
 *  DocFX-friendly XML docs
 */

package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is added to an <see cref="Entity"/>.
/// </summary>
/// <remarks>
/// Not cancellable. Fired immediately after the container is updated.
/// </remarks>
@Getter
public class PersistentEntityAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Entity that received the entry.</summary>
    private final Entity entity;

    /// <summary>Key under which the value was stored.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>The stored value.</summary>
    private final Object value;

    /// <summary>Creates and dispatches the event.</summary>
    public PersistentEntityAddedEvent(Entity entity, NamespacedKey namespacedKey, Object value){
        this.entity = entity;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}