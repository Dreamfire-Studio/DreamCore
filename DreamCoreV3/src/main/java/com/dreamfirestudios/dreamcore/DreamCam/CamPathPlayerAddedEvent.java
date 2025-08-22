package com.dreamfirestudios.dreamcore.DreamCam;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired before a player is added to a <see cref="DreamCamPath"/>.
/// </summary>
/// <remarks>
/// Cancellable. If cancelled, the player is not added.
/// </remarks>
@Getter
public final class CamPathPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DreamCamPath camPath;
    private final Player player;
    private boolean cancelled;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The camera path.</param>
    /// <param name="player">The player being added.</param>
    public CamPathPlayerAddedEvent(DreamCamPath camPath, Player player) {
        this.camPath = camPath;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}