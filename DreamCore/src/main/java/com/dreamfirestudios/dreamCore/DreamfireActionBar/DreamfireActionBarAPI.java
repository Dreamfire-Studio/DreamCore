package com.dreamfirestudios.dreamCore.DreamfireActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DreamfireActionBarAPI {
    public static DreamfireActionBar TryGetActionBar(UUID barID){
        return DreamCore.GetDreamfireCore().GetDreamfireActionBar(barID);
    }

    public static boolean IsPlayerInActionBar(Player player){
        for(var dreamfireActionBar : DreamCore.GetDreamfireCore().GetAllDreamfireActionBar()){
            if(dreamfireActionBar.isPlayerViewing(player)) return true;
        }
        return false;
    }

    public static DreamfireActionBar TryGetActionBar(Player player){
        for(var dreamfireActionBar : DreamCore.GetDreamfireCore().GetAllDreamfireActionBar()){
            if(dreamfireActionBar.isPlayerViewing(player)) return dreamfireActionBar;
        }
        return null;
    }
}
