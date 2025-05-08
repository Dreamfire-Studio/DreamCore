package com.dreamfirestudios.dreamCore.DreamfireLocationLimiter;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireLocationLimiter.Events.LocationLimiterLimitHit;
import com.dreamfirestudios.dreamCore.DreamfireLocationLimiter.Events.LocationLimiterPlayerAddedEvent;
import com.dreamfirestudios.dreamCore.DreamfireLocationLimiter.Events.LocationLimiterPlayerRemovedEvent;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.World;

public class DreamfireLocationLimiter {
    @Getter private final UUID id = UUID.randomUUID();
    private List<Player> players = new ArrayList<>();
    @Getter private LocationLimiterType locationLimiterType = LocationLimiterType.SNAP_TO_ORIGIN;
    @Getter private LocationLimiterStart locationLimiterStart = LocationLimiterStart.ORIGIN_POINT;
    private String edgeMessage = "You have reached the edge of the location!";
    @Getter private Location originPoint;
    private int distanceExtents = 10;
    @Getter private boolean stopped = false;
    private World originWorld;


    public void AddPlayer(Player player){
        if(!players.contains(player)){
            if(new LocationLimiterPlayerAddedEvent(this, player).isCancelled()) return;
            if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerLocationLimiter, player.getUniqueId())) return;
            players.add(player);
        }
    }

    public void RemovePlayer(Player player){
        if(players.contains(player)){
            new LocationLimiterPlayerRemovedEvent(this, player);
            players.remove(player);
        }
    }

    /**
     * Initializes and starts the location limiter by teleporting players to the origin point.
     */
    public void startLocationLimiter() {
        if (locationLimiterStart == LocationLimiterStart.ORIGIN_POINT) {
            for (Player player : players) {
                player.teleport(originPoint);
            }
        }
        originWorld = originPoint.getWorld();
    }

    /**
     * Ticks every game loop and checks if any players are out of bounds.
     * If a player exceeds the allowed distance from the origin, handle them accordingly.
     */
    public void tickLocationLimiter() {
        if (stopped) return;

        for (Player player : players) {
            if (player.getWorld() == null || !player.getWorld().equals(originWorld)) {
                player.teleport(originPoint);
                return;
            }

            double distance = player.getLocation().distance(originPoint);
            if (distance >= distanceExtents) {
                handleOutOfBoundsPlayer(player, distance);
            }
        }
    }

    /**
     * Handles a player who has exceeded the allowed distance.
     *
     * @param player The player who exceeded the distance.
     * @param distance The distance the player is from the origin.
     */
    private void handleOutOfBoundsPlayer(Player player, double distance) {
        // Display an edge message to the player
        player.sendMessage(edgeMessage);

        switch (locationLimiterType) {
            case SNAP_TO_ORIGIN:
                player.teleport(originPoint);
                new LocationLimiterLimitHit(this, player);
                break;
            case PUSH_BACK:
                pushPlayerBack(player, distance);
                new LocationLimiterLimitHit(this, player);
                break;
        }
    }

    /**
     * Pushes the player back towards the origin point if they exceed the distance limit.
     *
     * @param player The player to push back.
     * @param distance The distance the player is from the origin.
     */
    private void pushPlayerBack(Player player, double distance) {
        Vector direction = player.getLocation().toVector().subtract(originPoint.toVector()).normalize();
        player.setVelocity(direction.multiply(0.5));
    }

    /**
     * Toggles the location limiter state (active/inactive).
     *
     * @param state The state to set the limiter to.
     */
    public void toggleLocationLimiter(boolean state) {
        stopped = !state;
    }

    /**
     * Stops the location limiter and removes it from the DreamfireCore registry.
     */
    public void stopLocationLimiter() {
        stopped = true;
        DreamCore.GetDreamfireCore().DeleteDreamfireLocationLimiter(id);
    }

    public static class LocationLimiterBuilder {
        private UUID limiterID = UUID.randomUUID();
        private List<Player> players = new ArrayList<>();
        private LocationLimiterType locationLimiterType = LocationLimiterType.SNAP_TO_ORIGIN;
        private LocationLimiterStart locationLimiterStart = LocationLimiterStart.ORIGIN_POINT;
        private String edgeMessage = "You have reached the edge of the location!";
        private int distanceExtents = 10;

        public LocationLimiterBuilder addPlayer(Player player) {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            players.add(player);
            return this;
        }

        public LocationLimiterBuilder locationLimiterType(LocationLimiterType locationLimiterType) {
            if (locationLimiterType == null) throw new IllegalArgumentException("LocationLimiterType cannot be null");
            this.locationLimiterType = locationLimiterType;
            return this;
        }

        public LocationLimiterBuilder locationLimiterStart(LocationLimiterStart locationLimiterStart) {
            if (locationLimiterStart == null) throw new IllegalArgumentException("LocationLimiterStart cannot be null");
            this.locationLimiterStart = locationLimiterStart;
            return this;
        }

        public LocationLimiterBuilder edgeMessage(String edgeMessage) {
            if (edgeMessage == null || edgeMessage.isEmpty()) throw new IllegalArgumentException("Edge message cannot be null or empty");
            this.edgeMessage = edgeMessage;
            return this;
        }

        public LocationLimiterBuilder distanceExtents(int distanceExtents) {
            if (distanceExtents <= 0) throw new IllegalArgumentException("Distance extents must be greater than zero");
            this.distanceExtents = distanceExtents;
            return this;
        }

        /**
         * Builds and initializes a DreamfireLocationLimiter with the configured settings.
         *
         * @param originPoint The origin point around which the limiter is applied.
         * @return A new DreamfireLocationLimiter instance.
         */
        public DreamfireLocationLimiter build(Location originPoint) {
            if (originPoint == null) throw new IllegalArgumentException("Origin point cannot be null");

            var storedLimiter = DreamCore.GetDreamfireCore().GetDreamfireLocationLimiter(limiterID);
            if (storedLimiter != null){
                players.forEach(storedLimiter::AddPlayer);
                return storedLimiter;
            }

            DreamfireLocationLimiter locationLimiter = new DreamfireLocationLimiter();
            players.forEach(locationLimiter::AddPlayer);
            locationLimiter.locationLimiterType = locationLimiterType;
            locationLimiter.locationLimiterStart = locationLimiterStart;
            locationLimiter.edgeMessage = edgeMessage;
            locationLimiter.originPoint = originPoint;
            locationLimiter.distanceExtents = distanceExtents;
            return DreamCore.GetDreamfireCore().AddDreamfireLocationLimiter(limiterID, locationLimiter);
        }
    }
}
