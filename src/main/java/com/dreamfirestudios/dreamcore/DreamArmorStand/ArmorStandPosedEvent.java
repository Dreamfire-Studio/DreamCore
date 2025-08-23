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

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when an <see cref="ArmorStand"/> has one of its poses changed.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// It provides the armor stand reference, the body part pose that was modified,
/// and the new rotation angles in radians.
/// </remarks>
@Getter
public class ArmorStandPosedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The armor stand whose pose was changed.
    /// </summary>
    private final ArmorStand armorStand;

    /// <summary>
    /// The body part pose that was modified.
    /// </summary>
    private final ArmorStandPose pose;

    /// <summary>
    /// The new rotation angle on the X axis (in radians).
    /// </summary>
    private final float angleX;

    /// <summary>
    /// The new rotation angle on the Y axis (in radians).
    /// </summary>
    private final float angleY;

    /// <summary>
    /// The new rotation angle on the Z axis (in radians).
    /// </summary>
    private final float angleZ;

    /// <summary>
    /// Creates a new <see cref="ArmorStandPosedEvent"/>.
    /// </summary>
    /// <param name="armorStand">The armor stand whose pose was modified.</param>
    /// <param name="pose">The body part pose being changed.</param>
    /// <param name="angleX">The new X-axis angle (radians).</param>
    /// <param name="angleY">The new Y-axis angle (radians).</param>
    /// <param name="angleZ">The new Z-axis angle (radians).</param>
    public ArmorStandPosedEvent(ArmorStand armorStand, ArmorStandPose pose, float angleX, float angleY, float angleZ) {
        this.armorStand = armorStand;
        this.pose = pose;
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the handler list for this event.
    /// </summary>
    /// <returns>The static handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <inheritdoc />
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}