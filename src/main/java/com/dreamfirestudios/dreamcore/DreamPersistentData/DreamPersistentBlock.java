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
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.logging.Level;

/// <summary>
/// Utilities for working with <see cref="PersistentDataContainer"/> on <see cref="Block"/> tile entities.
/// </summary>
/// <remarks>
/// These helpers validate keys, safely read/write typed values, clone data between blocks,
/// and support expiring entries scheduled on the Bukkit main thread.
/// </remarks>
public class DreamPersistentBlock {

    /// <summary>
    /// Validates whether a given key conforms to <see cref="NamespacedKey"/> requirements.
    /// </summary>
    /// <param name="key">The raw (local) key string to validate.</param>
    public static boolean isValidKey(String key) {
        return key != null && key.matches("[a-z0-9/._-]{1,256}");
    }

    /// <summary>
    /// Returns the <see cref="PersistentDataContainer"/> for a tile <see cref="Block"/>.
    /// </summary>
    /// <param name="block">The block to inspect.</param>
    /// <returns>The persistent data container of the block.</returns>
    /// <exception cref="IllegalArgumentException">If <paramref name="block"/> is null or not a <see cref="TileState"/>.</exception>
    public static PersistentDataContainer ReturnPersistentDataContainer(Block block) {
        if(block == null){
            throw new IllegalArgumentException("Block cannot be null.");
        }
        if(!(block instanceof TileState tileState)){
            throw new IllegalArgumentException("Block must be instance of tile state!");
        }
        return tileState.getPersistentDataContainer();
    }

    /// <summary>
    /// Retrieves all persistent data stored in a block's container, grouped by data types.
    /// </summary>
    /// <param name="block">The tile block to read.</param>
    /// <returns>A map of data types to their key/value maps.</returns>
    /// <example>
    /// <code>
    /// var dump = DreamPersistentBlock.GetALl(block);
    /// dump.forEach((type, kv) -&gt; getLogger().info(type + ": " + kv));
    /// </code>
    /// </example>
    public static LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>> GetALl(Block block){
        var data = new LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>>();
        for(var persistentDataTypeEnum : PersistentDataTypes.values()) data.put(persistentDataTypeEnum, GetALl(block, persistentDataTypeEnum));
        return data;
    }

    /// <summary>
    /// Retrieves all persistent data of a specific type from a block's container.
    /// </summary>
    /// <param name="block">The tile block to read.</param>
    /// <param name="persistentDataType">The logical data type to extract.</param>
    /// <returns>Key/value map for the given type. Missing entries are returned as <c>null</c> values.</returns>
    public static LinkedHashMap<String, Object> GetALl(Block block, PersistentDataTypes persistentDataType){
        var data = new LinkedHashMap<String, Object>();
        try {
            var persistentDataContainer = ReturnPersistentDataContainer(block);
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
    /// Retrieves a typed value for a given key from a block's container.
    /// </summary>
    /// <typeparam name="T">Expected value type (as defined by <paramref name="type"/>).</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c> if null).</param>
    /// <param name="block">The tile block.</param>
    /// <param name="key">Local key portion for <see cref="NamespacedKey"/>.</param>
    /// <param name="type">Bukkit <see cref="PersistentDataType"/> describing the value.</param>
    /// <returns>The stored value, or <c>null</c> if absent/invalid.</returns>
    /// <example>
    /// <code>
    /// Integer level = DreamPersistentBlock.Get(plugin, chestBlock, "upgrade_level", PersistentDataType.INTEGER);
    /// </code>
    /// </example>
    public static <T> T Get(JavaPlugin javaPlugin, Block block, String key, PersistentDataType<?, T> type) {
        if (block == null) {
            DreamChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.", DreamMessageSettings.all());
            return null;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return null;
        }
        try {
            var container = ReturnPersistentDataContainer(block);
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return container.has(namespacedKey, type) ? container.get(namespacedKey, type) : null;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while retrieving persistent data for key: " + key, DreamMessageSettings.all());
            return null;
        }
    }

    /// <summary>
    /// Checks if a key exists in a block's container.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="block">The tile block.</param>
    /// <param name="persistentDataType">The expected data type.</param>
    /// <param name="key">Local key.</param>
    /// <returns><c>true</c> if present; otherwise <c>false</c>.</returns>
    public static boolean Has(JavaPlugin javaPlugin, Block block, PersistentDataType persistentDataType, String key) {
        if (block == null) {
            DreamChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var persistentDataContainer = ReturnPersistentDataContainer(block);
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return persistentDataContainer.has(namespacedKey, persistentDataType);
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while checking persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Adds or updates a typed value in a block's container.
    /// </summary>
    /// <typeparam name="T">Stored value type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="block">The tile block to modify.</param>
    /// <param name="type">Data type descriptor.</param>
    /// <param name="key">Local key.</param>
    /// <param name="value">Value to store.</param>
    /// <returns><c>true</c> on success; otherwise <c>false</c>.</returns>
    /// <example>
    /// <code>
    /// DreamPersistentBlock.Add(plugin, block, PersistentDataType.STRING, "owner", player.getUniqueId().toString());
    /// </code>
    /// </example>
    public static <T> boolean Add(JavaPlugin javaPlugin, Block block, PersistentDataType<?, T> type, String key, T value) {
        if (block == null) {
            DreamChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        if(!(block instanceof TileState tileState)){
            throw new IllegalArgumentException("Block must be instance of tile state!");
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var persistentDataContainer = tileState.getPersistentDataContainer();
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            persistentDataContainer.set(namespacedKey, type, value);
            tileState.update();
            new PersistentBlockAddedEvent(block, namespacedKey, value);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Removes a value by key from a block's container.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="block">The tile block to modify.</param>
    /// <param name="key">Local key to remove.</param>
    /// <returns><c>true</c> if removed; otherwise <c>false</c>.</returns>
    public static boolean Remove(JavaPlugin javaPlugin, Block block, String key) {
        if (block == null) {
            DreamChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        if(!(block instanceof TileState tileState)){
            throw new IllegalArgumentException("Block must be instance of tile state!");
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var persistentDataContainer = tileState.getPersistentDataContainer();
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            persistentDataContainer.remove(namespacedKey);
            tileState.update();
            new PersistentBlockRemovedEvent(block, namespacedKey);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Clones all persistent data from one block to another (key/type preserving).
    /// </summary>
    /// <param name="from">Source tile block.</param>
    /// <param name="to">Target tile block.</param>
    /// <example>
    /// <code>
    /// DreamPersistentBlock.CloneData(sourceChest, targetChest);
    /// </code>
    /// </example>
    public static void CloneData(Block from, Block to) {
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
    /// Adds a value that automatically expires after a given duration.
    /// </summary>
    /// <typeparam name="T">Stored value type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="block">Tile block to modify.</param>
    /// <param name="type">Data type descriptor.</param>
    /// <param name="key">Local key.</param>
    /// <param name="value">Value to store.</param>
    /// <param name="expiryMillis">Expiry in milliseconds (scheduled on main thread).</param>
    /// <returns><c>true</c> on success; else <c>false</c>.</returns>
    /// <example>
    /// <code>
    /// DreamPersistentBlock.AddExpiring(plugin, block, PersistentDataType.BOOLEAN, "glowing", true, 5000);
    /// </code>
    /// </example>
    public static <T> boolean AddExpiring(JavaPlugin javaPlugin, Block block, PersistentDataType<?, T> type, String key, T value, long expiryMillis) {
        if (block == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot add expiring persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            Add(javaPlugin, block, type, key, value);
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> Remove(javaPlugin, block, key), expiryMillis / 50);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding expiring persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }
}