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
/// <remarks>Not cancellable by default.</remarks>
@Getter
public class BossBarPlayerRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DreamBossBar bossBar;
    private final Player player;

    public BossBarPlayerRemovedEvent(DreamBossBar bossBar, Player player) {
        this.bossBar = bossBar;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}