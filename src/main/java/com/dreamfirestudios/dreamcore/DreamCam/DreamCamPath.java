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

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/// <summary>
/// Represents a cinematic camera path composed of multiple camera sets (segments).
/// Handles player state preservation, teleporting, and event dispatch.
/// </summary>
/// <remarks>
/// Use <see cref="DreamCamPath.CamPathBuilder"/> to construct instances.
/// Call <see cref="#startCamPath()"/> to begin, <see cref="#endCamPath(boolean)"/> to stop, and
/// <see cref="#onPlayerLeave(Player)"/> to handle disconnects.
/// </remarks>
/// <example>
/// ```java
/// DreamCamPath path = new DreamCamPath.CamPathBuilder()
///     .addTravelPath(a, b, LookAtType.NoFocus, null, 100)
///     .players(player)
///     .create();
/// path.startCamPath();
/// ```
/// </example>
public class DreamCamPath extends DreamClassID {
    private final List<UUID> players = new ArrayList<>();
    private List<DreamCamSet> camSets = new ArrayList<>();

    private final Map<UUID, GameMode> playersGamemodesBefore = new HashMap<>();
    private final Map<UUID, Location> playersLocationsBefore = new HashMap<>();
    private final Map<UUID, Boolean> playersFlyingBefore = new HashMap<>();
    private BukkitRunnable bukkitRunnable;

    /// <summary>Gets all camera sets in this path.</summary>
    public List<DreamCamSet> getCamSets() { return camSets; }

    /// <summary>Gets the players currently on this path (by UUID).</summary>
    public List<UUID> getPlayers() { return players; }

    /// <summary>
    /// Adds multiple players to the camera path.
    /// </summary>
    /// <param name="players">Players to add.</param>
    public void addPlayer(Player... players) {
        for (var player : players) addPlayer(player);
    }

    /// <summary>
    /// Adds a single player to the camera path.
    /// Dispatches <see cref="CamPathPlayerAddedEvent"/> (cancellable).
    /// </summary>
    /// <param name="player">Player to add.</param>
    public void addPlayer(Player player) {
        if (new CamPathPlayerAddedEvent(this, player).isCancelled()) return;
        players.add(player.getUniqueId());
    }

    /// <summary>
    /// Removes all players from the path.
    /// </summary>
    /// <param name="hasFinished">Whether the path finished normally.</param>
    public void removeAllPlayers(boolean hasFinished) {
        for (var playerUUID : new ArrayList<>(players)) {
            var player = Bukkit.getPlayer(playerUUID);
            if (player != null) removePlayerFromPath(player, hasFinished);
        }
    }

    /// <summary>
    /// Removes a single player from the path and restores their state.
    /// Dispatches <see cref="CamPathPlayerLeaveEvent"/>.
    /// </summary>
    /// <param name="player">Player to remove.</param>
    /// <param name="hasFinished">Whether the path finished normally.</param>
    public void removePlayerFromPath(Player player, boolean hasFinished) {
        if (!players.contains(player.getUniqueId())) return;
        new CamPathPlayerLeaveEvent(this, player, hasFinished);
        player.setGameMode(playersGamemodesBefore.getOrDefault(player.getUniqueId(), player.getGameMode()));
        player.teleport(playersLocationsBefore.getOrDefault(player.getUniqueId(), player.getLocation()));
        player.setFlying(playersFlyingBefore.getOrDefault(player.getUniqueId(), player.isFlying()));
        players.remove(player.getUniqueId());
    }

    /// <summary>
    /// Starts playback of the camera path for all players.
    /// Dispatches <see cref="CamPathStartEvent"/>.
    /// </summary>
    public void startCamPath() {
        if (bukkitRunnable != null) return;
        for (var playerUUID : players) {
            var player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            playersGamemodesBefore.put(playerUUID, player.getGameMode());
            playersLocationsBefore.put(playerUUID, player.getLocation());
            playersFlyingBefore.put(playerUUID, player.isFlying());
            player.setGameMode(GameMode.SPECTATOR);
        }
        new CamPathStartEvent(this);
        bukkitRunnable = new DreamCamPathRunnable(this);
        bukkitRunnable.runTaskTimer(DreamCore.DreamCore, 0L, 1L);
    }

