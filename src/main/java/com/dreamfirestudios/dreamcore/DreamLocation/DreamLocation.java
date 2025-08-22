package com.dreamfirestudios.dreamcore.DreamLocation;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/// <summary>
/// Location and vector helper utilities with safe null checks, world consistency checks,
/// and squared-distance usage where appropriate.
/// </summary>
/// <remarks>
/// Provides common math operations (closest, midpoint, points between, cube fill, etc.)
/// while ensuring <see cref="World"/> consistency and null safety.
/// Distances inside loops use <c>distanceSquared</c> where possible.
/// </remarks>
/// <example>
/// <code>
/// Location a = player.getLocation();
/// Location b = chest.getLocation();
/// Location mid = DreamLocation.midpoint(a, b);
/// </code>
/// </example>
public final class DreamLocation {

    private DreamLocation() {}

    // ---------------------------------------------------------------------
    // CLOSEST
    // ---------------------------------------------------------------------

    /// <summary>
    /// Returns the closest location to the provided origin from a list.
    /// </summary>
    /// <param name="locations">Candidate locations (must not be null/empty).</param>
    /// <param name="originLocation">Origin location (must not be null, must have a world).</param>
    /// <returns>
    /// Closest location in the same world, or <c>null</c> if none share a world.
    /// </returns>
    /// <remarks>Internally uses <see cref="Location#distanceSquared(Location)"/>.</remarks>
    /// <example>
    /// <code>
    /// Location nearest = DreamLocation.closestLocation(homes, player.getLocation());
    /// </code>
    /// </example>
    public static Location ReturnClosestLocation(List<Location> locations, Location originLocation) {
        return closestLocation(locations, originLocation);
    }

    public static Location closestLocation(List<Location> locations, Location originLocation) {
        if (locations == null || locations.isEmpty())
            throw new IllegalArgumentException("Locations list cannot be null or empty.");
        if (originLocation == null || originLocation.getWorld() == null)
            throw new IllegalArgumentException("Origin location/world cannot be null.");

        final World world = originLocation.getWorld();
        double best = Double.POSITIVE_INFINITY;
        Location bestLoc = null;

        for (Location candidate : locations) {
            if (candidate == null || candidate.getWorld() == null) continue;
            if (!world.equals(candidate.getWorld())) continue;

            double d2 = originLocation.distanceSquared(candidate);
            if (d2 < best) {
                best = d2;
                bestLoc = candidate;
                if (best == 0.0) break;
            }
        }
        return bestLoc;
    }

    // ---------------------------------------------------------------------
    // MIDPOINT
    // ---------------------------------------------------------------------

    /// <summary>
    /// Finds the midpoint between two locations (same world required).
    /// </summary>
    /// <param name="a">First location.</param>
    /// <param name="b">Second location.</param>
    /// <returns>
    /// Midpoint location, or <c>null</c> if the worlds differ.
    /// </returns>
    /// <example>
    /// <code>
    /// Location mid = DreamLocation.midpoint(home, spawn);
    /// </code>
    /// </example>
    public static Location FindMidPointBetween2Locations(Location a, Location b) {
        return midpoint(a, b);
    }

    public static Location midpoint(Location a, Location b) {
        if (a == null || b == null) throw new IllegalArgumentException("Both locations must not be null.");
        if (!Objects.equals(a.getWorld(), b.getWorld())) return null;
        return new Location(
                a.getWorld(),
                (a.getX() + b.getX()) * 0.5,
                (a.getY() + b.getY()) * 0.5,
                (a.getZ() + b.getZ()) * 0.5
        );
    }

    // ---------------------------------------------------------------------
    // BETWEEN
    // ---------------------------------------------------------------------

    /// <summary>
    /// Checks if a block lies within the axis-aligned box defined by two locations.
    /// </summary>
    /// <param name="location1">First corner.</param>
    /// <param name="location2">Opposite corner.</param>
    /// <param name="block">Block to test.</param>
    /// <returns>
    /// True if the block lies within the box and worlds match; false otherwise.
    /// </returns>
    /// <example>
    /// <code>
    /// if (DreamLocation.isBlockBetween(a, b, block)) {
    ///     player.sendMessage("Inside region!");
    /// }
    /// </code>
    /// </example>
    public static boolean IsBlockBetweenLocations(Location location1, Location location2, Block block) {
        return isBlockBetween(location1, location2, block);
    }

