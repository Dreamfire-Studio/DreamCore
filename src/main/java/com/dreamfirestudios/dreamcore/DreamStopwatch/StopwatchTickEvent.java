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
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired each tick period while a <see cref="DreamStopwatch"/> is running.
/// </summary>
@Getter
public class StopwatchTickEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The stopwatch instance.</summary>
    private final DreamStopwatch stopwatch;
    /// <summary>Elapsed seconds at this tick.</summary>
    private final int elapsedSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public StopwatchTickEvent(DreamStopwatch stopwatch, int elapsedSeconds) {
        this.stopwatch = stopwatch;
        this.elapsedSeconds = elapsedSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}