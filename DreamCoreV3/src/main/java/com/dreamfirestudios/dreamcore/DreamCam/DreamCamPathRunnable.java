package com.dreamfirestudios.dreamcore.DreamCam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Bukkit runnable that steps through a <see cref="DreamCamPath"/> point-by-point,
/// teleporting players and dispatching events.
/// </summary>
public class DreamCamPathRunnable extends BukkitRunnable {
    private final List<Location> combinedLocations = new ArrayList<>();
    private final DreamCamPath camPath;
    private int index;

    public DreamCamPathRunnable(DreamCamPath camPath) {
        this.camPath = camPath;
        this.index = 0;
        for (var camSet : camPath.getCamSets()) combinedLocations.addAll(camSet.points());
    }

    @Override
    public void run() {
        if (index >= combinedLocations.size()) {
            camPath.endCamPath(true);
            return;
        }

        var nextLocation = combinedLocations.get(index);
        for (var camSet : camPath.getCamSets()) {
            if (!camSet.points().contains(nextLocation)) continue;
            var lastPoint = camSet.points().get(camSet.points().size() - 1);
            nextLocation = camSet.setRotation(nextLocation, lastPoint);
        }

        for (var playerUUID : camPath.getPlayers()) {
            var player = Bukkit.getPlayer(playerUUID);
            if (player != null) player.teleport(nextLocation);
        }

        new CamPathPointReachedEvent(camPath, nextLocation);
        index++;
    }
}