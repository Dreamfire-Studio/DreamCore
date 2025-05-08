package com.dreamfirestudios.dreamCore.DreamfireParticles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class DreamfireParticle {
    /**
     * Spawns a single particle at the given Location.
     */
    public static void spawnParticle(World world, Particle particle, Location location) {
        spawnParticle(world, particle, location.toVector());
    }

    /**
     * Spawns a single particle at the given Vector point.
     */
    public static void spawnParticle(World world, Particle particle, Vector point) {
        world.spawnParticle(particle, point.getX(), point.getY(), point.getZ(), 1);
    }

    /**
     * Spawns a range of particles at the given location with more control over parameters.
     */
    public static void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
    }

    /**
     * Spawns a sphere of particles centered at a Location.
     */
    public static void spawnSphere(Location location, Particle particle, int density, int duration, double radius) {
        if (location.getWorld() == null) return;

        for (double i = 0; i <= Math.PI; i += Math.PI / density) {
            double r = radius * Math.sin(i);  // Radius in the xz-plane
            double y = radius * Math.cos(i);  // Y offset

            for (double a = 0; a < Math.PI * 2; a += Math.PI / density) {
                double x = Math.cos(a) * r;
                double z = Math.sin(a) * r;

                Location particleLocation = location.clone().add(x, y, z);
                location.getWorld().spawnParticle(particle, particleLocation, duration, 0F, 0F, 0F, 0.001);
            }
        }
    }

    /**
     * Spawns a cube of particles centered at a Location.
     */
    public static void spawnCube(Location location, Particle particle, int density, int duration, double sideLength) {
        if (location.getWorld() == null) return;

        double halfSide = sideLength / 2.0;
        for (double x = -halfSide; x <= halfSide; x += sideLength / density) {
            for (double y = -halfSide; y <= halfSide; y += sideLength / density) {
                for (double z = -halfSide; z <= halfSide; z += sideLength / density) {
                    Location particleLocation = location.clone().add(x, y, z);
                    location.getWorld().spawnParticle(particle, particleLocation, duration, 0F, 0F, 0F, 0.001);
                }
            }
        }
    }

    /**
     * Spawns a ring of particles around a Location.
     */
    public static void spawnRing(Location location, Particle particle, int density, int duration, double radius) {
        if (location.getWorld() == null) return;

        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / density) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location particleLocation = location.clone().add(x, 0, z);
            location.getWorld().spawnParticle(particle, particleLocation, duration, 0F, 0F, 0F, 0.001);
        }
    }

    /**
     * Spawns a cone of particles originating from a Location.
     */
    public static void spawnCone(Location location, Particle particle, int density, int duration, double radius, double height) {
        if (location.getWorld() == null) return;

        for (double y = 0; y <= height; y += height / density) {
            double currentRadius = radius * (1 - (y / height));  // Radius decreases as you go up
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / density) {
                double x = Math.cos(angle) * currentRadius;
                double z = Math.sin(angle) * currentRadius;

                Location particleLocation = location.clone().add(x, y, z);
                location.getWorld().spawnParticle(particle, particleLocation, duration, 0F, 0F, 0F, 0.001);
            }
        }
    }

    /**
     * Spawns a spiral of particles originating from a Location.
     */
    public static void spawnSpiral(Location location, Particle particle, int density, int duration, double radius, double height, double turns) {
        if (location.getWorld() == null) return;

        for (double i = 0; i < turns * Math.PI * 2; i += Math.PI / density) {
            double angle = i;
            double y = height * (i / (turns * Math.PI * 2));  // The y-value increases from 0 to the specified height
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location particleLocation = location.clone().add(x, y, z);
            location.getWorld().spawnParticle(particle, particleLocation, duration, 0F, 0F, 0F, 0.001);
        }
    }
}
