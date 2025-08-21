package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is added to an <see cref="Entity"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to react when custom metadata is stored on an entity via a <see cref="NamespacedKey"/>.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentEntityAdded(PersistentEntityAddedEvent e) {
///     Entity ent = e.getEntity();
///     getLogger().info("Added key " + e.getNamespacedKey() + " to entity " + ent.getType());
/// }
/// ```
/// </example>
@Getter
public class PersistentEntityAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The entity that received the persistent data entry.
    /// </summary>
    private final Entity entity;

    /// <summary>
    /// The namespaced key under which the value was stored.
    /// </summary>
    private final NamespacedKey namespacedKey;

    /// <summary>
    /// The value that was stored for the key.
    /// </summary>
    private final Object value;

    /// <summary>
    /// Creates a new <see cref="PersistentEntityAddedEvent"/>.
    /// </summary>
    /// <param name="entity">The target entity.</param>
    /// <param name="namespacedKey">The key used to store the value.</param>
    /// <param name="value">The value stored.</param>
    public PersistentEntityAddedEvent(Entity entity, NamespacedKey namespacedKey, Object value){
        this.entity = entity;
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