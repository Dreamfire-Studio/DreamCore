package com.dreamfirestudios.dreamCore.DreamfireArmorStand;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand.ArmorStandSpawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.Bukkit;

public class DreamfireArmorStandBuilder {
    private final Location location;
    private boolean isVisible = true;
    private boolean customNameVisible = false;
    private String customName = "";
    private boolean canPickupItems = false;
    private boolean gravity = true;
    private boolean arms = false;
    private boolean basePlate = true;
    private boolean small = false;
    private boolean marker = false;
    private boolean glowing = false;

    public DreamfireArmorStandBuilder(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location or World cannot be null");
        }
        this.location = location;
    }

    public DreamfireArmorStandBuilder visible(boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    public DreamfireArmorStandBuilder customNameVisible(boolean customNameVisible) {
        this.customNameVisible = customNameVisible;
        return this;
    }

    public DreamfireArmorStandBuilder customName(String customName) {
        this.customName = PlainTextComponentSerializer.plainText().serialize(DreamfireMessage.formatMessage(customName));
        return this;
    }

    public DreamfireArmorStandBuilder canPickupItems(boolean canPickupItems) {
        this.canPickupItems = canPickupItems;
        return this;
    }

    public DreamfireArmorStandBuilder gravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public DreamfireArmorStandBuilder arms(boolean arms) {
        this.arms = arms;
        return this;
    }

    public DreamfireArmorStandBuilder basePlate(boolean basePlate) {
        this.basePlate = basePlate;
        return this;
    }

    public DreamfireArmorStandBuilder small(boolean small) {
        this.small = small;
        return this;
    }

    public DreamfireArmorStandBuilder marker(boolean marker) {
        this.marker = marker;
        return this;
    }

    public DreamfireArmorStandBuilder glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public ArmorStand build() {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(isVisible);
        armorStand.setCustomNameVisible(customNameVisible);
        armorStand.customName(Component.text(customName));
        armorStand.setCanPickupItems(canPickupItems);
        armorStand.setGravity(gravity);
        armorStand.setArms(arms);
        armorStand.setBasePlate(basePlate);
        armorStand.setSmall(small);
        armorStand.setMarker(marker);
        armorStand.setGlowing(glowing);
        ArmorStandSpawnEvent event = new ArmorStandSpawnEvent(armorStand, location, isVisible, customNameVisible, customName, canPickupItems, gravity);
        Bukkit.getPluginManager().callEvent(event);
        return armorStand;
    }
}