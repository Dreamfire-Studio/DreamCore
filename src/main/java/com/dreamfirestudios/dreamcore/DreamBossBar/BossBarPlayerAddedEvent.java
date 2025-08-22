package com.dreamfirestudios.dreamcore.DreamBossBar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired before a player is added to a <see cref="DreamBossBar"/>.
/// </summary>
/// <remarks>
/// This event is <see cref="Cancellable"/>. Cancelling prevents the player from being added,
/// subject to how the caller handles the cancellation state.
/// Dispatched from the constructor.
/// </remarks>
@Getter
public class BossBarPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The boss bar to which the player will be added.</summary>
    private final DreamBossBar bossBar;

    /// <summary>The player to add.</summary>
    private final Player player;

    private boolean cancelled;

    /// <summary>
    /// Constructs and dispatches a new <see cref="BossBarPlayerAddedEvent"/>.
    /// </summary>
    /// <param name="bossBar">The target boss bar.</param>
    /// <param name="player">The player to add.</param>
    public BossBarPlayerAddedEvent(DreamBossBar bossBar, Player player) {
        this.bossBar = bossBar;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }

    /// <inheritdoc/>
    @Override public boolean isCancelled() { return cancelled; }

    /// <inheritdoc/>
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}