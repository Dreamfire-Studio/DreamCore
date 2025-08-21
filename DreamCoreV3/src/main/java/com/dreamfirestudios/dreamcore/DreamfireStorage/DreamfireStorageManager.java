package com.dreamfirestudios.dreamcore.DreamfireStorage;

import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DreamfireStorageManager {
    private final Map<String, DreamfireStorageObject<?>> serverStorage = new LinkedHashMap<>();
    private final Map<UUID, Map<String, DreamfireStorageObject<?>>> playerStorage = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> DreamfireStorageObject<T> getData(Object key, UUID uuid) {
        return (DreamfireStorageObject<T>) getStorageMap(uuid).get(keyToString(key));
    }

    public <T> T getValue(Object key, UUID uuid) {
        DreamfireStorageObject<T> storageObject = getData(key, uuid);
        return storageObject == null ? null : storageObject.storageData();
    }

    public <T> DreamfireStorageObject<T> storeData(Object key, DreamfireStorageObject<T> obj, UUID uuid) {
        if(!new StorageObjectAddedEvent(obj).isCancelled()){
            getStorageMap(uuid).put(keyToString(key), obj);
        }
        return obj;
    }

    public boolean containsData(Object key, UUID uuid) {
        return getStorageMap(uuid).containsKey(keyToString(key));
    }

    public DreamfireStorageObject<?> removeData(Object key, UUID uuid) {
        var data = getStorageMap(uuid).remove(keyToString(key));
        if(data != null && !new StorageObjectRemovedEvent(data).isCancelled()){
            getStorageMap(uuid).remove(keyToString(key));
        }
        return data;
    }

    private Map<String, DreamfireStorageObject<?>> getStorageMap(UUID uuid) {
        return (uuid == null) ? serverStorage : playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
    }

    private String keyToString(Object key) {
        return key.toString();
    }
}
