/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dreamfirestudios.dreamcore.DreamArmorStand;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.Bukkit;

/// <summary>
/// A builder class for creating and configuring <see cref="ArmorStand"/> instances.
/// </summary>
/// <remarks>
/// Supports chaining to configure properties such as visibility, arms, gravity,
/// size, and custom names.
/// When <see cref="build"/> is called, an <see cref="ArmorStand"/> is spawned
/// in the world and an <see cref="ArmorStandSpawnEvent"/> is fired.
/// </remarks>
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

    /// <summary>
    /// Creates a new <see cref="DreamfireArmorStandBuilder"/> at the given location.
    /// </summary>
    /// <param name="location">The spawn location for the armor stand. Must not be null and must have a world.</param>
    /// <exception cref="IllegalArgumentException">Thrown if <paramref name="location"/> or its world is null.</exception>
    public DreamfireArmorStandBuilder(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location or World cannot be null");
        }
        this.location = location;
    }

    /// <summary>
    /// Sets the visibility of the armor stand.
    /// </summary>
    public DreamfireArmorStandBuilder visible(boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand's custom name is visible.
    /// </summary>
    public DreamfireArmorStandBuilder customNameVisible(boolean customNameVisible) {
        this.customNameVisible = customNameVisible;
        return this;
    }

    /// <summary>
    /// Sets the custom name of the armor stand, applying DreamMessage formatting.
    /// </summary>
    public DreamfireArmorStandBuilder customName(String customName) {
        this.customName = PlainTextComponentSerializer.plainText().serialize(
                DreamMessageFormatter.format(customName, DreamMessageSettings.all())
        );
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand can pick up items.
    /// </summary>
    public DreamfireArmorStandBuilder canPickupItems(boolean canPickupItems) {
        this.canPickupItems = canPickupItems;
        return this;
    }

    /// <summary>
    /// Sets whether gravity is applied to the armor stand.
    /// </summary>
    public DreamfireArmorStandBuilder gravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand has arms.
    /// </summary>
    public DreamfireArmorStandBuilder arms(boolean arms) {
        this.arms = arms;
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand has a base plate.
    /// </summary>
    public DreamfireArmorStandBuilder basePlate(boolean basePlate) {
        this.basePlate = basePlate;
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand is small.
    /// </summary>
    public DreamfireArmorStandBuilder small(boolean small) {
        this.small = small;
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand is a marker (no hitbox).
    /// </summary>
    public DreamfireArmorStandBuilder marker(boolean marker) {
        this.marker = marker;
        return this;
    }

    /// <summary>
    /// Sets whether the armor stand is glowing.
    /// </summary>
    public DreamfireArmorStandBuilder glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    /// <summary>
    /// Builds and spawns the configured <see cref="ArmorStand"/> at the location.
    /// </summary>
    /// <returns>The spawned <see cref="ArmorStand"/>.</returns>
    /// <remarks>
    /// Automatically fires an <see cref="ArmorStandSpawnEvent"/> after spawning.
    /// </remarks>
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

        ArmorStandSpawnEvent event = new ArmorStandSpawnEvent(
                armorStand, location, isVisible, customNameVisible, customName, canPickupItems, gravity
        );
        Bukkit.getPluginManager().callEvent(event);

        return armorStand;
    }
}