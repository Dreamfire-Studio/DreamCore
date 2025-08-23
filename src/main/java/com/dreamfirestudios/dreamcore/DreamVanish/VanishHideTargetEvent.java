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
package com.dreamfirestudios.dreamcore.DreamVanish;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a target entity is marked hidden from a specific viewer via
/// <see cref="DreamVanish#hideTargetFromViewer(Entity, Player)"/>.
/// </summary>
/// <remarks>Synchronously dispatched from the constructor.</remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onHide(VanishHideTargetEvent e) {
///     // e.getTarget(), e.getViewer()
/// }
/// </code>
/// </example>
@Getter
public class VanishHideTargetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The hidden target.</summary>
    private final Entity target;
    /// <summary>The viewer who can no longer see the target.</summary>
    private final Player viewer;

    /// <summary>Constructs and dispatches the event.</summary>
    public VanishHideTargetEvent(Entity target, Player viewer){
        this.target = target;
        this.viewer = viewer;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}