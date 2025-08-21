package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.entity.Player;

public class DreamActionBarAPI {
    public static boolean IsPlayerInActionBar(Player player){
        for(var dreamActionBar : DreamCore.DreamActionBars.values()){
            if(dreamActionBar.isPlayerViewing(player)) {
                return true;
            }
        }
        return false;
    }
}