    public static boolean isBlockBetween(Location location1, Location location2, Block block) {
        if (location1 == null || location2 == null || block == null)
            throw new IllegalArgumentException("Locations and block must not be null.");
        if (location1.getWorld() == null || location2.getWorld() == null || block.getWorld() == null)
            return false;
        if (!location1.getWorld().equals(location2.getWorld())) return false;
        if (!block.getWorld().equals(location1.getWorld())) return false;

        int minX = Math.min(location1.getBlockX(), location2.getBlockX());
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        int minY = Math.min(location1.getBlockY(), location2.getBlockY());
        int maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

        int bx = block.getX(), by = block.getY(), bz = block.getZ();
        return bx >= minX && bx <= maxX
                && by >= minY && by <= maxY
                && bz >= minZ && bz <= maxZ;
    }

    // ---------------------------------------------------------------------
    // POINTS BETWEEN TWO LOCATIONS
    // ---------------------------------------------------------------------

    /// <summary>
    /// Returns evenly spaced points (including endpoints) between two locations.
    /// </summary>
    /// <param name="start">Start location.</param>
    /// <param name="end">End location.</param>
    /// <param name="spacing">Desired spacing in blocks (&gt;0).</param>
    /// <returns>Array of locations from start to end inclusive.</returns>
    /// <remarks>
    /// If spacing is larger than distance, returns just {start, end}.
    /// </remarks>
    /// <example>
    /// <code>
    /// Location[] trail = DreamLocation.pointsBetween(a, b, 1.0);
    /// </code>
    /// </example>
    public static Location[] ReturnAllLocationsBetweenTwoLocations(Location start, Location end, double spacing) {
        return pointsBetween(start, end, spacing);
    }

    public static Location[] pointsBetween(Location start, Location end, double spacing) {
        if (start == null || end == null) throw new IllegalArgumentException("Start/end must not be null.");
        if (start.getWorld() == null || end.getWorld() == null)
            throw new IllegalArgumentException("Start/end worlds must not be null.");
        if (!start.getWorld().equals(end.getWorld()))
            throw new IllegalArgumentException("Start/end must be in the same world.");
        if (spacing <= 0) throw new IllegalArgumentException("Spacing must be greater than zero.");

        double distance = start.distance(end);
        if (distance == 0.0) {
            return new Location[]{start.clone()};
        }

        int segments = Math.max(1, (int) Math.floor(distance / spacing));
        int points = segments + 1;

        Vector step = end.toVector().subtract(start.toVector()).multiply(1.0 / segments);

        Location[] out = new Location[points];
        for (int i = 0; i < points; i++) {
            Vector v = start.toVector().add(step.clone().multiply(i));
            out[i] = new Location(start.getWorld(), v.getX(), v.getY(), v.getZ());
        }
        return out;
    }

    // ---------------------------------------------------------------------
    // CUBE FILL
    // ---------------------------------------------------------------------

    /// <summary>
    /// Returns all block locations inside the axis-aligned box defined by two points.
    /// </summary>
    /// <param name="a">First corner.</param>
    /// <param name="b">Opposite corner.</param>
    /// <returns>List of all block positions (inclusive).</returns>
    /// <example>
    /// <code>
    /// List&lt;Location&gt; cube = DreamLocation.cubeLocations(a, b);
    /// </code>
    /// </example>
    public static List<Location> GetAllLocationsInCubeArea(Location a, Location b) {
        return cubeLocations(a, b);
    }

    public static List<Location> cubeLocations(Location a, Location b) {
        if (a == null || b == null) throw new IllegalArgumentException("Locations must not be null.");
        if (a.getWorld() == null || b.getWorld() == null)
            throw new IllegalArgumentException("Worlds must not be null.");
        if (!a.getWorld().equals(b.getWorld()))
            throw new IllegalArgumentException("Locations must be in the same world.");

        List<Location> locations = new ArrayList<>();

        int minX = Math.min(a.getBlockX(), b.getBlockX());
        int minY = Math.min(a.getBlockY(), b.getBlockY());
        int minZ = Math.min(a.getBlockZ(), b.getBlockZ());

        int maxX = Math.max(a.getBlockX(), b.getBlockX());
        int maxY = Math.max(a.getBlockY(), b.getBlockY());
        int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());

