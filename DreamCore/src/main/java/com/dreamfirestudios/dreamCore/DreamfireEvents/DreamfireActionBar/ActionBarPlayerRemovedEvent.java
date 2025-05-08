package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ActionBarPlayerRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireActionBar dreamfireActionBar;
    private final Player player;

    public ActionBarPlayerRemovedEvent( DreamfireActionBar dreamfireActionBar, Player player){
        this.dreamfireActionBar = dreamfireActionBar;
        this.player = player;
        Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {Bukkit.getPluginManager().callEvent(this);});
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
