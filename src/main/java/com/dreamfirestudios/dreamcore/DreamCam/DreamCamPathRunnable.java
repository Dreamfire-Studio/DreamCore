/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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