package com.dreamfirestudios.dreamCore.DreamfireVanish.Event;

import com.dreamfirestudios.dreamCore.DreamfireArmorStand.ArmorStandSlot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


@Getter
public class VanishHideTargetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Entity target;
    private final Player viewer;

    public VanishHideTargetEvent(Entity target, Player viewer){
        this.target = target;
        this.viewer = viewer;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
