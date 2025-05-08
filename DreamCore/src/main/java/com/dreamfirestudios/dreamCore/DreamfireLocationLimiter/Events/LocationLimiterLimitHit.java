package com.dreamfirestudios.dreamCore.DreamfireLocationLimiter.Events;

import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import com.dreamfirestudios.dreamCore.DreamfireLocationLimiter.DreamfireLocationLimiter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class LocationLimiterLimitHit extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireLocationLimiter dreamfireLocationLimiter;
    private final Player player;

    public LocationLimiterLimitHit(DreamfireLocationLimiter dreamfireLocationLimiter, Player player){
        this.dreamfireLocationLimiter = dreamfireLocationLimiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
