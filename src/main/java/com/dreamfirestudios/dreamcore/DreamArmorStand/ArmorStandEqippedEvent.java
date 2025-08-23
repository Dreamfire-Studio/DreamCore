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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when an <see cref="ArmorStand"/> is equipped with an item in a specific slot.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// It provides the armor stand reference, the item being equipped, and the slot that was modified.
/// </remarks>
@Getter
public class ArmorStandEqippedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The armor stand that was equipped.
    /// </summary>
    private final ArmorStand armorStand;

    /// <summary>
    /// The item equipped into the slot.
    /// </summary>
    private final ItemStack item;

    /// <summary>
    /// The slot of the armor stand that was equipped.
    /// </summary>
    private final ArmorStandSlot slot;

    /// <summary>
    /// Creates a new <see cref="ArmorStandEqippedEvent"/>.
    /// </summary>
    /// <param name="armorStand">The armor stand being equipped.</param>
    /// <param name="item">The item placed in the slot.</param>
    /// <param name="slot">The slot that was modified.</param>
    public ArmorStandEqippedEvent(ArmorStand armorStand, ItemStack item, ArmorStandSlot slot) {
        this.armorStand = armorStand;
        this.item = item;
        this.slot = slot;
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