        World w = a.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    locations.add(new Location(w, x, y, z));
                }
            }
        }
        return locations;
    }

    // ---------------------------------------------------------------------
    // TOTAL DISTANCE
    // ---------------------------------------------------------------------

    /// <summary>
    /// Calculates the total path length through provided locations in order.
    /// </summary>
    /// <param name="locations">Locations (≥2, all consecutive in same world).</param>
    /// <returns>Total path length.</returns>
    /// <example>
    /// <code>
    /// double d = DreamLocation.totalDistance(a, b, c);
    /// </code>
    /// </example>
    public static double TotalDistance(Location... locations) {
        return totalDistance(locations);
    }

    public static double totalDistance(Location... locations) {
        if (locations == null || locations.length < 2)
            throw new IllegalArgumentException("At least two locations must be provided.");
        double total = 0.0;
        for (int i = 0; i < locations.length - 1; i++) {
            Location a = Objects.requireNonNull(locations[i], "Location[" + i + "] is null");
            Location b = Objects.requireNonNull(locations[i + 1], "Location[" + (i + 1) + "] is null");
            if (!Objects.equals(a.getWorld(), b.getWorld()))
                throw new IllegalArgumentException("All consecutive locations must be in the same world.");
            total += a.distance(b);
        }
        return total;
    }

    // ---------------------------------------------------------------------
    // DIRECTIONAL POSITION
    // ---------------------------------------------------------------------

    /// <summary>
    /// Computes a location offset from start by direction.normalized * distance.
    /// </summary>
    /// <param name="start">Start location.</param>
    /// <param name="direction">Direction vector.</param>
    /// <param name="distance">Distance in blocks.</param>
    /// <returns>Offset location in same world.</returns>
    /// <example>
    /// <code>
    /// Location ahead = DreamLocation.translate(player.getLocation(), player.getLocation().getDirection(), 5);
    /// </code>
    /// </example>
    public static Location GetLocationInDirection(Location start, Vector direction, double distance) {
        return translate(start, direction, distance);
    }

    public static Location translate(Location start, Vector direction, double distance) {
        if (start == null || direction == null)
            throw new IllegalArgumentException("Start location and direction must not be null.");
        Vector newPos = start.toVector().add(direction.clone().normalize().multiply(distance));
        return newPos.toLocation(start.getWorld());
    }

    // ---------------------------------------------------------------------
    // ANGLES
    // ---------------------------------------------------------------------

    /// <summary>
    /// Calculates angle (radians) of vector a-&gt;b around a given axis.
    /// </summary>
    /// <param name="a">Start location.</param>
    /// <param name="b">End location.</param>
    /// <param name="axis">One of "X","Y","Z" (case-insensitive).</param>
    /// <returns>Angle in radians.</returns>
    /// <remarks>
    /// Axis meanings:
    /// - X: angle in YZ-plane → atan2(dY,dZ)
    /// - Y: angle in XZ-plane → atan2(dX,dZ)
    /// - Z: angle in XY-plane → atan2(dY,dX)
    /// </remarks>
    /// <example>
    /// <code>
    /// double yaw = DreamLocation.axisAngle(a, b, "Y");
    /// </code>
    /// </example>
    public static double CalculateAngleBetweenLocations(Location a, Location b, String axis) {
        return axisAngle(a, b, axis);
    }

    public static double axisAngle(Location a, Location b, String axis) {
        if (a == null || b == null) throw new IllegalArgumentException("Locations must not be null.");
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double dz = b.getZ() - a.getZ();

        String ax = Objects.requireNonNull(axis, "axis").trim().toUpperCase(Locale.ROOT);
        switch (ax) {
            case "X": return Math.atan2(dy, dz);
            case "Y": return Math.atan2(dx, dz);
            case "Z": return Math.atan2(dy, dx);
            default: throw new IllegalArgumentException("Invalid axis: " + axis + " (expected X, Y, or Z)");
        }
    }
}