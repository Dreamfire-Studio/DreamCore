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
package com.dreamfirestudios.dreamcore.DreamItemDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired immediately after an <see cref="ItemDisplay"/> is spawned and configured
/// by <see cref="DreamItemDisplay.ItemDisplayBuilder#spawn(org.bukkit.inventory.ItemStack)"/>.
/// </summary>
/// <remarks>
/// This event currently calls itself through the plugin manager inside its constructor,
/// matching the pattern shown. If you prefer consistency with your other events,
/// we can switch to a static <c>fire(...)</c> helper (no behavior changes).
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onItemDisplaySpawn(ItemDisplaySpawnEvent e) {
///     e.getItemDisplay().setPersistent(true);
/// }
/// </code>
/// </example>
@Getter
public class ItemDisplaySpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /// <summary>The newly spawned <see cref="ItemDisplay"/>.</summary>
    private final ItemDisplay itemDisplay;

    /// <summary>
    /// Constructs and immediately fires the event through the plugin manager.
    /// </summary>
    /// <param name="itemDisplay">The spawned <see cref="ItemDisplay"/>.</param>
    public ItemDisplaySpawnEvent(ItemDisplay itemDisplay){
        this.itemDisplay = itemDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Static accessor required by Bukkit to obtain the shared handler list.
    /// </summary>
    /// <returns>The shared handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <summary>
    /// Instance accessor required by Bukkit for listener registration.
    /// </summary>
    /// <returns>The shared handler list.</returns>
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}