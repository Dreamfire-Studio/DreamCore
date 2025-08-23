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

import com.dreamfirestudios.dreamcore.DreamBook.DreamBook;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// <summary>
/// Fired when the pages of a <see cref="DreamBook"/> are updated (set/added/cleared).
/// </summary>
/// <remarks>
/// Not cancellable. Dispatched from the constructor (instantiation triggers <c>callEvent</c>).
/// Listeners receive a snapshot of the current page list via <see cref="#getPages()"/>.
/// </remarks>
@Getter
public class DreamBookPagesUpdatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The book whose pages were updated.
    /// </summary>
    private final DreamBook book;

    /// <summary>
    /// A snapshot list of the book's pages at the time of update.
    /// </summary>
    private final List<String> pages;

    /// <summary>
    /// Constructs and dispatches a new <see cref="DreamBookPagesUpdatedEvent"/>.
    /// </summary>
    /// <param name="book">The book whose pages changed.</param>
    /// <param name="pages">A snapshot of the new page list.</param>
    public DreamBookPagesUpdatedEvent(DreamBook book, List<String> pages) {
        this.book = book;
        this.pages = pages;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}