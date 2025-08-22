// src/main/java/com/dreamfirestudios/dreamcore/DreamScoreboard/ScoreboardPlayerRemovedEvent.java
package com.dreamfirestudios.dreamcore.DreamScoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired before a player is removed from viewing a <see cref="DreamScoreboard"/>.
/// </summary>
/// <remarks>Cancelable. If cancelled, the player will remain a viewer.</remarks>
@Getter
public class ScoreboardPlayerRemovedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>The scoreboard being viewed.</summary>
    private final DreamScoreboard scoreboard;
    /// <summary>The player being removed.</summary>
    private final Player player;
    private boolean cancelled;

    /// <summary>Constructs and dispatches the event.</summary>
    public ScoreboardPlayerRemovedEvent(DreamScoreboard scoreboard, Player player){
        this.scoreboard = scoreboard;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    /// <inheritdoc />
    @Override public boolean isCancelled() { return cancelled; }
    /// <inheritdoc />
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}