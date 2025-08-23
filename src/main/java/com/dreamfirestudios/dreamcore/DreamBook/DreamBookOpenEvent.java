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
package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBook"/> is about to be opened for a player.
/// </summary>
/// <remarks>
/// This event is <see cref="Cancellable"/>. Cancelling prevents the open operation,
/// depending on how listeners and the caller handle the cancellation state.
/// The event is dispatched from the constructor (instantiation triggers <c>callEvent</c>).
/// </remarks>
@Getter
public class DreamBookOpenEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The book that is being opened.
    /// </summary>
    private final DreamBook dreamfireBook;

    /// <summary>
    /// The target player for whom the book is being opened.
    /// </summary>
    private final Player player;

    private boolean cancelled;

    /// <summary>
    /// Constructs and dispatches a new <see cref="DreamBookOpenEvent"/>.
    /// </summary>
    /// <param name="dreamfireBook">The book to open.</param>
    /// <param name="player">The target player.</param>
    public DreamBookOpenEvent(DreamBook dreamfireBook, Player player) {
        this.dreamfireBook = dreamfireBook;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    /// <inheritdoc/>
    @Override
    public boolean isCancelled() { return cancelled; }

    /// <inheritdoc/>
    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}