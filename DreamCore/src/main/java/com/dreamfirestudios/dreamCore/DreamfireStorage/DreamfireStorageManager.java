package com.dreamfirestudios.dreamCore.DreamfireStorage;

import com.dreamfirestudios.dreamCore.DreamfireStorage.Events.StorageObjectRemovedEvent;
import com.dreamfirestudios.dreamCore.DreamfireStorage.Events.StorageObjectStoredEvent;

import java.util.LinkedHashMap;
import java.util.UUID;

public class DreamfireStorageManager {
    private final LinkedHashMap<String, DreamfireStorageObject<?>> serverStorage = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, LinkedHashMap<String, DreamfireStorageObject<?>>> playerStorage = new LinkedHashMap<>();

    public <T> DreamfireStorageObject<T> getData(Object key, UUID uuid){
        if(uuid == null) return (DreamfireStorageObject<T>) serverStorage.getOrDefault(key.toString(), null);
        else{
            var storageObjects = playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
            return (DreamfireStorageObject<T>) storageObjects.getOrDefault(key.toString(), null);
        }
    }

    public <T> T getValue(Object key, UUID uuid){
        if(uuid == null) {
            var storageObject = (DreamfireStorageObject<T>) serverStorage.getOrDefault(key.toString(), null);
            return storageObject == null ? null : storageObject.storageData();
        }
        else{
            var storageObjects = playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
            var storageObject = (DreamfireStorageObject<T>) storageObjects.getOrDefault(key.toString(), null);
            return storageObject == null ? null : storageObject.storageData();
        }
    }

    public <T> DreamfireStorageObject<T> storeData(Object key, DreamfireStorageObject<T> dreamfireStorageObject, UUID uuid) {
        if(uuid == null) serverStorage.put(key.toString(), dreamfireStorageObject);
        else{
            var storageObjects = playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
            storageObjects.put(key.toString(), dreamfireStorageObject);
        }
        new StorageObjectStoredEvent(dreamfireStorageObject);
        return dreamfireStorageObject;
    }

    public boolean containsData(Object key, UUID uuid) {
        if(uuid == null) return serverStorage.containsKey(key.toString());
        else return playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>()).containsKey(key.toString());
    }

    public DreamfireStorageObject<?> removeData(Object key, UUID uuid) {
        DreamfireStorageObject<?> dreamfireStorageObject;
        if(uuid == null) dreamfireStorageObject = serverStorage.remove(key.toString());
        else dreamfireStorageObject =  playerStorage.computeIfAbsent(uuid, k -> new LinkedHashMap<>()).remove(key.toString());
        new StorageObjectRemovedEvent(dreamfireStorageObject);
        return dreamfireStorageObject;
    }
}
