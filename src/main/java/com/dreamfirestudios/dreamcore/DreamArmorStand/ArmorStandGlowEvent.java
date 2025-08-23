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
/// Event fired when an <see cref="ArmorStand"/> has its glowing state changed.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// It provides the armor stand reference and whether the glowing effect is being enabled or disabled.
/// </remarks>
@Getter
public class ArmorStandGlowEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The armor stand whose glowing state changed.
    /// </summary>
    private final ArmorStand armorStand;

    /// <summary>
    /// Whether the armor stand is glowing (<c>true</c>) or not (<c>false</c>).
    /// </summary>
    private final boolean glowing;

    /// <summary>
    /// Creates a new <see cref="ArmorStandGlowEvent"/>.
    /// </summary>
    /// <param name="armorStand">The armor stand affected.</param>
    /// <param name="glowing">The new glowing state of the armor stand.</param>
    public ArmorStandGlowEvent(ArmorStand armorStand, boolean glowing) {
        this.armorStand = armorStand;
        this.glowing = glowing;
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
