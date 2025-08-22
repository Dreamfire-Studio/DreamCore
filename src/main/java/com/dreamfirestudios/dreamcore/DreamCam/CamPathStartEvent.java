package com.dreamfirestudios.dreamcore.DreamCam;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamCamPath"/> starts running.
/// </summary>
/// <remarks>
/// Not cancellable. Emitted after players are switched to spectator and state captured.
/// Dispatched from the constructor.
/// </remarks>
@Getter
public final class CamPathStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The started camera path.</summary>
    private final DreamCamPath camPath;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The started camera path.</param>
    public CamPathStartEvent(DreamCamPath camPath) {
        this.camPath = camPath;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}