package com.dreamfirestudios.dreamcore.DreamCam;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired each tick when a path point is reached and applied to players.
/// </summary>
/// <remarks>
/// Not cancellable. Use this to chain FX, sounds, or markers per waypoint.
/// Dispatched from the constructor.
/// </remarks>
@Getter
public final class CamPathPointReachedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The camera path being played.</summary>
    private final DreamCamPath camPath;

    /// <summary>The point reached (the caller should pass a snapshot/clone).</summary>
    private final Location location;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The camera path.</param>
    /// <param name="location">The point reached (cloned snapshot recommended by caller).</param>
    public CamPathPointReachedEvent(DreamCamPath camPath, Location location) {
        this.camPath = camPath;
        this.location = location;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}