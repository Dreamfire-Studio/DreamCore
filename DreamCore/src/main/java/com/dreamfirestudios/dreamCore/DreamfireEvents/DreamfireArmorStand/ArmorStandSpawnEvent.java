package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ArmorStandSpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ArmorStand armorStand;
    private final Location location;
    private final boolean isVisible;
    private final boolean customNameVisible;
    private final String customName;
    private final boolean canPickupItems;
    private final boolean useGravity;

    public ArmorStandSpawnEvent(ArmorStand armorStand, Location location, boolean isVisible, boolean customNameVisible, String customName, boolean canPickupItems, boolean useGravity){
        this.armorStand = armorStand;
        this.location = location;
        this.isVisible = isVisible;
        this.customNameVisible = customNameVisible;
        this.customName = customName;
        this.canPickupItems = canPickupItems;
        this.useGravity = useGravity;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
