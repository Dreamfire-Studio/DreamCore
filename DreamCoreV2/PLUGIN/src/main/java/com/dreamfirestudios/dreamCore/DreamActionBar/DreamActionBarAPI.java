package com.dreamfirestudios.dreamCore.DreamActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
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
