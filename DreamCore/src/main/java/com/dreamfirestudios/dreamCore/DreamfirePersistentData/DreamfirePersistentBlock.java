package com.dreamfirestudios.dreamCore.DreamfirePersistentData;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireChat;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Event.PersistentBlockAddedEvent;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Event.PersistentBlockRemovedEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.logging.Level;

//TODO make expirables persist server restart
public class DreamfirePersistentBlock {
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
     * @param block The block whose PersistentDataContainer is to be retrieved.
     * @return The PersistentDataContainer of the entity.
     * @throws IllegalArgumentException if the entity is null.
     */
    public static PersistentDataContainer ReturnPersistentDataContainer(Block block) {
        if(block == null){
            throw new IllegalArgumentException("Block cannot be null.");
        }

        if(!(block instanceof TileState tileState)){
            throw new IllegalArgumentException("Block must be instance of tile state!");
        }

        return tileState.getPersistentDataContainer();
    }

    /**
     * Retrieves all persistent data stored in an entity's container, grouped by data types.
     *
     * @param block The block whose PersistentDataContainer is to be retrieved.
     * @return A map of data types to their corresponding key-value pairs.
     */
    public static LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>> GetALl(Block block){
        var data = new LinkedHashMap<PersistentDataTypes, LinkedHashMap<String, Object>>();
        for(var persistentDataTypeEnum : PersistentDataTypes.values()) data.put(persistentDataTypeEnum, GetALl(block, persistentDataTypeEnum));
        return data;
    }

    /**
     * Retrieves all persistent data of a specific type from an entity's container.
     *
     * @param block The block whose PersistentDataContainer is to be retrieved.
     * @param persistentDataType The type of persistent data to retrieve.
     * @return A map of keys to their corresponding values.
     */
    public static LinkedHashMap<String, Object> GetALl(Block block, PersistentDataTypes persistentDataType){
        var data = new LinkedHashMap<String, Object>();
        try {
            var persistentDataContainer = ReturnPersistentDataContainer(block);
            var persistentData = persistentDataType.persistentDataType;
            for (var namespacedKey : persistentDataContainer.getKeys()) {
                data.put(namespacedKey.getKey(), persistentDataContainer.get(namespacedKey, persistentData));
            }
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while retrieving persistent data");
        }
        return data;
    }

    /**
     * Retrieves the value of a specific persistent data key from an entity with type safety.
     *
     * @param block The block to query.
     * @param key The key of the data to retrieve.
     * @param type The PersistentDataType specifying the expected type of the data.
     * @param <T> The type of the data to retrieve.
     * @return The value of the persistent data, or null if it does not exist.
     */
    public static <T> T Get(JavaPlugin javaPlugin, Block block, String key, PersistentDataType<?, T> type) {
        if (block == null) {
            DreamfireChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.");
            return null;
        }
        if (!isValidKey(key)) {
            DreamfireChat.SendMessageToConsole("Invalid key: " + key);
            return null;
        }
        try {
            var container = ReturnPersistentDataContainer(block);
            javaPlugin = javaPlugin == null ? DreamCore.GetDreamfireCore() : javaPlugin;
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return container.has(namespacedKey, type) ? container.get(namespacedKey, type) : null;
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while retrieving persistent data for key: " + key);
            return null;
        }
    }

    /**
     * Checks if a specific persistent data key exists in an entity's container.
     *
     * @param javaPlugin The plugin instance (uses DreamfireCore by default).
     * @param block The block to query.
     * @param persistentDataType The type of the data to check.
     * @param key The key of the data to check.
     * @return True if the key exists, false otherwise.
     */
    public static boolean Has(JavaPlugin javaPlugin, Block block, PersistentDataType persistentDataType, String key) {
        if (block == null) {
            DreamfireChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.");
            return false;
        }
        if (!isValidKey(key)) {
            DreamfireChat.SendMessageToConsole("Invalid key: " + key);
            return false;
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.GetDreamfireCore() : javaPlugin;
            var persistentDataContainer = ReturnPersistentDataContainer(block);
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            return persistentDataContainer.has(namespacedKey, persistentDataType);
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while checking persistent data for key: " + key);
            return false;
        }
    }

