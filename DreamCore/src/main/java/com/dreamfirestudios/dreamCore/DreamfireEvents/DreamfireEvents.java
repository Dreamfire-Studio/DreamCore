package com.dreamfirestudios.dreamCore.DreamfireEvents;

import com.dreamfirestudios.dreamCore.DreamfireLuckPerms.DreamfireLuckPerms;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DreamfireEvents {
    public static boolean HandleDreamfireEvents(IDreamfireEvents iDreamfireEvents, Entity entity){
        if(entity == null) return false;

        if(iDreamfireEvents.worlds().length > 0){
            var worlds = Arrays.stream(iDreamfireEvents.worlds()).toList();
            var world = entity.getLocation().getWorld();
            if(world != null && !worlds.contains(world.getName())) return false;
        }

        if(iDreamfireEvents.difficulty() != null){
            var world = entity.getLocation().getWorld();
            if(world != null && world.getDifficulty() != iDreamfireEvents.difficulty()) return false;
        }

        if(entity instanceof Player player){
            if(iDreamfireEvents.weatherType() != null){
                if(player.getPlayerWeather() != iDreamfireEvents.weatherType()) return false;
            }

            if(iDreamfireEvents.gameMode() != null){
                if(player.getGameMode() != iDreamfireEvents.gameMode()) return false;
            }

            for(var perm : iDreamfireEvents.perms()){
                var user = DreamfireLuckPerms.getUser(player);
                if(!DreamfireLuckPerms.hasPermission(user, perm)) return false;
            }
        }

        return !iDreamfireEvents.op() || entity.isOp();
    }

    public static boolean HandleDreamfireEvents(IDreamfireEvents iDreamfireEvents, Location location){
        if(iDreamfireEvents.worlds().length > 0){
            var worlds = Arrays.stream(iDreamfireEvents.worlds()).toList();
            var world = location.getWorld();
            if(world != null && !worlds.contains(world.getName())) return false;
        }

        if(iDreamfireEvents.difficulty() != null){
            var world = location.getWorld();
            if(world != null && world.getDifficulty() != iDreamfireEvents.difficulty()) return false;
        }

        return true;
    }
}
