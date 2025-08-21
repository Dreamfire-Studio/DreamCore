package com.dreamfirestudios.dreamcore.DreamBook;

import com.dreamfirestudios.dreamcore.DreamBook.DreamBook;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// <summary>
/// Fired when the pages of a <see cref="DreamfireBook"/> are updated (set/added/cleared).
/// </summary>
@Getter
public class DreamBookPagesUpdatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DreamBook book;
    private final List<String> pages; // snapshot of pages

    public DreamBookPagesUpdatedEvent(DreamBook book, List<String> pages) {
        this.book = book;
        this.pages = pages;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}