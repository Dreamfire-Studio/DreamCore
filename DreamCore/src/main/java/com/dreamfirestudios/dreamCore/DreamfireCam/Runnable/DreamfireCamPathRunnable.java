package com.dreamfirestudios.dreamCore.DreamfireCam.Runnable;

import com.dreamfirestudios.dreamCore.DreamfireCam.DreamfireCamPath;
import com.dreamfirestudios.dreamCore.DreamfireCam.Events.CamPathPointReachedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DreamfireCamPathRunnable extends BukkitRunnable {
    private List<Location> combinedLocations = new ArrayList<>();
    private final DreamfireCamPath camPath;
    private int index;

    public DreamfireCamPathRunnable(DreamfireCamPath camPath){
        this.camPath = camPath;
        this.index = 0;
        for(var camSet : this.camPath.getCamSets()) combinedLocations.addAll(camSet.points());
    }

    @Override
    public void run() {
        if(index >= combinedLocations.size()){
            camPath.EndCamPath(true);
        }
        else{
            var nextLocation = combinedLocations.get(index);
            for(var camSet : this.camPath.getCamSets()){
                if(!camSet.points().contains(nextLocation)) continue;
                var lastPoint = camSet.points().get(camSet.points().size() - 1);
                nextLocation = camSet.SetRotation(nextLocation, lastPoint);
            }
            for(var playerUUID : camPath.getPlayers()){
                var player  = Bukkit.getPlayer(playerUUID);
                if(player == null) continue;
                player.teleport(nextLocation);
            }
            new CamPathPointReachedEvent(camPath, nextLocation);
            index += 1;
        }
    }
}
