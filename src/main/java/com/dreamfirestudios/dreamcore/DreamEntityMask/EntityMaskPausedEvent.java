package com.dreamfirestudios.dreamcore.DreamEntityMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamEntityMask"/> is paused.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to restore visibility, cancel scheduled tasks,
/// or notify systems that the mask is inactive.
/// </remarks>
@Getter
public class EntityMaskPausedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The mask that was paused.</summary>
    private final DreamEntityMask dreamfireEntityMask;

    /// <summary>
    /// Constructs and dispatches a new <see cref="EntityMaskPausedEvent"/>.
    /// </summary>
    /// <param name="dreamfireEntityMask">The paused mask.</param>
    public EntityMaskPausedEvent(DreamEntityMask dreamfireEntityMask){
        this.dreamfireEntityMask = dreamfireEntityMask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list for Bukkit’s event system.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}