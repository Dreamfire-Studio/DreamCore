package com.dreamfirestudios.dreamCore.DreamfireHologram.Event;

import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import com.dreamfirestudios.dreamCore.DreamfireHologram.DreamfireHologram;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class HologramAddLineEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireHologram dreamfireHologram;
    private final String line;

    public HologramAddLineEvent(DreamfireHologram dreamfireHologram, String line){
        this.dreamfireHologram = dreamfireHologram;
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
