package com.dreamfirestudios.dreamCore.DreamfirePlayer;


import com.dreamfirestudios.dreamCore.DreamCore;

import java.util.ArrayList;
import java.util.UUID;

public class DreamfirePlayerActionAPI {
    public static ArrayList<DreamfirePlayerAction> ReturnPlayerLocks(UUID uuid){
        return DreamCore.GetDreamfireCore().GetDreamfirePlayerActions(uuid);
    }

    public static boolean CanPlayerAction(DreamfirePlayerAction playerAction, UUID player){
        var playerActionData = ReturnPlayerLocks(player);
        return !playerActionData.contains(playerAction);
    }

    public static void LockPlayerAction(DreamfirePlayerAction playerAction, UUID player){
        var playerActionData = ReturnPlayerLocks(player);
        playerActionData.add(playerAction);
        DreamCore.GetDreamfireCore().AddDreamfirePlayerActions(player, playerActionData);
    }

    public static void UnLockPlayerAction(DreamfirePlayerAction playerAction, UUID player){
        var playerActionData = ReturnPlayerLocks(player);
        playerActionData.remove(playerAction);
        DreamCore.GetDreamfireCore().AddDreamfirePlayerActions(player, playerActionData);
    }
}