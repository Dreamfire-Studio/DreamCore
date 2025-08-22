package com.dreamfirestudios.dreamcore.DreamEntityMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamEntityMask"/> is created and registered for a player.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Typical usage:
/// <list type="bullet">
///   <item>Initialize per-player data structures tied to the mask.</item>
///   <item>Register listeners or schedulers bound to this mask instance.</item>
/// </list>
/// </remarks>
@Getter
public class EntityMaskCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The newly created mask.</summary>
    private final DreamEntityMask dreamfireEntityMask;

    /// <summary>The player that owns the mask.</summary>
    private final Player player;

    /// <summary>
    /// Constructs and dispatches a new <see cref="EntityMaskCreateEvent"/>.
    /// </summary>
    /// <param name="dreamfireEntityMask">The created mask.</param>
    /// <param name="player">The owning player.</param>
    public EntityMaskCreateEvent(DreamEntityMask dreamfireEntityMask, Player player){
        this.dreamfireEntityMask = dreamfireEntityMask;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list for Bukkit’s event system.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}