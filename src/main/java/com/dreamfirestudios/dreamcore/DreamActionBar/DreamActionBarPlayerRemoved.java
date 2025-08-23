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
package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a <see cref="Player"/> is removed from a <see cref="DreamActionBar"/>.
/// </summary>
/// <remarks>
/// This event is cancellable. Cancelling it will prevent the player
/// from being removed as a viewer of the action bar.
/// </remarks>
public class DreamActionBarPlayerRemoved extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The action bar instance the player is being removed from.
    /// </summary>
    @Getter
    private final DreamActionBar DreamActionBar;

    /// <summary>
    /// The player being removed from the action bar.
    /// </summary>
    @Getter
    private final Player Player;

    private boolean cancelled;

    /// <summary>
    /// Creates a new instance of the <see cref="DreamActionBarPlayerRemoved"/> event.
    /// </summary>
    /// <param name="dreamActionBar">The action bar from which the player is being removed.</param>
    /// <param name="player">The player being removed.</param>
    public DreamActionBarPlayerRemoved(DreamActionBar dreamActionBar, Player player) {
        DreamActionBar = dreamActionBar;
        Player = player;
        Bukkit.getScheduler().runTask(DreamCore.DreamCore,
                () -> Bukkit.getPluginManager().callEvent(this));
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

    /// <inheritdoc />
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /// <inheritdoc />
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}