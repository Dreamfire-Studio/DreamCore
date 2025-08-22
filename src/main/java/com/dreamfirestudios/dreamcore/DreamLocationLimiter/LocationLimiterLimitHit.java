package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a player exceeds the limiter boundary.
/// </summary>
/// <remarks>
/// Fired whenever a player is snapped back to origin or pushed back by a
/// <see cref="DreamLocationLimiter"/>.
/// Can be used to trigger custom warnings, penalties, or effects.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onHit(LocationLimiterLimitHit e) {
///     e.getPlayer().sendMessage("Boundary reached!");
/// }
/// </code>
/// </example>
@Getter
public class LocationLimiterLimitHit extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamLocationLimiter limiter;
    private final Player player;

    /// <param name="limiter">Limiter instance that triggered the event.</param>
    /// <param name="player">Player who hit the limiter boundary.</param>
    public LocationLimiterLimitHit(DreamLocationLimiter limiter, Player player) {
        this.limiter = limiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <returns>Handler list for Bukkit event system.</returns>
    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
}
