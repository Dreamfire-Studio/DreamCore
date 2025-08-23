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
package com.dreamfirestudios.dreamcore.DreamPersistentData;

import com.dreamfirestudios.dreamcore.DreamChat.DreamChat;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.logging.Level;

/// <summary>
/// Utilities for reading and writing <see cref="PersistentDataContainer"/> on <see cref="ItemStack"/> metadata.
/// </summary>
/// <remarks>
/// Works via <c>ItemMeta#getPersistentDataContainer()</c>. Includes clone and expiring helper methods.
/// </remarks>
public class DreamPersistentItemStack {

    /// <summary>
    /// Validates a local key for use with <see cref="NamespacedKey"/>.
    /// </summary>
    /// <param name="key">Local key part.</param>
    /// <returns><c>true</c> if valid; otherwise <c>false</c>.</returns>
    public static boolean isValidKey(String key) {
        return key != null && key.matches("[a-z0-9/._-]{1,256}");
    }

    /// <summary>
    /// Gets the persistent data container from an item stack's meta.
    /// </summary>
    /// <param name="itemStack">Target item stack.</param>
    /// <returns>Persistent data container.</returns>
    /// <exception cref="IllegalArgumentException">If <paramref name="itemStack"/> or its meta is null.</exception>
    public static PersistentDataContainer ReturnPersistentDataContainer(ItemStack itemStack) {
        if(itemStack == null){
            throw new IllegalArgumentException("Itemstack cannot be null.");
        }
        if(itemStack.getItemMeta() == null){
            throw new IllegalArgumentException("itemStack.getItemMeta() cannot be null.");
        }
        return itemStack.getItemMeta().getPersistentDataContainer();
    }

    /// <summary>
    /// Retrieves all persistent data grouped by logical types for an item stack.
    /// </summary>
    /// <param name="itemStack">Target item stack.</param>
    /// <returns>Type → (key → value) map.</returns>
    public static LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>> GetALl(ItemStack itemStack){
        var data = new LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>>();
        for (var persistentDataType : PersistentDataTypes.values()) {
            data.put(persistentDataType, GetALl(itemStack, persistentDataType));
        }
        return data;
    }

