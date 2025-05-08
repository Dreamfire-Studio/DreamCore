package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ArmorStandGlowEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ArmorStand armorStand;
    private final boolean glowing;

    public ArmorStandGlowEvent(ArmorStand armorStand, boolean glowing){
        this.armorStand = armorStand;
        this.glowing = glowing;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
