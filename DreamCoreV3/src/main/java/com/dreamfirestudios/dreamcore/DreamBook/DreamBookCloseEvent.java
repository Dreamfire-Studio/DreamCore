package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DreamBookCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamBook dreamBook;
    private final Player player;

    public DreamBookCloseEvent(DreamBook dreamfireBook, Player player){
        this.dreamBook = dreamfireBook;
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
