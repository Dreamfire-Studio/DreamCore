package com.dreamfirestudios.dreamCore.DreamfireLocation;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DreamfireLocation {

    /**
     * Returns the closest location to the provided originLocation from a list of locations.
     *
     * @param locations A list of locations to check against.
     * @param originLocation The reference location to compare distances.
     * @return The closest Location from the list to the originLocation.
     * @throws IllegalArgumentException if the locations list is null or empty.
     */
    public static Location ReturnClosestLocation(List<Location> locations, Location originLocation){
        if (locations == null || locations.isEmpty()) throw new IllegalArgumentException("Locations list cannot be null or empty.");
        var distance = Double.POSITIVE_INFINITY;
        Location location = null;
        for(var s : locations){
            if(originLocation.distance(s) < distance){
                distance = originLocation.distance(s);
                location = s;
                if(distance == 0) break;
            }
        }
        return location;
    }

    /**
     * Finds the midpoint between two locations.
     *
     * @param a The first location.
     * @param b The second location.
     * @return The midpoint location between a and b, or null if the locations are in different worlds.
     * @throws IllegalArgumentException if either of the locations is null.
     */
    public static Location FindMidPointBetween2Locations(Location a, Location b){
        if (a == null || b == null) throw new IllegalArgumentException("Both locations must not be null.");
        if(a.getWorld() != b.getWorld()) return null;
        var newX = (a.getX() + b.getX()) / 2;
        var newY = (a.getY() + b.getY()) / 2;
        var newZ = (a.getZ() + b.getZ()) / 2;
        return new Location(a.getWorld(), newX, newY, newZ);
    }

    /**
     * Checks if a block is between two locations.
     *
     * @param location1 The first location.
     * @param location2 The second location.
     * @param block The block to check.
     * @return True if the block lies between the two locations; false otherwise.
     * @throws IllegalArgumentException if any of the locations or the block is null.
     */
    public static boolean IsBlockBetweenLocations(Location location1, Location location2, Block block) {
        if (location1 == null || location2 == null || block == null) throw new IllegalArgumentException("Locations and block must not be null.");
        var world = location1.getWorld();
        var minX = Math.min(location1.getBlockX(), location2.getBlockX());
        var maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        var minY = Math.min(location1.getBlockY(), location2.getBlockY());
        var maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        var minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        var maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
        return block.getWorld().equals(world) && block.getX() >= minX && block.getX() <= maxX &&
                block.getY() >= minY && block.getY() <= maxY && block.getZ() >= minZ && block.getZ() <= maxZ;
    }

    /**
     * Returns all locations between two locations at a specified spacing.
     *
     * @param start The starting location.
     * @param end The ending location.
     * @param spacing The distance between each point.
     * @return An array of locations between the two given locations.
     * @throws IllegalArgumentException if spacing is less than or equal to zero.
     */
    public Location[] ReturnAllLocationsBetweenTwoLocations(Location start, Location end, double spacing){
        if (spacing <= 0)  throw new IllegalArgumentException("Spacing must be greater than zero.");

        var startVec = start.toVector();
        var endVec = end.toVector();
        var direction = endVec.clone().subtract(startVec).normalize();

        var distance = start.distance(end);
        var numberOfPoints = (int) Math.ceil(distance / spacing);

        var locations = new Location[numberOfPoints];
        for (var i = 0; i < numberOfPoints; i++) {
            Vector pointVec = startVec.clone().add(direction.clone().multiply(spacing * i));
            locations[i] = pointVec.toLocation(start.getWorld());
        }
        return locations;
    }

    /**
     * Returns a list of all locations within a cubic area defined by two locations.
     *
     * @param a The first location.
     * @param b The second location.
     * @return A list of all locations within the cubic area.
     * @throws IllegalArgumentException if either of the locations is null.
     */
    public static List<Location> GetAllLocationsInCubeArea(Location a, Location b) {
        if (a == null || b == null) throw new IllegalArgumentException("Locations must not be null.");

        List<Location> locations = new ArrayList<>();

        int minX = Math.min(a.getBlockX(), b.getBlockX());
        int minY = Math.min(a.getBlockY(), b.getBlockY());
        int minZ = Math.min(a.getBlockZ(), b.getBlockZ());

        int maxX = Math.max(a.getBlockX(), b.getBlockX());
        int maxY = Math.max(a.getBlockY(), b.getBlockY());
        int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    locations.add(new Location(a.getWorld(), x, y, z));
                }
            }
        }

        return locations;
    }

    /**
     * Calculates the total distance between a series of locations.
     *
     * @param locations An array of locations to calculate the distance between.
     * @return The total distance.
     * @throws IllegalArgumentException if locations array is null or empty.
     */
    public static double TotalDistance(Location... locations){
        if (locations == null || locations.length < 2) throw new IllegalArgumentException("At least two locations must be provided.");
        var total = 0.0;
        for(var i = 0; i < locations.length - 1; i++) total += locations[i].distance(locations[i + 1]);
        return total;
    }

    public static Location GetLocationInDirection(Location start, Vector direction, double distance) {
        if (start == null || direction == null) throw new IllegalArgumentException("Start location and direction must not be null.");
        Vector newPosition = start.toVector().add(direction.normalize().multiply(distance));
        return newPosition.toLocation(start.getWorld());
    }

    public static double CalculateAngleBetweenLocations(Location a, Location b, String axis) {
        if (a == null || b == null) throw new IllegalArgumentException("Locations must not be null.");
        double deltaX = b.getX() - a.getX();
        double deltaY = b.getY() - a.getY();
        double deltaZ = b.getZ() - a.getZ();
        switch(axis.toUpperCase()) {
            case "X": return Math.atan2(deltaZ, deltaY); // Angle in X-Y plane
            case "Y": return Math.atan2(deltaX, deltaZ); // Angle in X-Z plane
            case "Z": return Math.atan2(deltaY, deltaX); // Angle in Y-Z plane
            default: throw new IllegalArgumentException("Invalid axis provided.");
        }
    }
}