    /// <summary>
    /// Stops the path and restores all player states.
    /// Dispatches <see cref="CamPathStopEvent"/>.
    /// </summary>
    /// <param name="hasFinished">Whether the path finished normally.</param>
    public void endCamPath(boolean hasFinished) {
        for (var playerUUID : new ArrayList<>(players)) {
            var player = Bukkit.getPlayer(playerUUID);
            if (player != null) removePlayerFromPath(player, hasFinished);
        }
        new CamPathStopEvent(this);
        if (bukkitRunnable != null) {
            bukkitRunnable.cancel();
            bukkitRunnable = null;
        }
    }

    /// <summary>
    /// Called when the plugin is disabled to safely remove players.
    /// </summary>
    public void onDisable() {
        removeAllPlayers(false);
    }

    /// <summary>
    /// Called when a player leaves unexpectedly.
    /// </summary>
    /// <param name="player">Player who left.</param>
    public void onPlayerLeave(Player player) {
        removePlayerFromPath(player, false);
    }

    // ---- Builder ----

    /// <summary>
    /// Builder class for creating <see cref="DreamCamPath"/> instances.
    /// </summary>
    public static class CamPathBuilder {
        private final List<DreamCamSet> camSets = new ArrayList<>();
        private final List<Player> players = new ArrayList<>();

        /// <summary>
        /// Adds a linear travel path segment.
        /// </summary>
        /// <param name="start">Start location (used as initial snapshot).</param>
        /// <param name="end">End location (final point before stepping).</param>
        /// <param name="lookAtType">Rotation strategy.</param>
        /// <param name="data">Look-at data (Location or Entity) depending on <paramref name="lookAtType"/>.</param>
        /// <param name="durationInTicks">Number of ticks to reach the end (must be &gt; 0).</param>
        /// <returns>This builder.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="durationInTicks"/> ≤ 0.</exception>
        public CamPathBuilder addTravelPath(Location start, Location end, LookAtType lookAtType, Object data, int durationInTicks) {
            if (durationInTicks <= 0) throw new IllegalArgumentException("durationInTicks must be > 0");

            var pathLocations = new ArrayList<Location>();
            pathLocations.add(start.clone());

            var stepX = (end.getX() - start.getX()) / durationInTicks;
            var stepY = (end.getY() - start.getY()) / durationInTicks;
            var stepZ = (end.getZ() - start.getZ()) / durationInTicks;
            var stepYaw = (end.getYaw() - start.getYaw()) / durationInTicks;
            var stepPitch = (end.getPitch() - start.getPitch()) / durationInTicks;

            var step = new Location(start.getWorld(), stepX, stepY, stepZ, (float) stepYaw, (float) stepPitch);

            for (int i = 1; i <= durationInTicks; i++) {
                var prevLocation = pathLocations.get(i - 1).clone();
                var next = prevLocation.add(step);
                next.setYaw(next.getYaw() + stepYaw);
                next.setPitch(next.getPitch() + stepPitch);
                pathLocations.add(next);
            }

            camSets.add(new DreamCamSet(pathLocations, lookAtType, data));
            return this;
        }

        /// <summary>
        /// Adds players to this path.
        /// </summary>
        /// <param name="players">Players to add (may be empty).</param>
        /// <returns>This builder.</returns>
        public CamPathBuilder players(Player... players) {
            this.players.addAll(Arrays.asList(players));
            return this;
        }

        /// <summary>
        /// Builds or retrieves the path instance.
        /// </summary>
        /// <returns>
        /// The existing stored path if one was found, otherwise stores and returns the result of <c>Map.put</c>
        /// (i.e., the previous value, which is typically <c>null</c>).
        /// Behavior preserved intentionally.
        /// </returns>
        public DreamCamPath create() {
            var path = new DreamCamPath();
            path.camSets = camSets;
            players.forEach(path::addPlayer);
            return DreamCore.DreamCamPaths.put(path.getClassID(), path);
        }
    }
}