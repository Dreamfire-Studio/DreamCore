package com.dreamfirestudios.dreamcore.DreamEntityMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamEntityMask"/> is resumed (play state).
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to trigger re-application of mask logic,
/// such as hiding entities again when unpaused.
/// </remarks>
@Getter
public class EntityMaskStartedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The mask that was started.</summary>
    private final DreamEntityMask dreamfireEntityMask;

    /// <summary>
    /// Constructs and dispatches a new <see cref="EntityMaskStartedEvent"/>.
    /// </summary>
    /// <param name="dreamfireEntityMask">The resumed mask.</param>
    public EntityMaskStartedEvent(DreamEntityMask dreamfireEntityMask){
        this.dreamfireEntityMask = dreamfireEntityMask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list for Bukkit’s event system.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}