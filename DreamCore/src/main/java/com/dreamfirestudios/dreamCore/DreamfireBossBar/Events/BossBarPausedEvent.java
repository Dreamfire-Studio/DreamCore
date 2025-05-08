package com.dreamfirestudios.dreamCore.DreamfireBossBar.Events;

import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import com.dreamfirestudios.dreamCore.DreamfireBossBar.DreamfireBossBar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class BossBarPausedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireBossBar dreamfireBossBar;

    public BossBarPausedEvent( DreamfireBossBar dreamfireBossBar){
        this.dreamfireBossBar = dreamfireBossBar;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
