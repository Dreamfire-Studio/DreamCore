package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand;

import com.dreamfirestudios.dreamCore.DreamfireArmorStand.ArmorStandPose;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ArmorStandPosedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ArmorStand armorStand;
    private final ArmorStandPose pose;
    private final float angleX;
    private final float angleY;
    private final float angleZ;

    public ArmorStandPosedEvent(ArmorStand armorStand, ArmorStandPose pose, float angleX, float angleY, float angleZ){
        this.armorStand = armorStand;
        this.pose = pose;
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}