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
package com.dreamfirestudios.dreamcore.DreamCam;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a player leaves a <see cref="DreamCamPath"/> (either manually or at the end).
/// </summary>
/// <remarks>
/// Not cancellable. Use <see cref="#isHasFinished()"/> to distinguish normal completion versus early exit.
/// Dispatched from the constructor.
/// </remarks>
@Getter
public final class CamPathPlayerLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The camera path being left.</summary>
    private final DreamCamPath camPath;

    /// <summary>The player leaving the path.</summary>
    private final Player player;

    /// <summary><c>true</c> if the path completed normally; otherwise the player exited early.</summary>
    private final boolean hasFinished;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The camera path.</param>
    /// <param name="player">The player leaving.</param>
    /// <param name="hasFinished">True if the path completed normally.</param>
    public CamPathPlayerLeaveEvent(DreamCamPath camPath, Player player, boolean hasFinished) {
        this.camPath = camPath;
        this.player = player;
        this.hasFinished = hasFinished;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}