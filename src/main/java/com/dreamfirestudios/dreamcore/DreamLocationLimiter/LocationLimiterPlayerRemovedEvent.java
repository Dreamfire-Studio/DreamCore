package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a player is removed from a <see cref="DreamLocationLimiter"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Useful for cleanup actions such as clearing buffs, resetting effects,
/// or notifying other systems.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onLimiterRemove(LocationLimiterPlayerRemovedEvent e) {
///     e.getPlayer().sendMessage("You left the arena limiter.");
/// }
/// </code>
/// </example>
@Getter
public class LocationLimiterPlayerRemovedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamLocationLimiter limiter;
    private final Player player;

    /// <param name="limiter">Limiter that removed the player.</param>
    /// <param name="player">Player removed from the limiter.</param>
    public LocationLimiterPlayerRemovedEvent(DreamLocationLimiter limiter, Player player) {
        this.limiter = limiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
}
