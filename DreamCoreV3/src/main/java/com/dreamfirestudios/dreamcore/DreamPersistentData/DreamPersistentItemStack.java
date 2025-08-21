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

public class DreamPersistentItemStack {
    /**
     * Validates whether a given key conforms to Minecraft's NamespacedKey requirements.
     *
     * @param key The key to validate.
     * @return True if the key is valid, false otherwise.
     */
    public static boolean isValidKey(String key) {
        return key != null && key.matches("[a-z0-9/._-]{1,256}");
    }

    /**
     * Returns the PersistentDataContainer of the given entity.
     *
     * @param itemStack The itemstack whose PersistentDataContainer is to be retrieved.
     * @return The PersistentDataContainer of the entity.
     * @throws IllegalArgumentException if the entity is null.
     */
    public static PersistentDataContainer ReturnPersistentDataContainer(ItemStack itemStack) {
        if(itemStack == null){
            throw new IllegalArgumentException("Itemstack cannot be null.");
        }
        if(itemStack.getItemMeta() == null){
            throw new IllegalArgumentException("itemStack.getItemMeta() cannot be null.");
        }
        return itemStack.getItemMeta().getPersistentDataContainer();
    }

    /**
     * Retrieves all persistent data stored in an entity's container, grouped by data types.
     *
     * @param itemStack The itemstack whose PersistentDataContainer is to be retrieved.
     * @return A map of data types to their corresponding key-value pairs.
     */
    public static LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>> GetALl(ItemStack itemStack){
        var data = new LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>>();
        for (var persistentDataType : PersistentDataTypes.values()) {
            data.put(persistentDataType, GetALl(itemStack, persistentDataType));
        }
        return data;
    }

    /**
     * Retrieves all persistent data of a specific type from an entity's container.
     *
     * @param itemStack The itemstack whose PersistentDataContainer is to be retrieved.
     * @param persistentDataType The type of persistent data to retrieve.
     * @return A map of keys to their corresponding values.
     */
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

    /**
     * Retrieves the value of a specific persistent data key from an entity with type safety.
     *
     * @param itemStack The itemStack to query.
     * @param key The key of the data to retrieve.
     * @param type The PersistentDataType specifying the expected type of the data.
     * @param <T> The type of the data to retrieve.
     * @return The value of the persistent data, or null if it does not exist.
     */
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

    /**
     * Checks if a specific persistent data key exists in an entity's container.
     *
     * @param javaPlugin The plugin instance (uses DreamfireCore by default).
     * @param itemStack The itemStack to query.
     * @param persistentDataType The type of the data to check.
     * @param key The key of the data to check.
     * @return True if the key exists, false otherwise.
     */
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

    /**
     * Adds or updates a persistent data entry in an entity's container with type safety.
     *
     * @param javaPlugin The plugin instance (uses DreamfireCore by default).
     * @param itemStack The itemStack to update.
     * @param type The PersistentDataType specifying the type of the data.
     * @param key The key of the data.
     * @param value The value of the data.
     * @param <T> The type of the data to store.
     * @return True if the operation is successful, false otherwise.
     */
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

    /**
     * Removes a specific persistent data entry from an entity's container.
     *
     * @param javaPlugin The plugin instance (uses DreamfireCore by default).
     * @param itemStack The itemStack to update.
     * @param key The key of the data to remove.
     * @return True if the operation is successful, false otherwise.
     */
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

    /**
     * Clones all persistent data from one entity to another.
     *
     * @param from The source itemstack from which data will be cloned.
     * @param to The target itemstack to which data will be cloned.
     */
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

    /**
     * Adds a persistent data entry to an entity that automatically expires after a specified duration.
     *
     * @param javaPlugin   The plugin instance (uses DreamfireCore by default).
     * @param itemStack       The itemStack to update.
     * @param type         The PersistentDataType specifying the type of the data.
     * @param key          The key of the data.
     * @param value        The value of the data.
     * @param expiryMillis The duration in milliseconds after which the data will be removed.
     */
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
