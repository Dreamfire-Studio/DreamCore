// src/main/java/com/dreamfirestudios/dreamcore/DreamScoreboard/ScoreboardPlayerAddedEvent.java
package com.dreamfirestudios.dreamcore.DreamScoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScoreboardPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamScoreboard scoreboard;
    private final Player player;
    private boolean cancelled;

    public ScoreboardPlayerAddedEvent(DreamScoreboard scoreboard, Player player){
        this.scoreboard = scoreboard;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}