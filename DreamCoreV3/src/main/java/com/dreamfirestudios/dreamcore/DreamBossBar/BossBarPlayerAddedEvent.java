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
@Getter
public class BossBarPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final DreamBossBar bossBar;
    private final Player player;
    private boolean cancelled;

    public BossBarPlayerAddedEvent(DreamBossBar bossBar, Player player) {
        this.bossBar = bossBar;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}