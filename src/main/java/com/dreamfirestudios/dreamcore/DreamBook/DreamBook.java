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

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import com.dreamfirestudios.dreamcore.DreamPersistentData.DreamPersistentItemStack;
import lombok.Getter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 /// <summary>
 /// Represents a server-controlled written book with viewer tracking and utility helpers.
 /// </summary>
 /// <remarks>
 /// Use <see cref="BookBuilder"/> to create instances.
 /// Fires events when the book is opened/closed, pages change, or viewers list changes.
 /// </remarks>
 */
public class DreamBook extends DreamClassID {

    /// <summary>Persistent key for tagging book items in NBT.</summary>
    public static final String DreamfireBookKey = "DreamfireBook";

    @Getter private ItemStack book;
    @Getter private BookMeta bookMeta;

    private final List<String> pages = new ArrayList<>();
    private final List<UUID> viewers = new ArrayList<>();

    // --------------------------
    // Viewer helpers
    // --------------------------

    /// <summary>
    /// Checks whether a player is currently viewing this book.
    /// </summary>
    /// <param name="player">Player to check.</param>
    /// <returns><c>true</c> if the player is a current viewer; otherwise <c>false</c>.</returns>
    public boolean isPlayerInBook(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        return viewers.contains(player.getUniqueId());
    }

    /// <summary>
    /// Opens the book for a player and records them as a viewer.
    /// </summary>
    /// <param name="player">Player to open the book for.</param>
    public void openBook(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");

        player.closeInventory(); // ensure a clean open
        var openEvent = new DreamBookOpenEvent(this, player);
        if (openEvent.isCancelled()) return;

        player.openBook(book);

        UUID id = player.getUniqueId();
        if (!viewers.contains(id)) {
            var added = new DreamBookViewerAddedEvent(this, player);
            if (!added.isCancelled()) {
                viewers.add(id);
            }
        }
    }

    /// <summary>
    /// Opens the book for a player for a fixed duration (seconds) before closing.
    /// </summary>
    /// <param name="player">Player to open the book for.</param>
    /// <param name="durationInSeconds">Duration until auto-close (seconds).</param>
    public void openBook(Player player, int durationInSeconds) {
        if (durationInSeconds < 0) throw new IllegalArgumentException("Duration cannot be negative.");
        openBook(player);
        Bukkit.getScheduler().runTaskLater(
                DreamCore.DreamCore,
                () -> closeBook(player),
                durationInSeconds * 20L
        );
    }

    /// <summary>
    /// Closes the book for a player and removes them from the viewer list.
    /// </summary>
    /// <param name="player">Player to close the book for.</param>
    public void closeBook(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");

        UUID id = player.getUniqueId();
        if (!viewers.contains(id)) return;

        // Force-close sequence (reliable across client versions)
        player.openInventory(Bukkit.createInventory(null, 9));
        player.closeInventory();

        new DreamBookCloseEvent(this, player);

        var removed = new DreamBookViewerRemovedEvent(this, player);
        if (!removed.isCancelled()) {
            viewers.remove(id);
        }
    }

    /// <summary>
    /// Should be called when a player leaves; ensures the book is closed for them.
    /// </summary>
    /// <param name="player">Player who quit.</param>
    public void playerQuit(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        closeBook(player);
    }

    // --------------------------
    // Pages helpers
    // --------------------------

    /// <summary>
    /// Returns an immutable copy of current pages.
    /// </summary>
    public List<String> getPagesView() {
        return Collections.unmodifiableList(new ArrayList<>(pages));
    }

    /// <summary>
    /// Replaces all pages and updates the underlying BookMeta.
    /// </summary>
    /// <param name="newPages">New pages to set (plain text).</param>
    public void setPages(List<String> newPages) {
        if (newPages == null) throw new IllegalArgumentException("Pages cannot be null.");
        pages.clear();
        pages.addAll(newPages);
        bookMeta.setPages(pages);
        book.setItemMeta(bookMeta);
        new DreamBookPagesUpdatedEvent(this, new ArrayList<>(pages));
    }

