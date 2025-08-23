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
package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a hologram has been created and its initial lines spawned.
/// </summary>
/// <remarks>
/// Emitted by the builder when a hologram instance is registered and initial lines are added.
/// Useful for attaching behaviors (e.g., scheduled updates) right after creation.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onSpawn(HologramSpawnEvent e) {
///     // schedule an animation tick
///     scheduler.runTaskTimer(plugin, e.getHologram()::displayNextFrame, 20L, 20L);
/// }
/// </code>
/// </example>
@Getter
public class HologramSpawnEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>The hologram that has just spawned.</summary>
    private final DreamHologram hologram;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">The hologram that spawned.</param>
    public HologramSpawnEvent(@NotNull DreamHologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /// <summary>
    /// Fires this event via the Bukkit plugin manager.
    /// </summary>
    /// <param name="hologram">The newly spawned hologram.</param>
    public static void fire(@NotNull DreamHologram hologram) {
        Bukkit.getPluginManager().callEvent(new HologramSpawnEvent(hologram));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}