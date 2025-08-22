package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class LocationLimiterLimitHit extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamLocationLimiter limiter;
    private final Player player;

    public LocationLimiterLimitHit(DreamLocationLimiter limiter, Player player) {
        this.limiter = limiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}