package com.dreamfirestudios.dreamcore.DreamBossBar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class BossBarFrameAdvancedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DreamBossBar bossBar;
    private final int appliedFrameIndex;

    public BossBarFrameAdvancedEvent(DreamBossBar bossBar, int appliedFrameIndex) {
        this.bossBar = bossBar;
        this.appliedFrameIndex = appliedFrameIndex;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}