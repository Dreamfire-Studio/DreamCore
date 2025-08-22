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
/// Event fired when a persistent data entry is removed from an <see cref="Entity"/>.
/// </summary>
/// <remarks>
/// Not cancellable. Fired after the container is updated.
/// </remarks>
@Getter
public class PersistentEntityRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Entity from which the key was removed.</summary>
    private final Entity entity;

    /// <summary>Key that was removed.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>Creates and dispatches the event.</summary>
    public PersistentEntityRemovedEvent(Entity entity, NamespacedKey namespacedKey){
        this.entity = entity;
        this.namespacedKey = namespacedKey;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}