    /// <summary>
    /// Appends pages and updates the underlying BookMeta.
    /// </summary>
    public void addPages(String... additionalPages) {
        if (additionalPages == null) throw new IllegalArgumentException("Pages cannot be null.");
        for (String p : additionalPages) {
            pages.add(PlainTextComponentSerializer.plainText()
                    .serialize(DreamMessageFormatter.format(p,  DreamMessageSettings.all())));
        }
        bookMeta.setPages(pages);
        book.setItemMeta(bookMeta);
        new DreamBookPagesUpdatedEvent(this, new ArrayList<>(pages));
    }

    /// <summary>
    /// Removes all pages and updates the underlying BookMeta.
    /// </summary>
    public void clearPages() {
        pages.clear();
        bookMeta.setPages(pages);
        book.setItemMeta(bookMeta);
        new DreamBookPagesUpdatedEvent(this, List.of());
    }

    // --------------------------
    // Reserved for future animations
    // --------------------------

    /// <summary>
    /// Reserved hook for a future “book frame” animation (no-op).
    /// </summary>
    public void displayNextFrame() {
        // Intentionally left blank: opening books does not provide a reliable paged animation API.
        // If approved, we can simulate page-flips by rebuilding items and re-opening per tick.
    }

    // --------------------------
    // Builder
    // --------------------------

    /**
     /// <summary>
     /// Builder for <see cref="DreamfireBook"/>.
     /// </summary>
     */
    public static class BookBuilder {
        private final ItemStack book;
        private final BookMeta bookMeta;
        private final List<String> pages = new ArrayList<>();

        /// <summary>
        /// Creates a builder with the given author and title.
        /// </summary>
        /// <param name="bookAuthor">Author (formatted via DreamfireMessage).</param>
        /// <param name="bookTitle">Title (formatted via DreamfireMessage).</param>
        public BookBuilder(String bookAuthor, String bookTitle) {
            if (bookAuthor == null || bookTitle == null) {
                throw new IllegalArgumentException("Author and title cannot be null.");
            }
            this.book = new ItemStack(Material.WRITTEN_BOOK);
            this.bookMeta = (BookMeta) book.getItemMeta();
            this.bookMeta.setAuthor(PlainTextComponentSerializer.plainText()
                    .serialize(DreamMessageFormatter.format(bookAuthor, DreamMessageSettings.all())));
            this.bookMeta.setTitle(PlainTextComponentSerializer.plainText()
                    .serialize(DreamMessageFormatter.format(bookTitle, DreamMessageSettings.all())));
        }

        /// <summary>
        /// Sets the book generation (original/copy/etc.).
        /// </summary>
        public BookBuilder generation(BookMeta.Generation generation) {
            this.bookMeta.setGeneration(generation);
            return this;
        }

        /// <summary>
        /// Adds pages (formatted) to this builder.
        /// </summary>
        public BookBuilder bookPages(String... bookPages) {
            if (bookPages == null) return this;
            for (String page : bookPages) {
                pages.add(PlainTextComponentSerializer.plainText()
                        .serialize(DreamMessageFormatter.format(page, DreamMessageSettings.all())));
            }
            return this;
        }

        /// <summary>
        /// Deprecated: legacy Spigot TextComponent pages.
        /// </summary>
        @Deprecated
        public BookBuilder bookPages(TextComponent... bookPages) {
            // You can keep this for back-compat, but prefer the String-based API.
            bookMeta.spigot().addPage(bookPages);
            return this;
        }

        /// <summary>
        /// Builds a <see cref="DreamfireBook"/> and registers it for persistence.
        /// </summary>
        public DreamBook createBook() {
            DreamBook dreamfireBook = new DreamBook();
            if (!pages.isEmpty()) bookMeta.setPages(pages);
            book.setItemMeta(bookMeta);

            dreamfireBook.book = book;
            dreamfireBook.bookMeta = bookMeta;
            dreamfireBook.pages.addAll(pages);
            DreamPersistentItemStack.Add(
                    DreamCore.DreamCore,
                    book,
                    PersistentDataType.STRING,
                    DreamfireBookKey,
                    dreamfireBook.getClassID().toString()
            );
            DreamCore.DreamBooks.put(dreamfireBook.getClassID(), dreamfireBook);
            return dreamfireBook;
        }
    }
}