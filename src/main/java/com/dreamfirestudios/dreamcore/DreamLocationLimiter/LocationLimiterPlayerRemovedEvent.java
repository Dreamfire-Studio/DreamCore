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
package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a player is removed from a <see cref="DreamLocationLimiter"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Useful for cleanup actions such as clearing buffs, resetting effects,
/// or notifying other systems.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onLimiterRemove(LocationLimiterPlayerRemovedEvent e) {
///     e.getPlayer().sendMessage("You left the arena limiter.");
/// }
/// </code>
/// </example>
@Getter
public class LocationLimiterPlayerRemovedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamLocationLimiter limiter;
    private final Player player;

    /// <param name="limiter">Limiter that removed the player.</param>
    /// <param name="player">Player removed from the limiter.</param>
    public LocationLimiterPlayerRemovedEvent(DreamLocationLimiter limiter, Player player) {
        this.limiter = limiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
}
