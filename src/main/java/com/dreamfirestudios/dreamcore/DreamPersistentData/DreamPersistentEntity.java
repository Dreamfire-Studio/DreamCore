/*  Copyright (c) Dreamfire Studios
 *  This file is part of DreamfireV2 (industry-level code quality initiative).
 *  Style: DocFX-friendly XML docs, consistent with ChaosGalaxyTCG headers.
 */

package com.dreamfirestudios.dreamcore.DreamPersistentData;

import com.dreamfirestudios.dreamcore.DreamChat.DreamChat;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;

/// <summary>
/// Utilities for reading and writing <see cref="PersistentDataContainer"/> on <see cref="Entity"/>.
/// </summary>
/// <remarks>
/// Mirrors the block utilities but targets entity containers.
/// Provides type-safe <c>Get</c>, <c>Has</c>, <c>Add</c>, <c>Remove</c>, cloning, and expiring values.
/// </remarks>
public class DreamPersistentEntity {

    /// <summary>
    /// Validates a local key for use with <see cref="NamespacedKey"/>.
    /// </summary>
    /// <param name="key">Local key part.</param>
    /// <returns><c>true</c> if valid; otherwise <c>false</c>.</returns>
    public static boolean isValidKey(String key) {
        return key != null && key.matches("[a-z0-9/._-]{1,256}");
    }

    /// <summary>
    /// Gets an entity's <see cref="PersistentDataContainer"/>.
    /// </summary>
    /// <param name="entity">Target entity.</param>
    /// <returns>Persistent data container.</returns>
    /// <exception cref="IllegalArgumentException">When <paramref name="entity"/> is null.</exception>
    public static PersistentDataContainer ReturnPersistentDataContainer(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        return entity.getPersistentDataContainer();
    }

    /// <summary>
    /// Retrieves all persistent data grouped by logical types.
    /// </summary>
    /// <param name="entity">Target entity.</param>
    /// <returns>Type → (key → value) map.</returns>
    public static LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>> GetALl(Entity entity) {
        var data = new LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>>();
        for (var persistentDataType : PersistentDataTypes.values()) {
            data.put(persistentDataType, GetALl(entity, persistentDataType));
        }
        return data;
    }

    /// <summary>
    /// Retrieves all data for a specific logical type.
    /// </summary>
    /// <param name="entity">Target entity.</param>
    /// <param name="persistentDataType">Logical data type.</param>
    /// <returns>Key/value map for that type.</returns>
    public static LinkedHashMap<String, Object> GetALl(Entity entity, PersistentDataTypes persistentDataType) {
        var data = new LinkedHashMap<String, Object>();
        try {
            var persistentDataContainer = ReturnPersistentDataContainer(entity);
            var persistentData = persistentDataType.persistentDataType;
            for (var namespacedKey : persistentDataContainer.getKeys()) {
                data.put(namespacedKey.getKey(), persistentDataContainer.get(namespacedKey, persistentData));
            }
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while retrieving persistent data", DreamMessageSettings.all());
        }
        return data;
    }

    /// <summary>
    /// Retrieves a typed value from an entity container.
    /// </summary>
    /// <typeparam name="T">Expected type.</typeparam>
    /// <param name="entity">Target entity.</param>
    /// <param name="key">Local key.</param>
    /// <param name="type">Bukkit type descriptor.</param>
    /// <returns>Value or <c>null</c> if none.</returns>
    /// <example>
    /// <code>
    /// String owner = DreamPersistentEntity.Get(entity, "owner", PersistentDataType.STRING);
    /// </code>
    /// </example>
    public static <T> T Get(Entity entity, String key, PersistentDataType<?, T> type) {
        if (entity == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot retrieve persistent data.", DreamMessageSettings.all());
            return null;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return null;
        }
        try {
            var container = ReturnPersistentDataContainer(entity);
            var namespacedKey = new NamespacedKey(com.dreamfirestudios.dreamcore.DreamCore.DreamCore, key);
            return container.has(namespacedKey, type) ? container.get(namespacedKey, type) : null;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while retrieving persistent data for key: " + key, DreamMessageSettings.all());
            return null;
        }
    }

