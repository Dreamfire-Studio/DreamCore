package com.dreamfirestudios.dreamCore.DreamfireMovement;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import com.dreamfirestudios.dreamCore.DreamfireStorage.DreamfireStorageObject;
import com.dreamfirestudios.dreamCore.DreamfireStorage.DreamfireUUIDStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DreamfireMovement {
    public static final String PlayerMovementKey = "DreamfireMovement::MovementLock";
    public static final String PlayerRotationKey = "DreamfireMovement::RotationLock";

    public static Location ReturnLocationLock(Player player){
        var dreamfireStorage = DreamCore.GetDreamfireStorageManager();
        return dreamfireStorage.getValue(PlayerMovementKey, player.getUniqueId());
    }

    public static void LockPlayerLocation(Player player, Location location){
        var dreamfireStorage = DreamCore.GetDreamfireStorageManager();
        DreamfirePlayerActionAPI.LockPlayerAction(DreamfirePlayerAction.PlayerMove, player.getUniqueId());
        dreamfireStorage.storeData(PlayerMovementKey, new DreamfireStorageObject<>(location), player.getUniqueId());
    }

    public static void UnLockPlayerLocation(Player player){
        var dreamfireStorage = DreamCore.GetDreamfireStorageManager();
        DreamfirePlayerActionAPI.UnLockPlayerAction(DreamfirePlayerAction.PlayerMove, player.getUniqueId());
        dreamfireStorage.removeData(PlayerMovementKey, player.getUniqueId());
    }


    public static Location ReturnRotationLock(Player player){
        var dreamfireStorage = DreamCore.GetDreamfireStorageManager();
        return dreamfireStorage.getValue(PlayerRotationKey, player.getUniqueId());
    }

    public static void LockPlayeRotation(Player player, Location location){
        var dreamfireStorage = DreamCore.GetDreamfireStorageManager();
        DreamfirePlayerActionAPI.LockPlayerAction(DreamfirePlayerAction.PlayerRotate, player.getUniqueId());
        dreamfireStorage.storeData(PlayerRotationKey, new DreamfireStorageObject<>(location), player.getUniqueId());
    }

    public static void UnLockPlayeRotation(Player player){
        var dreamfireStorage = DreamCore.GetDreamfireStorageManager();
        DreamfirePlayerActionAPI.UnLockPlayerAction(DreamfirePlayerAction.PlayerRotate, player.getUniqueId());
        dreamfireStorage.removeData(PlayerRotationKey, player.getUniqueId());
    }
}
