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
package com.dreamfirestudios.dreamcore.DreamBlockDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event triggered when a <see cref="BlockDisplay"/> entity is created.
/// </summary>
/// <remarks>
/// This event is fired immediately after the display has been spawned,
/// allowing listeners to configure it. It is not cancellable.
/// </remarks>
@Getter
public class BlockDisplayCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The created <see cref="BlockDisplay"/> instance.
    /// </summary>
    private final BlockDisplay blockDisplay;

    /// <summary>
    /// Initializes a new instance of the <see cref="BlockDisplayCreatedEvent"/>.
    /// </summary>
    /// <param name="blockDisplay">The created block display entity.</param>
    public BlockDisplayCreatedEvent(BlockDisplay blockDisplay) {
        this.blockDisplay = blockDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the static handler list for this event type.
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
}