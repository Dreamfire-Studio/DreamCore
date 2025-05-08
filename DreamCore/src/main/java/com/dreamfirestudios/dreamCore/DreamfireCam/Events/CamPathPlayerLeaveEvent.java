package com.dreamfirestudios.dreamCore.DreamfireCam.Events;

import com.dreamfirestudios.dreamCore.DreamfireBossBar.DreamfireBossBar;
import com.dreamfirestudios.dreamCore.DreamfireCam.DreamfireCamPath;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CamPathPlayerLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireCamPath dreamfireCamPath;
    private final Player player;
    private final boolean hasFinished;

    public CamPathPlayerLeaveEvent( DreamfireCamPath dreamfireCamPath, Player player, boolean hasFinished){
        this.dreamfireCamPath = dreamfireCamPath;
        this.player = player;
        this.hasFinished = hasFinished;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
