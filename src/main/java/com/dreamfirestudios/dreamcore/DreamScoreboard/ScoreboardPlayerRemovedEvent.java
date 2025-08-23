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
package com.dreamfirestudios.dreamcore.DreamScoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired before a player is removed from viewing a <see cref="DreamScoreboard"/>.
/// </summary>
/// <remarks>Cancelable. If cancelled, the player will remain a viewer.</remarks>
@Getter
public class ScoreboardPlayerRemovedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>The scoreboard being viewed.</summary>
    private final DreamScoreboard scoreboard;
    /// <summary>The player being removed.</summary>
    private final Player player;
    private boolean cancelled;

    /// <summary>Constructs and dispatches the event.</summary>
    public ScoreboardPlayerRemovedEvent(DreamScoreboard scoreboard, Player player){
        this.scoreboard = scoreboard;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    /// <inheritdoc />
    @Override public boolean isCancelled() { return cancelled; }
    /// <inheritdoc />
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}