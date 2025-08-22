package com.dreamfirestudios.dreamcore.DreamCam;

import com.dreamfirestudios.dreamcore.DreamCore;
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
public class DreamCamPath {
    private final UUID camPathID = UUID.randomUUID();
    private final List<UUID> players = new ArrayList<>();
    private List<DreamCamSet> camSets = new ArrayList<>();

    private final Map<UUID, GameMode> playersGamemodesBefore = new HashMap<>();
    private final Map<UUID, Location> playersLocationsBefore = new HashMap<>();
    private final Map<UUID, Boolean> playersFlyingBefore = new HashMap<>();
    private BukkitRunnable bukkitRunnable;

    /// <summary>Gets all camera sets in this path.</summary>
    public List<DreamCamSet> getCamSets() { return camSets; }

    /// <summary>Gets the players currently on this path.</summary>
    public List<UUID> getPlayers() { return players; }

    /// <summary>
    /// Adds multiple players to the camera path.
    /// </summary>
    public void addPlayer(Player... players) {
        for (var player : players) addPlayer(player);
    }

    /// <summary>
    /// Adds a single player to the camera path.
    /// Dispatches <see cref="CamPathPlayerAddedEvent"/> (cancellable).
    /// </summary>
    public void addPlayer(Player player) {
        if (new CamPathPlayerAddedEvent(this, player).isCancelled()) return;
        players.add(player.getUniqueId());
    }

    /// <summary>
    /// Removes all players from the path.
    /// </summary>
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
    public void onPlayerLeave(Player player) {
        removePlayerFromPath(player, false);
    }

    // ---- Builder ----
    /// <summary>
    /// Builder class for creating <see cref="DreamCamPath"/> instances.
    /// </summary>
    public static class CamPathBuilder {
        private final UUID camPathID = UUID.randomUUID();
        private final List<DreamCamSet> camSets = new ArrayList<>();
        private final List<Player> players = new ArrayList<>();

        /// <summary>
        /// Adds a linear travel path segment.
        /// </summary>
        public CamPathBuilder addTravelPath(Location start, Location end, LookAtType lookAtType, Object data, int durationInTicks) {
            var pathLocations = new ArrayList<Location>();
            pathLocations.add(start.clone());

            var stepX = (end.getX() - start.getX()) / durationInTicks;
            var stepY = (end.getY() - start.getY()) / durationInTicks;
            var stepZ = (end.getZ() - start.getZ()) / durationInTicks;
            var stepYaw = (end.getYaw() - start.getYaw()) / durationInTicks;
            var stepPitch = (end.getPitch() - start.getPitch()) / durationInTicks;

            var step = new Location(start.getWorld(), stepX, stepY, stepZ, stepYaw, stepPitch);

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
        public CamPathBuilder players(Player... players) {
            this.players.addAll(Arrays.asList(players));
            return this;
        }

        /// <summary>
        /// Builds or retrieves the path instance.
        /// </summary>
        public DreamCamPath create() {
            var stored = DreamCore.DreamCamPaths.getOrDefault(camPathID, null);
            if (stored != null) {
                players.forEach(stored::addPlayer);
                return stored;
            }
            var path = new DreamCamPath();
            path.camSets = camSets;
            players.forEach(path::addPlayer);
            return DreamCore.DreamCamPaths.put(camPathID, path);
        }
    }
}