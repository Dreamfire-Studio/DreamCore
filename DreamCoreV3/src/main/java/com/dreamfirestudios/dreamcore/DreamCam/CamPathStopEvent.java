package com.dreamfirestudios.dreamcore.DreamCam;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamCamPath"/> stops (after restoring player states).
/// </summary>
/// <remarks>
/// Not cancellable.
/// </remarks>
@Getter
public final class CamPathStopEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamCamPath camPath;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The stopped camera path.</param>
    public CamPathStopEvent(DreamCamPath camPath) {
        this.camPath = camPath;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}