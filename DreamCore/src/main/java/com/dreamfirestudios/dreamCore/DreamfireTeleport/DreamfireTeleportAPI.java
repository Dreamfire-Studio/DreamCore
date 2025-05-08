package com.dreamfirestudios.dreamCore.DreamfireTeleport;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DreamfireTeleportAPI {
    public static void teleportplayer(Player player, LivingEntity liveTarget, Location softTarget, int timeToWait, boolean displayTime, boolean cancelOnMove){
        if(isPlayerTeleporting(player)) return;
        DreamCore.GetDreamfireCore().dreamfireTeleportArrayList.add(new DreamfireTeleport(liveTarget, softTarget, timeToWait, displayTime, cancelOnMove));
    }

    public static boolean isPlayerTeleporting(Player player) {
        for(var teleport : DreamCore.GetDreamfireCore().dreamfireTeleportArrayList)
            if(teleport.IsPlayerTeleporting(player)) return true;
        return false;
    }

    public static void cancelPlayerTeleport(Player player) {
        DreamfireTeleport remove = null;
        for(var teleport : DreamCore.GetDreamfireCore().dreamfireTeleportArrayList){
            if(teleport.CancelPlayerTeleport(player)) remove = teleport;
        }
        if(remove != null) DreamCore.GetDreamfireCore().dreamfireTeleportArrayList.remove(remove);
    }
}
