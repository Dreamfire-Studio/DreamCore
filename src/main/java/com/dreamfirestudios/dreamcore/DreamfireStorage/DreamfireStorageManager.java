package com.dreamfirestudios.dreamcore.DreamfireStorage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <summary>
 * Manages simple, plugin-scoped storage objects for both
 * server-wide (global) and per-player contexts.
 * </summary>
 *
 * <remarks>
 * <list type="bullet">
 *   <item>Global (server) storage is addressed by passing <c>null</c> for the <paramref name="uuid"/> parameter.</item>
 *   <item>Per-player storage is grouped under that player's <see cref="java.util.UUID"/>.</item>
 *   <item>When storing data, a <see cref="StorageObjectAddedEvent"/> is fired; cancellation blocks the put.</item>
 *   <item>When removing data, a <see cref="StorageObjectRemovedEvent"/> is fired (after removal in the current logic).</item>
 * </list>
 * </remarks>
 * <example>
 * <code>
 * var storage = new DreamfireStorageManager();
 * var id = UUID.randomUUID();
 * storage.storeData("cooldown", new DreamfireStorageObject&lt;&gt;(20), id);
 * Integer cd = storage.getValue("cooldown", id); // 20
 * </code>
 * </example>
 */
public class DreamfireStorageManager {

    /**
     * <summary>
     * Server-wide storage (when <c>uuid == null</c>).
     * </summary>
     */
    private final Map<String, DreamfireStorageObject<?>> serverStorage = new LinkedHashMap<>();

    /**
     * <summary>
     * Per-player storage; each player has their own namespaced map.
     * </summary>
     */
    private final Map<UUID, Map<String, DreamfireStorageObject<?>>> playerStorage = new LinkedHashMap<>();

    /**
     * <summary>
     * Returns a stored object wrapper for the given key and player namespace.
     * </summary>
     *
     * <typeparam name="T">Value type contained in the storage object.</typeparam>
     * <param name="key">Key (converted via <see cref="#keyToString(Object)"/>).</param>
     * <param name="uuid">Player UUID, or <c>null</c> for server/global storage.</param>
     * <returns>The storage wrapper instance, or <c>null</c> if not found.</returns>
     */
    @SuppressWarnings("unchecked")
    public <T> DreamfireStorageObject<T> getData(Object key, UUID uuid) {
        return (DreamfireStorageObject<T>) getStorageMap(uuid).get(keyToString(key));
    }

    /**
     * <summary>
     * Returns the stored value for the given key and namespace.
     * </summary>
     *
     * <typeparam name="T">Value type.</typeparam>
     * <param name="key">Key (converted via <see cref="#keyToString(Object)"/>).</param>
     * <param name="uuid">Player UUID, or <c>null</c> for global storage.</param>
     * <returns>The stored value, or <c>null</c> if not present.</returns>
     */
    public <T> T getValue(Object key, UUID uuid) {
        DreamfireStorageObject<T> storageObject = getData(key, uuid);
        return storageObject == null ? null : storageObject.storageData();
    }

    /**
     * <summary>
     * Stores a <see cref="DreamfireStorageObject"/> for a given key/namespace.
     * </summary>
     *
     * <typeparam name="T">Value type.</typeparam>
     * <param name="key">Key (converted via <see cref="#keyToString(Object)"/>).</param>
     * <param name="obj">Object wrapper to store.</param>
     * <param name="uuid">Player UUID, or <c>null</c> for global storage.</param>
     * <returns>The same object wrapper that was provided.</returns>
     * <remarks>
     * Fires <see cref="StorageObjectAddedEvent"/>. If the event is cancelled, the object is <b>not</b> stored.
     * </remarks>
     * <example>
     * <code>
     * manager.storeData("state", new DreamfireStorageObject&lt;&gt;("RUNNING"), null);
     * </code>
     * </example>
     */
    public <T> DreamfireStorageObject<T> storeData(Object key, DreamfireStorageObject<T> obj, UUID uuid) {
        if(!new StorageObjectAddedEvent(obj).isCancelled()){
            getStorageMap(uuid).put(keyToString(key), obj);
        }
        return obj;
    }

    /**
     * <summary>
     * Checks whether a key exists for the given namespace.
     * </summary>
     *
     * <param name="key">Key to check.</param>
     * <param name="uuid">Player UUID, or <c>null</c> for global storage.</param>
     * <returns><c>true</c> if present; otherwise <c>false</c>.</returns>
     */
    public boolean containsData(Object key, UUID uuid) {
        return getStorageMap(uuid).containsKey(keyToString(key));
    }

    /**
     * <summary>
     * Removes a stored object (if present) for the given key/namespace.
     * </summary>
     *
     * <param name="key">Key to remove.</param>
     * <param name="uuid">Player UUID, or <c>null</c> for global storage.</param>
     * <returns>The removed storage wrapper, or <c>null</c> if not present.</returns>
     * <remarks>
     * Current behavior: the object is removed first; then <see cref="StorageObjectRemovedEvent"/> is fired.
     * If that event is cancelled, this method still leaves the object removed (original logic retained).
     * </remarks>
     * <example>
     * <code>
     * var removed = manager.removeData("state", null);
     * if (removed != null) {
     *     // handle removal
     * }
     * </code>
     * </example>
     */
    public DreamfireStorageObject<?> removeData(Object key, UUID uuid) {
        var data = getStorageMap(uuid).remove(keyToString(key));
        if(data != null && !new StorageObjectRemovedEvent(data).isCancelled()){
            getStorageMap(uuid).remove(keyToString(key));
        }
        return data;
    }

    /**
     * <summary>
     * Returns the appropriate storage map (global vs per-player).
     * </summary>
     *
     * <param name="uuid">Player UUID, or <c>null</c> for global storage.</param>
     * <returns>Map used to store objects in that namespace.</returns>
     */
    private Map<String, DreamfireStorageObject<?>> getStorageMap(UUID uuid) {
        return (uuid == null) ? serverStorage : playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
    }

    /**
     * <summary>
     * Converts a key object to a <see cref="String"/> key.
     * </summary>
     *
     * <param name="key">Any non-null key object.</param>
     * <returns><see cref="String"/> representation used in the map.</returns>
     */
    private String keyToString(Object key) {
        return key.toString();
    }
}