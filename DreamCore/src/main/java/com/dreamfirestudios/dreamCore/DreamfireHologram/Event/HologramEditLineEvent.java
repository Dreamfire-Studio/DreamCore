package com.dreamfirestudios.dreamCore.DreamfireHologram.Event;

import com.dreamfirestudios.dreamCore.DreamfireHologram.DreamfireHologram;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class HologramEditLineEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireHologram dreamfireHologram;
    private final int index;
    private final String line;

    public HologramEditLineEvent(DreamfireHologram dreamfireHologram, int index, String line){
        this.dreamfireHologram = dreamfireHologram;
        this.index = index;
        this.line = line;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
