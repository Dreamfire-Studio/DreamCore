package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is added to an <see cref="ItemStack"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to react when custom metadata is stored on an item via a <see cref="NamespacedKey"/>.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentItemAdded(PersistentItemStackAddedEvent e) {
///     ItemStack stack = e.getItemStack();
///     getLogger().info("Added key " + e.getNamespacedKey() + " to item " + stack.getType());
/// }
/// ```
/// </example>
@Getter
public class PersistentItemStackAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The item stack that received the persistent data entry.
    /// </summary>
    private final ItemStack itemStack;

    /// <summary>
    /// The namespaced key under which the value was stored.
    /// </summary>
    private final NamespacedKey namespacedKey;

    /// <summary>
    /// The value that was stored for the key.
    /// </summary>
    private final Object value;

    /// <summary>
    /// Creates a new <see cref="PersistentItemStackAddedEvent"/>.
    /// </summary>
    /// <param name="itemStack">The target item stack.</param>
    /// <param name="namespacedKey">The key used to store the value.</param>
    /// <param name="value">The value stored.</param>
    public PersistentItemStackAddedEvent(ItemStack itemStack, NamespacedKey namespacedKey, Object value){
        this.itemStack = itemStack;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the handler list for this event.
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