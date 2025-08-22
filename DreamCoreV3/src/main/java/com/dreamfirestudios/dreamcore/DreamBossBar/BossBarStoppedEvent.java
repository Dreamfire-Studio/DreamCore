package com.dreamfirestudios.dreamcore.DreamBossBar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBossBar"/> is stopped.
/// </summary>
@Getter
public class BossBarStoppedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DreamBossBar bossBar;

    public BossBarStoppedEvent(DreamBossBar bossBar) {
        this.bossBar = bossBar;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}