package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is removed from an <see cref="ItemStack"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to react when custom metadata identified by a <see cref="NamespacedKey"/> is cleared from an item.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentItemRemoved(PersistentItemStackRemovedEvent e) {
///     ItemStack stack = e.getItemStack();
///     getLogger().info("Removed key " + e.getNamespacedKey() + " from item " + stack.getType());
/// }
/// ```
/// </example>
@Getter
public class PersistentItemStackRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The item stack from which the persistent data entry was removed.
    /// </summary>
    private final ItemStack itemStack;

    /// <summary>
    /// The namespaced key that was removed from the item stack.
    /// </summary>
    private final NamespacedKey namespacedKey;

    /// <summary>
    /// Initializes a new <see cref="PersistentItemStackRemovedEvent"/>.
    /// </summary>
    /// <param name="itemStack">The target item stack.</param>
    /// <param name="namespacedKey">The key that was removed.</param>
    public PersistentItemStackRemovedEvent(ItemStack itemStack, NamespacedKey namespacedKey){
        this.itemStack = itemStack;
        this.namespacedKey = namespacedKey;
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