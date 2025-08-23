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

/// <summary>
/// Runtime limiter that constrains players to a defined radius around an origin point.
/// </summary>
/// <remarks>
/// Supports two modes:
/// <list type="bullet">
///   <item><description><see cref="LocationLimiterType.SNAP_TO_ORIGIN"/>: teleport the player back.</description></item>
///   <item><description><see cref="LocationLimiterType.PUSH_BACK"/>: push the player back with velocity.</description></item>
/// </list>
/// Limiters can be toggled, started, stopped, and customized with messages.
/// </remarks>
/// <example>
/// <code>
/// DreamLocationLimiter limiter = new DreamLocationLimiter.LocationLimiterBuilder()
///     .type(LocationLimiterType.PUSH_BACK)
///     .extents(20)
///     .edgeMessage("Stay inside the arena!")
///     .build(arenaCenter);
/// limiter.startLocationLimiter();
/// </code>
/// </example>
public class DreamLocationLimiter extends DreamClassID {

    private final List<Player> players = new ArrayList<>();

    @Getter private LocationLimiterType locationLimiterType = LocationLimiterType.SNAP_TO_ORIGIN;
    @Getter private LocationLimiterStart locationLimiterStart = LocationLimiterStart.ORIGIN_POINT;
    @Getter private String edgeMessage = "You have reached the edge of the location!";
    @Getter private Location originPoint;
    @Getter private int distanceExtents = 10;
    @Getter private boolean stopped = false;

    private World originWorld;

    /// <summary>
    /// Adds a player to this limiter.
    /// </summary>
    /// <param name="player">Player to add.</param>
    /// <remarks>
    /// Fires cancellable <see cref="LocationLimiterPlayerAddedEvent"/> before adding.
    /// Skips if the player cannot be limited by external APIs.
    /// </remarks>
    /// <example>
    /// <code>
    /// limiter.AddPlayer(player);
    /// </code>
    /// </example>
    public void AddPlayer(Player player) {
        if (player == null) return;
        if (players.contains(player)) return;

        LocationLimiterPlayerAddedEvent evt = new LocationLimiterPlayerAddedEvent(this, player);
        if (evt.isCancelled()) return;

        if (!canPlayerBeLimited(player)) return;

        players.add(player);
    }

    /// <summary>
    /// Removes a player from this limiter.
    /// </summary>
    /// <param name="player">Player to remove.</param>
    /// <remarks>
    /// Fires <see cref="LocationLimiterPlayerRemovedEvent"/> after removal.
    /// </remarks>
    public void RemovePlayer(Player player) {
        if (player == null) return;
        if (!players.remove(player)) return;
        new LocationLimiterPlayerRemovedEvent(this, player);
    }

    /// <summary>
    /// Starts the limiter and teleports players to origin if configured.
    /// </summary>
    /// <exception cref="IllegalStateException">If origin/world are unset.</exception>
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

    /// <summary>
    /// Main tick handler that enforces the limiter each loop.
    /// </summary>
    /// <remarks>
    /// - Teleports cross-world players back to origin.
    /// - Handles players exceeding radius via <c>handleOutOfBoundsPlayer</c>.
    /// </remarks>
    public void tickLocationLimiter() {
        if (stopped) return;
        if (originPoint == null || originWorld == null) return;

        for (Player player : new ArrayList<>(players)) {
            if (player == null || !player.isOnline()) continue;

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

    /// <summary>
    /// Handles a player who exceeds the limiter boundary.
    /// </summary>
    /// <param name="player">Player out of bounds.</param>
    /// <param name="distance">Distance from origin.</param>
    /// <remarks>
    /// Sends <see cref="edgeMessage"/> and triggers a <see cref="LocationLimiterLimitHit"/> event.
    /// </remarks>
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

    /// <summary>
    /// Pushes a player back toward the origin.
    /// </summary>
    /// <param name="player">Player to push.</param>
    private void pushPlayerBack(Player player) {
        Vector toOrigin = originPoint.toVector().subtract(player.getLocation().toVector()).normalize();
        player.setVelocity(toOrigin.multiply(0.5));
    }

    /// <summary>
    /// Enables or disables limiter enforcement.
    /// </summary>
    /// <param name="state"><c>true</c> to enable; <c>false</c> to pause.</param>
    public void toggleLocationLimiter(boolean state) {
        this.stopped = !state;
    }

    /// <summary>
    /// Stops this limiter and unregisters it from <see cref="DreamCore"/>.
    /// </summary>
    public void stopLocationLimiter() {
        this.stopped = true;
        DreamCore.DreamLocationLimiters.remove(getClassID());
    }

    /// <summary>
    /// Updates the edge message.
    /// </summary>
    /// <param name="message">New message (null → empty).</param>
    public void setEdgeMessage(String message) {
        this.edgeMessage = message == null ? "" : message;
    }

    /// <summary>
    /// Updates the distance extent (radius).
    /// </summary>
    /// <param name="extents">New radius (&gt; 0).</param>
    public void setDistanceExtents(int extents) {
        if (extents <= 0) throw new IllegalArgumentException("Distance extents must be > 0");
        this.distanceExtents = extents;
    }

    /// <summary>
    /// Updates the limiter type.
    /// </summary>
    /// <param name="type">New limiter type.</param>
    public void setLocationLimiterType(LocationLimiterType type) {
        this.locationLimiterType = Objects.requireNonNull(type, "type");
    }

    /// <summary>
    /// Updates the origin point.
    /// </summary>
    /// <param name="origin">New origin (must have world).</param>
    public void setOriginPoint(Location origin) {
        if (origin == null || origin.getWorld() == null)
            throw new IllegalArgumentException("Origin point/world cannot be null.");
        this.originPoint = origin;
        this.originWorld = origin.getWorld();
    }

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
            return true; // default allow
        }
    }

    private void safeTeleport(Player player, Location target) {
        if (player == null || target == null || target.getWorld() == null) return;
        player.teleport(target);
    }

    // ---------------------------------------------------------------------
    // Builder
    // ---------------------------------------------------------------------

    /// <summary>
    /// Fluent builder for <see cref="DreamLocationLimiter"/>.
    /// </summary>
    /// <remarks>
    /// Lets you configure type, start mode, edge message, radius, and players before creating the limiter.
    /// </remarks>
    /// <example>
    /// <code>
    /// DreamLocationLimiter limiter = new DreamLocationLimiter.LocationLimiterBuilder()
    ///     .extents(15)
    ///     .type(LocationLimiterType.SNAP_TO_ORIGIN)
    ///     .addPlayer(player)
    ///     .build(world.getSpawnLocation());
    /// </code>
    /// </example>
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

        /// <summary>
        /// Builds a new limiter and registers it with <see cref="DreamCore"/>.
        /// </summary>
        /// <param name="originPoint">Origin point (must have world).</param>
        /// <returns>Created <see cref="DreamLocationLimiter"/>.</returns>
        public DreamLocationLimiter build(Location originPoint) {
            if (originPoint == null || originPoint.getWorld() == null)
                throw new IllegalArgumentException("Origin point/world cannot be null.");
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