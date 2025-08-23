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
/// Event fired after a hologram has been deleted and all ArmorStands removed.
/// </summary>
/// <remarks>
/// Emitted by <c>DreamHologram#deleteHologram()</c> once removal completes.
/// Use this to clean up any external references or metadata bound to the hologram.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onDelete(HologramDeleteEvent e) {
///     cache.remove(e.getHologram().getClassID());
/// }
/// </code>
/// </example>
@Getter
public class HologramDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>The hologram that was deleted.</summary>
    private final DreamHologram hologram;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">The deleted hologram.</param>
    public HologramDeleteEvent(@NotNull DreamHologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /// <summary>
    /// Fires this event through the Bukkit plugin manager.
    /// </summary>
    /// <param name="hologram">The deleted hologram.</param>
    public static void fire(@NotNull DreamHologram hologram) {
        Bukkit.getPluginManager().callEvent(new HologramDeleteEvent(hologram));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
