package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand;

import com.dreamfirestudios.dreamCore.DreamfireArmorStand.ArmorStandSlot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class ArmorStandEqippedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ArmorStand armorStand;
    private final ItemStack item;
    private final ArmorStandSlot slot;

    public ArmorStandEqippedEvent(ArmorStand armorStand, ItemStack item, ArmorStandSlot slot){
        this.armorStand = armorStand;
        this.item = item;
        this.slot = slot;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
