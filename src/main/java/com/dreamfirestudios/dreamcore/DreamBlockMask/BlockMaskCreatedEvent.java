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
package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBlockMask"/> is created and registered for a player.
/// </summary>
/// <remarks>
/// Not cancellable. This event is dispatched immediately from the constructor
/// (i.e., creating an instance will trigger <c>callEvent</c>).
/// Use this to initialize related state for the new mask.
/// </remarks>
@Getter
public final class BlockMaskCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// Player for whom the mask was created.
    /// </summary>
    private final Player player;

    /// <summary>
    /// The mask instance that was created.
    /// </summary>
    private final DreamBlockMask mask;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="mask">The created mask.</param>
    /// <param name="player">The associated player.</param>
    public BlockMaskCreatedEvent(DreamBlockMask mask, Player player) {
        this.player = player;
        this.mask = mask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}