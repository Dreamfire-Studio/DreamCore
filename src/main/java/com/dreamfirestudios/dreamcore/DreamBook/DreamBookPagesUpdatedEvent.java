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