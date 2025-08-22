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
/// <remarks>
/// This runnable does not manage its own lifecycle beyond signalling completion
/// (by calling <see cref="DreamCamPath.endCamPath(boolean)"/> when all points are consumed).
/// </remarks>
public class DreamCamPathRunnable extends BukkitRunnable {
    private final List<Location> combinedLocations = new ArrayList<>();
    private final DreamCamPath camPath;
    private int index;

    /// <summary>
    /// Constructs a runnable for a given path and flattens all segment points.
    /// </summary>
    /// <param name="camPath">Path to play.</param>
    public DreamCamPathRunnable(DreamCamPath camPath) {
        this.camPath = camPath;
        this.index = 0;
        for (var camSet : camPath.getCamSets()) combinedLocations.addAll(camSet.points());
    }

    /// <summary>
    /// Teleports all players to the next location, adjusts rotation per segment rule,
    /// and fires <see cref="CamPathPointReachedEvent"/>. Stops path when finished.
    /// </summary>
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