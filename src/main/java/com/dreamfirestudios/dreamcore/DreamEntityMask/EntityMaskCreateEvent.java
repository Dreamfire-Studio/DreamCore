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
package com.dreamfirestudios.dreamcore.DreamEntityMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamEntityMask"/> is created and registered for a player.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Typical usage:
/// <list type="bullet">
///   <item>Initialize per-player data structures tied to the mask.</item>
///   <item>Register listeners or schedulers bound to this mask instance.</item>
/// </list>
/// </remarks>
@Getter
public class EntityMaskCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The newly created mask.</summary>
    private final DreamEntityMask dreamfireEntityMask;

    /// <summary>The player that owns the mask.</summary>
    private final Player player;

    /// <summary>
    /// Constructs and dispatches a new <see cref="EntityMaskCreateEvent"/>.
    /// </summary>
    /// <param name="dreamfireEntityMask">The created mask.</param>
    /// <param name="player">The owning player.</param>
    public EntityMaskCreateEvent(DreamEntityMask dreamfireEntityMask, Player player){
        this.dreamfireEntityMask = dreamfireEntityMask;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list for Bukkit’s event system.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}