package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a player is about to be added to a <see cref="DreamLocationLimiter"/>.
/// </summary>
/// <remarks>
/// This event is cancellable. If cancelled, the player will not be added.
/// Useful for permission checks, region restrictions, or game logic.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onLimiterAdd(LocationLimiterPlayerAddedEvent e) {
///     if (!e.getPlayer().hasPermission("arena.join")) {
///         e.setCancelled(true);
///     }
/// }
/// </code>
/// </example>
@Getter
public class LocationLimiterPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamLocationLimiter limiter;
    private final Player player;
    private boolean cancelled;

    /// <param name="limiter">Limiter that attempted to add the player.</param>
    /// <param name="player">Player being added.</param>
    public LocationLimiterPlayerAddedEvent(DreamLocationLimiter limiter, Player player) {
        this.limiter = limiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Whether the event is cancelled.</returns>
    @Override
    public boolean isCancelled() { return cancelled; }

    /// <param name="cancelled">True to cancel player addition.</param>
    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
