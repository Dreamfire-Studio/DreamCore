package com.dreamfirestudios.dreamCore.DreamfireArmorStand;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireChat;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand.ArmorStandEqippedEvent;
import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand.ArmorStandGlowEvent;
import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand.ArmorStandPosedEvent;
import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireArmorStand.ArmorStandSpawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

//TODO animation class to animate armor stand over time
public class DreamfireArmorStand {

    /**
     * Equips an ArmorStand with an item in a specific slot.
     *
     * @param armorStand The ArmorStand to equip.
     * @param item The item to equip.
     * @param slot The slot to equip the item in.
     * @throws IllegalArgumentException If ArmorStand or Item is null.
     */
    public static void equipArmorStand(ArmorStand armorStand, ItemStack item, ArmorStandSlot slot) {
        if (armorStand == null || item == null) throw new IllegalArgumentException("ArmorStand or Item cannot be null");

        switch (slot) {
            case HEAD:
                armorStand.getEquipment().setHelmet(item);
                break;
            case CHEST:
                armorStand.getEquipment().setChestplate(item);
                break;
            case LEGS:
                armorStand.getEquipment().setLeggings(item);
                break;
            case FEET:
                armorStand.getEquipment().setBoots(item);
                break;
            case HAND:
                armorStand.getEquipment().setItemInMainHand(item);
                break;
            case OFFHAND:
                armorStand.getEquipment().setItemInOffHand(item);
                break;
            default:
                throw new IllegalArgumentException("Unknown ArmorStand slot: " + slot);
        }

        new ArmorStandEqippedEvent(armorStand, item, slot);
    }

    /**
     * Sets the pose of the ArmorStand.
     *
     * @param armorStand The ArmorStand to pose.
     * @param pose The pose to set.
     * @param angleX The X-axis rotation angle (in degrees).
     * @param angleY The Y-axis rotation angle (in degrees).
     * @param angleZ The Z-axis rotation angle (in degrees).
     * @throws IllegalArgumentException If ArmorStand or Pose is null.
     */
    public static void setPose(ArmorStand armorStand, ArmorStandPose pose, float angleX, float angleY, float angleZ) {
        if (armorStand == null || pose == null) throw new IllegalArgumentException("ArmorStand or Pose cannot be null");

        var rotation = new org.bukkit.util.EulerAngle(Math.toRadians(angleX), Math.toRadians(angleY), Math.toRadians(angleZ));

        switch (pose) {
            case HEAD:
                armorStand.setHeadPose(rotation);
                break;
            case BODY:
                armorStand.setBodyPose(rotation);
                break;
            case LEFT_ARM:
                armorStand.setLeftArmPose(rotation);
                break;
            case RIGHT_ARM:
                armorStand.setRightArmPose(rotation);
                break;
            case LEFT_LEG:
                armorStand.setLeftLegPose(rotation);
                break;
            case RIGHT_LEG:
                armorStand.setRightLegPose(rotation);
                break;
        }

        new ArmorStandPosedEvent(armorStand, pose, angleX, angleY, angleZ);
    }

    /**
     * Toggles the visibility of the ArmorStand.
     *
     * @param armorStand The ArmorStand to toggle visibility for.
     * @throws IllegalArgumentException If ArmorStand is null.
     */
    public static void toggleVisibility(ArmorStand armorStand) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setVisible(!armorStand.isVisible());
    }

    /**
     * Disables gravity for the ArmorStand.
     *
     * @param armorStand The ArmorStand to disable gravity for.
     * @throws IllegalArgumentException If ArmorStand is null.
     */
    public static void disableGravity(ArmorStand armorStand) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setGravity(false);
    }

    /**
     * Enables gravity for the ArmorStand.
     *
     * @param armorStand The ArmorStand to enable gravity for.
     * @throws IllegalArgumentException If ArmorStand is null.
     */
    public static void enableGravity(ArmorStand armorStand) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setGravity(true);
    }

    /**
     * Sets the glowing effect for the ArmorStand.
     *
     * @param armorStand The ArmorStand to set glowing for.
     * @param glowing Whether the ArmorStand should glow.
     * @throws IllegalArgumentException If ArmorStand is null.
     */
    public static void setGlowing(ArmorStand armorStand, boolean glowing) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setGlowing(glowing);
        new ArmorStandGlowEvent(armorStand, glowing);
    }

    /**
     * Teleports the given ArmorStand to a new location.
     *
     * @param armorStand The ArmorStand to teleport.
     * @param newLocation The location to teleport the ArmorStand to.
     * @throws IllegalArgumentException If ArmorStand or Location is null.
     */
    public static void teleportArmorStand(ArmorStand armorStand, Location newLocation) {
        if (armorStand == null || newLocation == null) throw new IllegalArgumentException("ArmorStand or Location cannot be null");
        armorStand.teleport(newLocation);
    }

    /**
     * Sets a custom name for the given ArmorStand.
     *
     * @param armorStand The ArmorStand to set the custom name for.
     * @param customName The custom name to set for the ArmorStand.
     * @throws IllegalArgumentException If ArmorStand or CustomName is null.
     */
    public static void setCustomName(ArmorStand armorStand, String customName) {
        if (armorStand == null || customName == null) throw new IllegalArgumentException("ArmorStand or CustomName cannot be null");
        armorStand.customName(DreamfireMessage.formatMessage(customName));
    }

    /**
     * Clones the given ArmorStand by creating a new ArmorStand with the same properties.
     * This includes location, visibility, custom name, ability to pick up items, and gravity settings.
     *
     * @param original The ArmorStand to clone.
     * @return A new ArmorStand with the same properties as the original.
     * @throws IllegalArgumentException If the original ArmorStand is null.
     */
    public static ArmorStand cloneArmorStand(ArmorStand original) {
        if (original == null) throw new IllegalArgumentException("Original ArmorStand cannot be null");
        Location loc = original.getLocation();
        return new DreamfireArmorStandBuilder(loc)
                .visible(original.isVisible())
                .customNameVisible(original.isCustomNameVisible())
                .customName(PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(original.customName())))
                .canPickupItems(original.getCanPickupItems())
                .gravity(original.hasGravity())
                .arms(original.hasArms())
                .basePlate(original.hasBasePlate())
                .small(original.isSmall())
                .marker(original.isMarker())
                .glowing(original.isGlowing())
                .build();
    }
}
