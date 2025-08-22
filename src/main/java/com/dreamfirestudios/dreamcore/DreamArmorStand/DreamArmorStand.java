package com.dreamfirestudios.dreamcore.DreamArmorStand;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/// <summary>
/// Utility class for performing operations on <see cref="ArmorStand"/> entities,
/// such as equipping items, posing, toggling visibility, gravity, glowing,
/// teleporting, naming, and cloning.
/// </summary>
public class DreamArmorStand {

    /// <summary>
    /// Equips an armor stand with an item in a specific slot.
    /// </summary>
    /// <param name="armorStand">The armor stand to equip.</param>
    /// <param name="item">The item to equip.</param>
    /// <param name="slot">The slot to equip the item in.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> or <paramref name="item"/> is <c>null</c>.
    /// </exception>
    public static void equipArmorStand(ArmorStand armorStand, ItemStack item, ArmorStandSlot slot) {
        if (armorStand == null || item == null) throw new IllegalArgumentException("ArmorStand or Item cannot be null");

        switch (slot) {
            case HEAD -> armorStand.getEquipment().setHelmet(item);
            case CHEST -> armorStand.getEquipment().setChestplate(item);
            case LEGS -> armorStand.getEquipment().setLeggings(item);
            case FEET -> armorStand.getEquipment().setBoots(item);
            case HAND -> armorStand.getEquipment().setItemInMainHand(item);
            case OFFHAND -> armorStand.getEquipment().setItemInOffHand(item);
            default -> throw new IllegalArgumentException("Unknown ArmorStand slot: " + slot);
        }

        new ArmorStandEqippedEvent(armorStand, item, slot);
    }

    /// <summary>
    /// Sets the pose of the armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to pose.</param>
    /// <param name="pose">The body part pose to set.</param>
    /// <param name="angleX">The X-axis rotation angle (in degrees).</param>
    /// <param name="angleY">The Y-axis rotation angle (in degrees).</param>
    /// <param name="angleZ">The Z-axis rotation angle (in degrees).</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> or <paramref name="pose"/> is <c>null</c>.
    /// </exception>
    public static void setPose(ArmorStand armorStand, ArmorStandPose pose, float angleX, float angleY, float angleZ) {
        if (armorStand == null || pose == null) throw new IllegalArgumentException("ArmorStand or Pose cannot be null");

        var rotation = new org.bukkit.util.EulerAngle(Math.toRadians(angleX), Math.toRadians(angleY), Math.toRadians(angleZ));

        switch (pose) {
            case HEAD -> armorStand.setHeadPose(rotation);
            case BODY -> armorStand.setBodyPose(rotation);
            case LEFT_ARM -> armorStand.setLeftArmPose(rotation);
            case RIGHT_ARM -> armorStand.setRightArmPose(rotation);
            case LEFT_LEG -> armorStand.setLeftLegPose(rotation);
            case RIGHT_LEG -> armorStand.setRightLegPose(rotation);
        }

        new ArmorStandPosedEvent(armorStand, pose, angleX, angleY, angleZ);
    }

    /// <summary>
    /// Toggles the visibility of the armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to toggle visibility for.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> is <c>null</c>.
    /// </exception>
    public static void toggleVisibility(ArmorStand armorStand) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setVisible(!armorStand.isVisible());
    }

    /// <summary>
    /// Disables gravity for the armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to disable gravity for.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> is <c>null</c>.
    /// </exception>
    public static void disableGravity(ArmorStand armorStand) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setGravity(false);
    }

    /// <summary>
    /// Enables gravity for the armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to enable gravity for.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> is <c>null</c>.
    /// </exception>
    public static void enableGravity(ArmorStand armorStand) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setGravity(true);
    }

    /// <summary>
    /// Sets the glowing effect for the armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to set glowing for.</param>
    /// <param name="glowing">Whether the armor stand should glow.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> is <c>null</c>.
    /// </exception>
    public static void setGlowing(ArmorStand armorStand, boolean glowing) {
        if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
        armorStand.setGlowing(glowing);
        new ArmorStandGlowEvent(armorStand, glowing);
    }

    /// <summary>
    /// Teleports the armor stand to a new location.
    /// </summary>
    /// <param name="armorStand">The armor stand to teleport.</param>
    /// <param name="newLocation">The location to teleport to.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> or <paramref name="newLocation"/> is <c>null</c>.
    /// </exception>
    public static void teleportArmorStand(ArmorStand armorStand, Location newLocation) {
        if (armorStand == null || newLocation == null) throw new IllegalArgumentException("ArmorStand or Location cannot be null");
        armorStand.teleport(newLocation);
    }

    /// <summary>
    /// Sets a custom name for the armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to set the name for.</param>
    /// <param name="customName">The custom name to assign.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="armorStand"/> or <paramref name="customName"/> is <c>null</c>.
    /// </exception>
    public static void setCustomName(ArmorStand armorStand, String customName) {
        if (armorStand == null || customName == null) throw new IllegalArgumentException("ArmorStand or CustomName cannot be null");
        armorStand.customName(DreamMessageFormatter.format(customName, DreamMessageSettings.all()));
    }

    /// <summary>
    /// Clones an armor stand by creating a new instance with the same properties.
    /// </summary>
    /// <param name="original">The original armor stand to clone.</param>
    /// <returns>A new armor stand with the same properties as the original.</returns>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="original"/> is <c>null</c>.
    /// </exception>
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