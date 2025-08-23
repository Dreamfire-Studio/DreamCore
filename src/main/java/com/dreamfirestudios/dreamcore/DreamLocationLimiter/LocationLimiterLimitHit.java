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
/// Event fired when a player exceeds the limiter boundary.
/// </summary>
/// <remarks>
/// Fired whenever a player is snapped back to origin or pushed back by a
/// <see cref="DreamLocationLimiter"/>.
/// Can be used to trigger custom warnings, penalties, or effects.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onHit(LocationLimiterLimitHit e) {
///     e.getPlayer().sendMessage("Boundary reached!");
/// }
/// </code>
/// </example>
@Getter
public class LocationLimiterLimitHit extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamLocationLimiter limiter;
    private final Player player;

    /// <param name="limiter">Limiter instance that triggered the event.</param>
    /// <param name="player">Player who hit the limiter boundary.</param>
    public LocationLimiterLimitHit(DreamLocationLimiter limiter, Player player) {
        this.limiter = limiter;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <returns>Handler list for Bukkit event system.</returns>
    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
}