    /**
     * Adds or updates a persistent data entry in an entity's container with type safety.
     *
     * @param javaPlugin The plugin instance (uses DreamfireCore by default).
     * @param block The block to update.
     * @param type The PersistentDataType specifying the type of the data.
     * @param key The key of the data.
     * @param value The value of the data.
     * @param <T> The type of the data to store.
     * @return True if the operation is successful, false otherwise.
     */
    public static <T> boolean Add(JavaPlugin javaPlugin, Block block, PersistentDataType<?, T> type, String key, T value) {
        if (block == null) {
            DreamfireChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.");
            return false;
        }
        if (!isValidKey(key)) {
            DreamfireChat.SendMessageToConsole("Invalid key: " + key);
            return false;
        }
        if(!(block instanceof TileState tileState)){
            throw new IllegalArgumentException("Block must be instance of tile state!");
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.GetDreamfireCore() : javaPlugin;
            var persistentDataContainer = tileState.getPersistentDataContainer();
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            persistentDataContainer.set(namespacedKey, type, value);
            tileState.update();
            new PersistentBlockAddedEvent(block, namespacedKey, value);
            return true;
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while adding persistent data for key: " + key);
            return false;
        }
    }

    /**
     * Removes a specific persistent data entry from an entity's container.
     *
     * @param javaPlugin The plugin instance (uses DreamfireCore by default).
     * @param block The block to update.
     * @param key The key of the data to remove.
     * @return True if the operation is successful, false otherwise.
     */
    public static boolean Remove(JavaPlugin javaPlugin, Block block, String key) {
        if (block == null) {
            DreamfireChat.SendMessageToConsole("block is null. Cannot retrieve persistent data.");
            return false;
        }
        if (!isValidKey(key)) {
            DreamfireChat.SendMessageToConsole("Invalid key: " + key);
            return false;
        }
        if(!(block instanceof TileState tileState)){
            throw new IllegalArgumentException("Block must be instance of tile state!");
        }
        try {
            javaPlugin = javaPlugin == null ? DreamCore.GetDreamfireCore() : javaPlugin;
            var persistentDataContainer = tileState.getPersistentDataContainer();
            var namespacedKey = new NamespacedKey(javaPlugin, key);
            persistentDataContainer.remove(namespacedKey);
            tileState.update();
            new PersistentBlockRemovedEvent(block, namespacedKey);
            return true;
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while adding persistent data for key: " + key);
            return false;
        }
    }

    /**
     * Clones all persistent data from one entity to another.
     *
     * @param from The source Block from which data will be cloned.
     * @param to The target Block to which data will be cloned.
     */
    public static void CloneData(Block from, Block to) {
        if (from == null || to == null) {
            DreamfireChat.SendMessageToConsole("Source or target entity is null. Cannot clone data.");
            return;
        }
        try {
            var fromData = GetALl(from);
            fromData.forEach((type, values) -> {
                values.forEach((key, value) -> Add(null, to, type.persistentDataType, key, value));
            });
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while cloning persistent data.");
        }
    }

    /**
     * Adds a persistent data entry to an entity that automatically expires after a specified duration.
     *
     * @param javaPlugin   The plugin instance (uses DreamfireCore by default).
     * @param block       The Block to update.
     * @param type         The PersistentDataType specifying the type of the data.
     * @param key          The key of the data.
     * @param value        The value of the data.
     * @param expiryMillis The duration in milliseconds after which the data will be removed.
     */
    public static <T> boolean AddExpiring(JavaPlugin javaPlugin, Block block, PersistentDataType<?, T> type, String key, T value, long expiryMillis) {
        if (block == null) {
            DreamfireChat.SendMessageToConsole("Entity is null. Cannot add expiring persistent data.");
            return false;
        }
        if (!isValidKey(key)) {
            DreamfireChat.SendMessageToConsole("Invalid key: " + key);
            return false;
        }
        try {
            Add(javaPlugin, block, type, key, value);
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> Remove(javaPlugin, block, key), expiryMillis / 50);
            return true;
        } catch (Exception e) {
            DreamfireChat.SendMessageToConsole("Error while adding expiring persistent data for key: " + key);
            return false;
        }
    }
}
