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
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamScoreboard"/> is created via the builder.
/// </summary>
/// <remarks>
/// If cancelled, the scoreboard will NOT be stored in <c>DreamCore.DreamScoreboards</c>.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onCreated(ScoreboardCreatedEvent e) {
///     // e.setCancelled(true);
/// }
/// </code>
/// </example>
public class ScoreboardCreatedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The created scoreboard.</summary>
    @Getter
    private final DreamScoreboard dreamfireScoreboard;

    private boolean cancelled;

    /// <summary>Constructs and dispatches the event.</summary>
    public ScoreboardCreatedEvent(DreamScoreboard dreamfireScoreboard) {
        this.dreamfireScoreboard = dreamfireScoreboard;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    public @NotNull HandlerList getHandlers() { return handlers; }

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