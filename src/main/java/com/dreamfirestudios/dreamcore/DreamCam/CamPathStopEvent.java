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
/// Not cancellable. Dispatched from the constructor.
/// </remarks>
@Getter
public final class CamPathStopEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The stopped camera path.</summary>
    private final DreamCamPath camPath;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The stopped camera path.</param>
    public CamPathStopEvent(DreamCamPath camPath) {
        this.camPath = camPath;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}