    /// <summary>
    /// Checks if a key exists in an entity container.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="entity">Target entity.</param>
    /// <param name="persistentDataType">Expected data type.</param>
    /// <param name="key">Local key.</param>
    /// <returns><c>true</c> if present; otherwise <c>false</c>.</returns>
    public static boolean Has(JavaPlugin javaPlugin, Entity entity, PersistentDataType persistentDataType, String key) {
        if (entity == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot check for persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var persistentDataContainer = ReturnPersistentDataContainer(entity);
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return persistentDataContainer.has(namespacedKey, persistentDataType);
        } catch (Exception e) {
            DreamChat.SendMessageToConsole( "Error while checking persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Adds or updates a typed value in an entity container.
    /// </summary>
    /// <typeparam name="T">Stored value type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="entity">Target entity.</param>
    /// <param name="type">Bukkit type descriptor.</param>
    /// <param name="key">Local key.</param>
    /// <param name="value">Value to store.</param>
    /// <returns><c>true</c> on success; else <c>false</c>.</returns>
    /// <example>
    /// <code>
    /// DreamPersistentEntity.Add(plugin, entity, PersistentDataType.INTEGER, "charges", 3);
    /// </code>
    /// </example>
    public static <T> boolean Add(JavaPlugin javaPlugin, Entity entity, PersistentDataType<?, T> type, String key, T value) {
        if (entity == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot add persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var container = ReturnPersistentDataContainer(entity);
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            container.set(namespacedKey, type, value);
            new PersistentEntityAddedEvent(entity, namespacedKey, value);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Removes a value by key from an entity container.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="entity">Target entity.</param>
    /// <param name="key">Local key to remove.</param>
    /// <returns><c>true</c> if removed; otherwise <c>false</c>.</returns>
    public static boolean Remove(JavaPlugin javaPlugin, Entity entity, String key) {
        if (entity == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot remove persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var persistentDataContainer = ReturnPersistentDataContainer(entity);
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            persistentDataContainer.remove(namespacedKey);
            new PersistentEntityRemovedEvent(entity, namespacedKey);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while removing persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Clones every key/value across logical data types from one entity to another.
    /// </summary>
    /// <param name="from">Source entity.</param>
    /// <param name="to">Destination entity.</param>
    /// <example>
    /// <code>
    /// DreamPersistentEntity.CloneData(zombie, husk);
    /// </code>
    /// </example>
    public static void CloneData(Entity from, Entity to) {
        if (from == null || to == null) {
            DreamChat.SendMessageToConsole("Source or target entity is null. Cannot clone data.", DreamMessageSettings.all());
            return;
        }
        try {
            var fromData = GetALl(from);
            fromData.forEach((type, values) -> {
                values.forEach((key, value) -> Add(null, to, type.persistentDataType, key, value));
            });
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while cloning persistent data.", DreamMessageSettings.all());
        }
    }

    /// <summary>
    /// Adds a value to an entity that automatically expires after the given duration.
    /// </summary>
    /// <typeparam name="T">Stored value type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="entity">Target entity.</param>
    /// <param name="type">Data type descriptor.</param>
    /// <param name="key">Local key.</param>
    /// <param name="value">Value to store.</param>
    /// <param name="expiryMillis">Expiry in milliseconds (Bukkit ticks used internally).</param>
    /// <returns><c>true</c> on success; else <c>false</c>.</returns>
    /// <example>
    /// <code>
    /// DreamPersistentEntity.AddExpiring(plugin, entity, PersistentDataType.BOOLEAN, "tag_temp", true, 10000);
    /// </code>
    /// </example>
    public static <T> boolean AddExpiring(JavaPlugin javaPlugin, Entity entity, PersistentDataType<?, T> type, String key, T value, long expiryMillis) {
        if (entity == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot add expiring persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            Add(javaPlugin, entity, type, key, value);
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> Remove(javaPlugin, entity, key), expiryMillis / 50);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding expiring persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }
}