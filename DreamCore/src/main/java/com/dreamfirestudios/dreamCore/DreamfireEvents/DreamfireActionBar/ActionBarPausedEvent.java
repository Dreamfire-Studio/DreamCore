package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ActionBarPausedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireActionBar dreamfireActionBar;

    public ActionBarPausedEvent( DreamfireActionBar dreamfireActionBar){
        this.dreamfireActionBar = dreamfireActionBar;
        Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {Bukkit.getPluginManager().callEvent(this);});
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
