package com.dreamfirestudios.dreamCore.DreamfireBook;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireBook.Events.DreamfireBookCloseEvent;
import com.dreamfirestudios.dreamCore.DreamfireBook.Events.DreamfireBookOpenEvent;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.DreamfirePersistentItemStack;
import lombok.Getter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DreamfireBook {
    public static final String DreamfireBookKey = "DreamfireBook";
    @Getter private UUID bookID;
    @Getter private ItemStack book;
    @Getter private BookMeta bookMeta;
    private List<String> pages;
    private List<UUID> viewers;

    /**
     * Checks if the player is currently viewing the book.
     *
     * @param player the player to check
     * @throws IllegalArgumentException if the player is null
     * @return true if the player is viewing the book, false otherwise
     */
    public boolean isPlayerInBook(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        return viewers.contains(player.getUniqueId());
    }

    /**
     * Opens the book for the player.
     *
     * @param player the player to open the book for
     * @throws IllegalArgumentException if the player is null
     */
    public void openBook(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        player.closeInventory();
        if (new DreamfireBookOpenEvent(this, player).isCancelled()) return;
        player.openBook(book);
        if(!viewers.contains(player.getUniqueId())) viewers.add(player.getUniqueId());
    }

    /**
     * Opens the book for the player for a specified duration in seconds.
     *
     * @param player the player to open the book for
     * @param durationInSeconds the duration for which the book will remain open
     * @throws IllegalArgumentException if the player is null or duration is negative
     */
    public void openBook(Player player, int durationInSeconds) {
        openBook(player);
        Bukkit.getScheduler().runTaskLater(DreamCore.GetDreamfireCore(), () -> {
            closeBook(player);
        }, durationInSeconds * 20L);
    }

    /**
     * Closes the book for the player.
     *
     * @param player the player to close the book for
     * @throws IllegalArgumentException if the player is null
     * @throws IllegalArgumentException if the player is null or duration is negative
     */
    public void closeBook(Player player){
        if(!viewers.contains(player.getUniqueId())) return;
        player.openInventory(Bukkit.createInventory(null, 9));
        player.closeInventory();
        new DreamfireBookCloseEvent(this, player);
        viewers.remove(player.getUniqueId());
    }

    /**
     * Handles player quit event, ensuring the book is closed for the player.
     *
     * @param player the player who quit
     * @throws IllegalArgumentException if the player is null
     */
    public void playerQuit(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        closeBook(player);
    }

    /**
     * Displays the next frame (not yet implemented).
     */
    public void displayNextFrame(){

    }

    public static class BookBuilder{
        private final UUID bookID = UUID.randomUUID();
        private final ItemStack book;
        private final BookMeta bookMeta;
        private final List<String> pages;

        /**
         * Initializes a new BookBuilder with the specified author and title.
         *
         * @param bookAuthor the author of the book
         * @param bookTitle the title of the book
         * @throws IllegalArgumentException if the author or title is null
         */
        public BookBuilder(String bookAuthor, String bookTitle) {
            if (bookAuthor == null || bookTitle == null) throw new IllegalArgumentException("Author and title cannot be null.");
            book = new ItemStack(Material.WRITTEN_BOOK);
            bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setAuthor(PlainTextComponentSerializer.plainText().serialize(DreamfireMessage.formatMessage(bookAuthor, null)));
            bookMeta.setTitle(PlainTextComponentSerializer.plainText().serialize(DreamfireMessage.formatMessage(bookTitle, null)));
            pages = new ArrayList<>();
        }

        /**
         * Sets the book's generation (e.g., original, copy, etc.).
         *
         * @param generation the generation of the book
         * @return the current BookBuilder instance for chaining
         */
        public BookBuilder generation(BookMeta.Generation generation) {
            this.bookMeta.setGeneration(generation);
            return this;
        }

        /**
         * Adds pages to the book.
         *
         * @param bookPages the pages to add
         * @return the current BookBuilder instance for chaining
         */
        public BookBuilder bookPages(String... bookPages) {
            for (String page : bookPages) pages.add(PlainTextComponentSerializer.plainText().serialize(DreamfireMessage.formatMessage(page, null)));
            return this;
        }

        /**
         * Deprecated method for adding pages using TextComponent objects.
         *
         * @param bookPages the pages to add as TextComponent objects
         * @return the current BookBuilder instance for chaining
         */
        @Deprecated
        public BookBuilder bookPages(TextComponent... bookPages) {
            bookMeta.spigot().addPage(bookPages);
            return this;
        }

        /**
         * Creates the DreamfireBook instance with the current builder configuration.
         *
         * @return the newly created DreamfireBook
         * @throws IllegalStateException if no pages have been added
         */
        @Deprecated
        public DreamfireBook createBook() {
            var dreamfireBook = new DreamfireBook();
            if (!pages.isEmpty()) bookMeta.setPages(pages);
            book.setItemMeta(bookMeta);
            dreamfireBook.bookID = bookID;
            dreamfireBook.book = book;
            dreamfireBook.bookMeta = bookMeta;
            dreamfireBook.pages = pages;
            DreamfirePersistentItemStack.Add(DreamCore.GetDreamfireCore(), book, PersistentDataType.STRING, DreamfireBookKey, bookID.toString());
            DreamCore.GetDreamfireCore().AddBookBuilder(bookID, dreamfireBook);
            return dreamfireBook;
        }
    }
}
