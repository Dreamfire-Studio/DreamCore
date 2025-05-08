package com.dreamfirestudios.dreamCore.DreamfireBook.Events;

import com.dreamfirestudios.dreamCore.DreamfireBook.DreamfireBook;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DreamfireBookCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireBook dreamfireBook;
    private final Player player;

    public DreamfireBookCloseEvent( DreamfireBook dreamfireBook, Player player){
        this.dreamfireBook = dreamfireBook;
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
