package com.dreamfirestudios.dreamCore.DreamfireCam;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireBossBar.Events.BossBarPlayerAddedEvent;
import com.dreamfirestudios.dreamCore.DreamfireCam.Events.CamPathPlayerAddedEvent;
import com.dreamfirestudios.dreamCore.DreamfireCam.Events.CamPathPlayerLeaveEvent;
import com.dreamfirestudios.dreamCore.DreamfireCam.Events.CamPathStartEvent;
import com.dreamfirestudios.dreamCore.DreamfireCam.Events.CamPathStopEvent;
import com.dreamfirestudios.dreamCore.DreamfireCam.Runnable.DreamfireCamPathRunnable;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DreamfireCamPath {
    private UUID camPathID = UUID.randomUUID();
    private List<UUID> players = new ArrayList<>();
    private List<DreamfireCamSet> camSets = new ArrayList<>();
    private final HashMap<UUID, GameMode> playersGamemodesBefore = new HashMap<>();
    private final HashMap<UUID, Location> playersLocationsBefore = new HashMap<>();
    private final HashMap<UUID, Boolean> playersFlyingBefore = new HashMap<>();
    private BukkitRunnable bukkitRunnable;

    public List<DreamfireCamSet> getCamSets(){return camSets;}
    public List<UUID> getPlayers(){return players;}

    /**
     * Adds players to the camera path.
     * @param players The players to add to the camera path.
     */
    public void addPlayer(Player... players){
        for(var player : players) addPlayer(player);
    }

    /**
     * Adds a single player to the camera path.
     * @param player The player to add to the camera path.
     * @throws IllegalArgumentException if the player is already added.
     */
    public void addPlayer(Player player) {
        if(new CamPathPlayerAddedEvent(this, player).isCancelled()) return;
        if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerCamPath, player.getUniqueId())) return;
        players.add(player.getUniqueId());
    }

    /**
     * Removes all players from the camera path.
     * @param hasFinished Boolean indicating if the camera path finished successfully.
     */
    public void RemoveAllPlayers(boolean hasFinished){
        for(var playeUUID : players){
            var player = Bukkit.getPlayer(playeUUID);
            if(player == null) continue;
            RemovePlayerFromPath(player, hasFinished);
        }
    }

    /**
     * Removes a specific player from the camera path.
     * @param player The player to remove from the camera path.
     * @param hasFinished Boolean indicating if the camera path finished successfully.
     */
    public void RemovePlayerFromPath(Player player, boolean hasFinished){
        if(!players.contains(player.getUniqueId())) return;
        new CamPathPlayerLeaveEvent(this, player, hasFinished);
        player.setGameMode(playersGamemodesBefore.getOrDefault(player.getUniqueId(), player.getGameMode()));
        player.teleport(playersLocationsBefore.getOrDefault(player.getUniqueId(), player.getLocation()));
        player.setFlying(playersFlyingBefore.getOrDefault(player.getUniqueId(), player.isFlying()));
        this.players.remove(player.getUniqueId());
    }

    /**
     * Starts the camera path for all players.
     */
    public void StartCamPath(){
        if(bukkitRunnable != null) return;
        for(var playeUUID : players){
            var player = Bukkit.getPlayer(playeUUID);
            if(player == null) continue;
            playersGamemodesBefore.put(playeUUID, player.getGameMode());
            playersLocationsBefore.put(playeUUID, player.getLocation());
            playersFlyingBefore.put(playeUUID, player.isFlying());
            player.setGameMode(GameMode.SPECTATOR);
        }
        new CamPathStartEvent(this);
        bukkitRunnable = new DreamfireCamPathRunnable(this);
        bukkitRunnable.runTaskTimer(DreamCore.GetDreamfireCore(), 0L, 1L);
    }

    /**
     * Ends the camera path and restores player states.
     * @param hasFinished Boolean indicating if the camera path finished successfully.
     */
    public void EndCamPath(boolean hasFinished){
        for(var playerUUID : players){
            var player = Bukkit.getPlayer(playerUUID);
            if(player == null) continue;
            RemovePlayerFromPath(player, hasFinished);
        }
        new CamPathStopEvent(this);
        bukkitRunnable.cancel();
        bukkitRunnable = null;
    }

    /**
     * Called when the path is disabled to remove all players.
     */
    public void OnDisable(){
        RemoveAllPlayers(false);
    }

    /**
     * Handles player leaving the camera path.
     * @param player The player leaving the camera path.
     */
    public void OnPlayerLeave(Player player){
        RemovePlayerFromPath(player, false);
    }

    public static class CamPathBuilder{
        private UUID camPathID = UUID.randomUUID();
        private List<DreamfireCamSet> camSets = new ArrayList<>();
        private List<Player> players = new ArrayList<>();

        /**
         * Adds a travel path to the camera path.
         * @param start The starting location of the path.
         * @param end The ending location of the path.
         * @param lookAtType The type of lookAt behavior.
         * @param data Additional data for the camera path.
         * @param durationInTicks The duration of the travel path in ticks.
         * @return The builder instance for chaining.
         */
        public CamPathBuilder addTravelPath(Location start, Location end, LookAtType lookAtType, Object data, int durationInTicks){
            var pathLocations = new ArrayList<Location>();
            pathLocations.add(start);

            var stepX = (end.getX() - start.getX()) / durationInTicks;
            var stepY = (end.getY() - start.getY()) / durationInTicks;
            var stepZ = (end.getZ() - start.getZ()) / durationInTicks;
            var stepYaw = (end.getYaw() - start.getYaw()) / durationInTicks;
            var stepPitch = (end.getPitch() - start.getPitch()) / durationInTicks;
            var step = new Location(start.getWorld(), stepX, stepY, stepZ, stepYaw, stepPitch);

            for (int i = 1; i <= durationInTicks; i++) {
                var prevLocation = pathLocations.get(i-1).clone();
                var nextlocation = prevLocation.add(step);
                var yaw = nextlocation.getYaw() + stepYaw;
                var pitch = nextlocation.getPitch() + stepPitch;
                nextlocation.setYaw(yaw);
                nextlocation.setPitch(pitch);
                pathLocations.add(nextlocation);
            }

            camSets.add(new DreamfireCamSet(pathLocations, lookAtType, data));
            return this;
        }

        /**
         * Adds players to the camera path.
         * @param players The players to add to the camera path.
         * @return The builder instance for chaining.
         */
        public CamPathBuilder players(Player... players){
            this.players.addAll(Arrays.asList(players));
            return this;
        }

        /**
         * Creates the camera path.
         * @return The created DreamfireCamPath instance.
         */
        public DreamfireCamPath Create(){
            var storedCameraPath = DreamCore.GetDreamfireCore().GetDreamfireCamPath(camPathID);
            if(storedCameraPath != null){
                players.forEach(storedCameraPath::addPlayer);
                return storedCameraPath;
            }
            var cameraPath = new DreamfireCamPath();
            cameraPath.camSets = camSets;
            players.forEach(cameraPath::addPlayer);
            return DreamCore.GetDreamfireCore().AddDreamfireCamPath(camPathID, cameraPath);
        }
    }
}
