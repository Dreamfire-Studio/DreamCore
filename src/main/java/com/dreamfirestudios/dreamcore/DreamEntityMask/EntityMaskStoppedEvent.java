package com.dreamfirestudios.dreamcore.DreamEntityMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamEntityMask"/> is stopped and removed.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// It is typically fired after all entities have been restored
/// and the mask is unregistered from <c>DreamCore.DreamEntityMasks</c>.
/// </remarks>
@Getter
public class EntityMaskStoppedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The mask that was stopped.</summary>
    private final DreamEntityMask dreamfireEntityMask;

    /// <summary>
    /// Constructs and dispatches a new <see cref="EntityMaskStoppedEvent"/>.
    /// </summary>
    /// <param name="dreamfireEntityMask">The stopped mask.</param>
    public EntityMaskStoppedEvent(DreamEntityMask dreamfireEntityMask){
        this.dreamfireEntityMask = dreamfireEntityMask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list for Bukkit’s event system.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}