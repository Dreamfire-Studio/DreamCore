package com.dreamfirestudios.dreamcore.DreamBossBar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a player is removed from a <see cref="DreamBossBar"/>.
/// </summary>
/// <remarks>
/// Not cancellable by default. Dispatched from the constructor.
/// </remarks>
@Getter
public class BossBarPlayerRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The boss bar the player was removed from.</summary>
    private final DreamBossBar bossBar;

    /// <summary>The player being removed.</summary>
    private final Player player;

    /// <summary>
    /// Constructs and dispatches a new <see cref="BossBarPlayerRemovedEvent"/>.
    /// </summary>
    /// <param name="bossBar">The source boss bar.</param>
    /// <param name="player">The player removed.</param>
    public BossBarPlayerRemovedEvent(DreamBossBar bossBar, Player player) {
        this.bossBar = bossBar;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}