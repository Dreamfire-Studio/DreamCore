package com.dreamfirestudios.dreamCore.DreamfireCam.Events;

import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import com.dreamfirestudios.dreamCore.DreamfireCam.DreamfireCamPath;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CamPathPointReachedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireCamPath dreamfireCamPath;
    private final Location location;

    public CamPathPointReachedEvent( DreamfireCamPath dreamfireCamPath, Location location){
        this.dreamfireCamPath = dreamfireCamPath;
        this.location = location;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
