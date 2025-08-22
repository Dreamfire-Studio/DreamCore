package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DreamLocationLimiter extends DreamClassID {

    private final List<Player> players = new ArrayList<>();

    @Getter
    private LocationLimiterType locationLimiterType = LocationLimiterType.SNAP_TO_ORIGIN;

    @Getter
    private LocationLimiterStart locationLimiterStart = LocationLimiterStart.ORIGIN_POINT;

    @Getter
    private String edgeMessage = "You have reached the edge of the location!";

    @Getter
    private Location originPoint;

    @Getter
    private int distanceExtents = 10;

    @Getter
    private boolean stopped = false;

    private World originWorld;

    /**
     * Add a player to the limiter (fires cancellable {@link LocationLimiterPlayerAddedEvent}).
     */
    public void AddPlayer(Player player) {
        if (player == null) return;
        if (players.contains(player)) return;

        LocationLimiterPlayerAddedEvent evt = new LocationLimiterPlayerAddedEvent(this, player);
        if (evt.isCancelled()) return;

        if (!canPlayerBeLimited(player)) return;

        players.add(player);
    }

    /**
     * Remove a player from the limiter (fires {@link LocationLimiterPlayerRemovedEvent}).
     */
    public void RemovePlayer(Player player) {
        if (player == null) return;
        if (!players.remove(player)) return;
        new LocationLimiterPlayerRemovedEvent(this, player);
    }

    /**
     * Initializes and starts the location limiter. Teleports players to origin if configured to do so.
     */
    public void startLocationLimiter() {
        if (originPoint == null || originPoint.getWorld() == null) {
            throw new IllegalStateException("Origin point/world must be set before starting the limiter.");
        }
        originWorld = originPoint.getWorld();

        if (locationLimiterStart == LocationLimiterStart.ORIGIN_POINT) {
            for (Player player : players) {
                safeTeleport(player, originPoint);
            }
        }
    }

    /**
     * Ticks each game loop: keeps players within the allowed radius.
     * Teleports across-world players back to origin, and handles out-of-bounds players.
     */
    public void tickLocationLimiter() {
        if (stopped) return;
        if (originPoint == null || originWorld == null) return;

        for (Player player : new ArrayList<>(players)) {
            if (player == null || !player.isOnline()) continue;

            // World correction
            if (!Objects.equals(player.getWorld(), originWorld)) {
                safeTeleport(player, originPoint);
                continue;
            }

            final double distance = player.getLocation().distance(originPoint);
            if (distance >= distanceExtents) {
                handleOutOfBoundsPlayer(player, distance);
            }
        }
    }

    /**
     * Handles a player who has exceeded the allowed distance.
     */
    private void handleOutOfBoundsPlayer(Player player, double distance) {
        if (edgeMessage != null && !edgeMessage.isEmpty()) {
            player.sendMessage(edgeMessage);
        }

        switch (locationLimiterType) {
            case SNAP_TO_ORIGIN:
                safeTeleport(player, originPoint);
                new LocationLimiterLimitHit(this, player);
                break;

            case PUSH_BACK:
                pushPlayerBack(player);
                new LocationLimiterLimitHit(this, player);
                break;
        }
    }

    /**
     * Pushes the player back toward the origin point.
     */
    private void pushPlayerBack(Player player) {
        Vector toOrigin = originPoint.toVector().subtract(player.getLocation().toVector()).normalize();
        player.setVelocity(toOrigin.multiply(0.5));
    }

    /**
     * Enable/disable limiter without removing it from registry.
     */
    public void toggleLocationLimiter(boolean state) {
        this.stopped = !state;
    }

    /**
     * Stop the limiter and remove from DreamCore registry.
     */
    public void stopLocationLimiter() {
        this.stopped = true;
        DreamCore.DreamLocationLimiters.remove(getClassID());
    }

    /**
     * Update message shown at the edge.
     */
    public void setEdgeMessage(String message) {
        this.edgeMessage = message == null ? "" : message;
    }

    /**
     * Update the distance extent (radius).
     */
    public void setDistanceExtents(int extents) {
        if (extents <= 0) throw new IllegalArgumentException("Distance extents must be > 0");
        this.distanceExtents = extents;
    }

    /**
     * Update limiter type at runtime.
     */
    public void setLocationLimiterType(LocationLimiterType type) {
        this.locationLimiterType = Objects.requireNonNull(type, "type");
    }

    /**
     * Update origin at runtime.
     */
    public void setOriginPoint(Location origin) {
        if (origin == null || origin.getWorld() == null)
            throw new IllegalArgumentException("Origin point/world cannot be null.");
        this.originPoint = origin;
        this.originWorld = origin.getWorld();
    }

    /* ---------------------------------------------------------------------
       Optional integration with a permissions/action API.
       If the API isn't present, allow the action by default.
       --------------------------------------------------------------------- */
    @SuppressWarnings("unchecked")
    private boolean canPlayerBeLimited(Player player) {
        try {
            Class<?> actionEnum = Class.forName("com.dreamfirestudios.dreamcore.DreamfirePlayerAction");
            Class<?> apiClass   = Class.forName("com.dreamfirestudios.dreamcore.DreamfirePlayerActionAPI");

            Object actionValue = Enum.valueOf((Class<Enum>) actionEnum, "PlayerLocationLimiter");
            Method m = apiClass.getMethod("CanPlayerAction", actionEnum, UUID.class);
            Object result = m.invoke(null, actionValue, player.getUniqueId());
            return result instanceof Boolean && (Boolean) result;
        } catch (Throwable ignored) {
            // API not found — allow by default
            return true;
        }
    }

    private void safeTeleport(Player player, Location target) {
        if (player == null || target == null || target.getWorld() == null) return;
        player.teleport(target);
    }

    /* =======================================================================
       Builder
       ======================================================================= */
    public static class LocationLimiterBuilder {
        private final List<Player> players = new ArrayList<>();
        private LocationLimiterType type = LocationLimiterType.SNAP_TO_ORIGIN;
        private LocationLimiterStart start = LocationLimiterStart.ORIGIN_POINT;
        private String edgeMessage = "You have reached the edge of the location!";
        private int extents = 10;

        public LocationLimiterBuilder addPlayer(Player player) {
            if (player != null) this.players.add(player);
            return this;
        }

        public LocationLimiterBuilder type(LocationLimiterType type) {
            this.type = Objects.requireNonNull(type, "type");
            return this;
        }

        public LocationLimiterBuilder start(LocationLimiterStart start) {
            this.start = Objects.requireNonNull(start, "start");
            return this;
        }

        public LocationLimiterBuilder edgeMessage(String message) {
            if (message != null && !message.isBlank()) this.edgeMessage = message;
            return this;
        }

        public LocationLimiterBuilder extents(int distance) {
            if (distance <= 0) throw new IllegalArgumentException("Distance extents must be > 0");
            this.extents = distance;
            return this;
        }

        /**
         * Build or fetch existing limiter from DreamCore registry.
         */
        public DreamLocationLimiter build(Location originPoint) {
            if (originPoint == null || originPoint.getWorld() == null) throw new IllegalArgumentException("Origin point/world cannot be null.");
            DreamLocationLimiter limiter = new DreamLocationLimiter();
            limiter.locationLimiterType = this.type;
            limiter.locationLimiterStart = this.start;
            limiter.edgeMessage = this.edgeMessage;
            limiter.distanceExtents = this.extents;
            limiter.originPoint = originPoint;
            limiter.originWorld = originPoint.getWorld();
            for (Player p : players) limiter.AddPlayer(p);
            return DreamCore.DreamLocationLimiters.put(limiter.getClassID(), limiter);
        }
    }
}