    /// <summary>
    /// Retrieves all entries of a specific logical type for an item stack.
    /// </summary>
    /// <param name="itemStack">Target item stack.</param>
    /// <param name="persistentDataType">Logical data type.</param>
    /// <returns>Key/value map for that type.</returns>
    public static LinkedHashMap<String, Object> GetALl(ItemStack itemStack, PersistentDataTypes persistentDataType){
        var data = new LinkedHashMap<String, Object>();
        try {
            var persistentDataContainer = ReturnPersistentDataContainer(itemStack);
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
    /// Retrieves a typed value from an item stack.
    /// </summary>
    /// <typeparam name="T">Expected type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="itemStack">Target item stack.</param>
    /// <param name="key">Local key.</param>
    /// <param name="type">Bukkit type descriptor.</param>
    /// <returns>Value or <c>null</c> if none.</returns>
    /// <example>
    /// <code>
    /// Integer level = DreamPersistentItemStack.Get(plugin, stack, "level", PersistentDataType.INTEGER);
    /// </code>
    /// </example>
    public static <T> T Get(JavaPlugin javaPlugin, ItemStack itemStack, String key, PersistentDataType<?, T> type) {
        if (itemStack == null) {
            DreamChat.SendMessageToConsole("Itemstack is null. Cannot retrieve persistent data.", DreamMessageSettings.all());
            return null;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return null;
        }
        try {
            var container = ReturnPersistentDataContainer(itemStack);
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return container.has(namespacedKey, type) ? container.get(namespacedKey, type) : null;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while retrieving persistent data for key: " + key, DreamMessageSettings.all());
            return null;
        }
    }

    /// <summary>
    /// Checks if a key exists for an item stack container.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="itemStack">Target item stack.</param>
    /// <param name="persistentDataType">Expected type.</param>
    /// <param name="key">Local key.</param>
    /// <returns><c>true</c> if present; otherwise <c>false</c>.</returns>
    public static boolean Has(JavaPlugin javaPlugin, ItemStack itemStack, PersistentDataType persistentDataType, String key) {
        if (itemStack == null) {
            DreamChat.SendMessageToConsole("ItemStack is null. Cannot check for persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var persistentDataContainer = ReturnPersistentDataContainer(itemStack);
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return persistentDataContainer.has(namespacedKey, persistentDataType);
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while checking persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Adds or updates a typed value for an item stack.
    /// </summary>
    /// <typeparam name="T">Stored value type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="itemStack">Target item stack.</param>
    /// <param name="type">Bukkit type descriptor.</param>
    /// <param name="key">Local key.</param>
    /// <param name="value">Value to store.</param>
    /// <returns><c>true</c> on success; else <c>false</c>.</returns>
    public static <T> boolean Add(JavaPlugin javaPlugin, ItemStack itemStack, PersistentDataType<?, T> type, String key, T value) {
        if (itemStack == null) {
            DreamChat.SendMessageToConsole("ItemStack is null. Cannot add persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (itemStack.getItemMeta() == null) {
            DreamChat.SendMessageToConsole("itemStack.getItemMeta() is null. Cannot add persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var itemMeta = itemStack.getItemMeta();
            var container = itemMeta.getPersistentDataContainer();
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            container.set(namespacedKey, type, value);
            itemStack.setItemMeta(itemMeta);
            new PersistentItemStackAddedEvent(itemStack, namespacedKey, value);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Removes a key from an item stack's container.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="itemStack">Target item stack.</param>
    /// <param name="key">Local key.</param>
    /// <returns><c>true</c> if removed; otherwise <c>false</c>.</returns>
    public static boolean Remove(JavaPlugin javaPlugin, ItemStack itemStack, String key) {
        if (itemStack == null) {
            DreamChat.SendMessageToConsole("ItemStack is null. Cannot add persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (itemStack.getItemMeta() == null) {
            DreamChat.SendMessageToConsole("itemStack.getItemMeta() is null. Cannot add persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.DreamCore : javaPlugin;
            var itemMeta = itemStack.getItemMeta();
            var container = itemMeta.getPersistentDataContainer();
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            container.remove(namespacedKey);
            itemStack.setItemMeta(itemMeta);
            new PersistentItemStackRemovedEvent(itemStack, namespacedKey);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }

    /// <summary>
    /// Clones all persistent data from one item stack to another.
    /// </summary>
    /// <param name="from">Source item stack.</param>
    /// <param name="to">Target item stack.</param>
    /// <example>
    /// <code>
    /// DreamPersistentItemStack.CloneData(template, result);
    /// </code>
    /// </example>
    public static void CloneData(ItemStack from, ItemStack to) {
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
    /// Adds a value that expires after the given duration.
    /// </summary>
    /// <typeparam name="T">Stored value type.</typeparam>
    /// <param name="javaPlugin">Owning plugin (defaults to <c>DreamCore.DreamCore</c>).</param>
    /// <param name="itemStack">Target item.</param>
    /// <param name="type">Data type descriptor.</param>
    /// <param name="key">Local key.</param>
    /// <param name="value">Value to store.</param>
    /// <param name="expiryMillis">Expiry in milliseconds (converted to ticks).</param>
    /// <returns><c>true</c> on success; else <c>false</c>.</returns>
    /// <example>
    /// <code>
    /// DreamPersistentItemStack.AddExpiring(plugin, stack, PersistentDataType.INTEGER, "temp_uses", 1, 2000);
    /// </code>
    /// </example>
    public static <T> boolean AddExpiring(JavaPlugin javaPlugin, ItemStack itemStack, PersistentDataType<?, T> type, String key, T value, long expiryMillis) {
        if (itemStack == null) {
            DreamChat.SendMessageToConsole("Entity is null. Cannot add expiring persistent data.", DreamMessageSettings.all());
            return false;
        }
        if (!isValidKey(key)) {
            DreamChat.SendMessageToConsole("Invalid key: " + key, DreamMessageSettings.all());
            return false;
        }
        try {
            Add(javaPlugin, itemStack, type, key, value);
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> Remove(javaPlugin, itemStack, key), expiryMillis / 50);
            return true;
        } catch (Exception e) {
            DreamChat.SendMessageToConsole("Error while adding expiring persistent data for key: " + key, DreamMessageSettings.all());
            return false;
        }
    }
}