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
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/// <summary>
/// Fired after a frame’s block changes are sent to the player.
/// </summary>
/// <remarks>
/// Not cancellable. Dispatched from the constructor (construction triggers <c>callEvent</c>).
/// </remarks>
@Getter
public class BlockMaskFrameAppliedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Target player who received the frame updates.</summary>
    private final Player player;

    /// <summary>The mask that produced this frame.</summary>
    private final DreamBlockMask mask;

    /// <summary>
    /// The map of world positions to the <see cref="BlockState"/> values that were applied in this frame.
    /// </summary>
    private final Map<Vector, BlockState> appliedStates;

    /// <summary>
    /// Constructs and dispatches the event after a frame is sent to a player.
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="mask">Owning mask.</param>
    /// <param name="appliedStates">Map of applied states keyed by block location vector.</param>
    public BlockMaskFrameAppliedEvent(Player player,
                                      DreamBlockMask mask,
                                      Map<Vector, BlockState> appliedStates) {
        this.player = player;
        this.mask = mask;
        this.appliedStates = appliedStates;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}