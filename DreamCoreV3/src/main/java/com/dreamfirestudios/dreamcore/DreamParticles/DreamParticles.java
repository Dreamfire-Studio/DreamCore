package com.dreamfirestudios.dreamcore.DreamParticles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Particle utilities with a generic shape system.
 *
 * <p>Key ideas:</p>
 * <ul>
 *   <li>Use {@code count} to control how many particles spawn per point.</li>
 *   <li>To persist an effect over time, call these methods on a repeating task.</li>
 *   <li>Shapes are produced by {@link ParticleShape} which returns relative {@link Vector} points.</li>
 * </ul>
 */
public final class DreamParticles {

    private DreamParticles() {}

    /* ======================================================================
     * Basic single-point emission
     * ====================================================================== */

    /** Spawns a single particle at the given absolute location (no data, no force). */
    public static void spawnSingle(World world, Particle particle, Location location) {
        if (world == null || particle == null || location == null || location.getWorld() == null) return;
        world.spawnParticle(particle, location, 1, 0, 0, 0, 0);
    }

    /** Spawns a single particle at the given absolute point (no data, no force). */
    public static void spawnSingle(World world, Particle particle, Vector point) {
        if (world == null || particle == null || point == null) return;
        world.spawnParticle(particle, point.getX(), point.getY(), point.getZ(), 1, 0, 0, 0, 0);
    }

    /**
     * Spawns particles with full control.
     *
     * @param world      world to emit in
     * @param particle   particle type
     * @param location   absolute location
     * @param count      particles per emission point (>=1)
     * @param offsetX    random X offset per particle
     * @param offsetY    random Y offset per particle
     * @param offsetZ    random Z offset per particle
     * @param speed      particle extra/speed parameter (varies by particle type)
     * @param data       optional particle data (e.g., {@link Particle.DustOptions}), may be null
     * @param force      if true, forces display regardless of distance or settings (Paper)
     */
    public static void spawn(
            World world, Particle particle, Location location,
            int count, double offsetX, double offsetY, double offsetZ, double speed,
            Object data, boolean force
    ) {
        if (world == null || particle == null || location == null || location.getWorld() == null) return;
        if (count <= 0) return;
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, data, force);
    }

    /* ======================================================================
     * Legacy-style simple wrappers (kept for convenience)
     * ====================================================================== */

    /** Convenience wrapper: simple emission at a location with offsets and speed (no data, not forced). */
    public static void spawn(World world, Particle particle, Location location,
                             int count, double offsetX, double offsetY, double offsetZ, double speed) {
        spawn(world, particle, location, count, offsetX, offsetY, offsetZ, speed, null, false);
    }

    /* ======================================================================
     * Generic Shape System
     * ====================================================================== */

    /**
     * A particle shape is a set of relative points (vectors) around an origin.
     * Implementations should return a stable collection for a single emission.
     */
    @FunctionalInterface
    public interface ParticleShape {
        /**
         * Sample a shape and return relative points to emit particles at.
         * The returned vectors are relative to an origin location (0,0,0 means origin).
         */
        Collection<Vector> sample();
    }

    /** Emits a sampled {@link ParticleShape} at an origin with full control. */
    public static void emitShape(
            World world, Particle particle, Location origin,
            ParticleShape shape,
            int count, double offsetX, double offsetY, double offsetZ, double speed,
            Object data, boolean force
    ) {
        if (world == null || particle == null || origin == null || origin.getWorld() == null) return;
        if (shape == null) return;
        if (count <= 0) return;

        for (Vector rel : shape.sample()) {
            Location at = origin.clone().add(rel);
            world.spawnParticle(particle, at, count, offsetX, offsetY, offsetZ, speed, data, force);
        }
    }

    /** Emits a shape with simple parameters (no data, not forced). */
    public static void emitShape(
            World world, Particle particle, Location origin, ParticleShape shape,
            int count, double offsetXYZ, double speed
    ) {
        emitShape(world, particle, origin, shape, count, offsetXYZ, offsetXYZ, offsetXYZ, speed, null, false);
    }

    /* ======================================================================
     * Ready-made shapes
     * ====================================================================== */

    /** Utility to clamp step counts. */
    private static int positive(int n, int fallback) {
        return (n > 0) ? n : fallback;
    }

    /** Returns a sphere shell shape (rings x segments points). */
    public static ParticleShape sphere(double radius, int rings, int segments) {
        final double R = Math.max(0.0, radius);
        final int RINGS = positive(rings, 12);
        final int SEGS  = positive(segments, 24);

        return () -> {
            List<Vector> pts = new ArrayList<>(RINGS * SEGS);
            for (int i = 0; i <= RINGS; i++) {
                double theta = Math.PI * i / RINGS; // 0..π
                double sin = Math.sin(theta);
                double cos = Math.cos(theta);
                double ringR = R * sin;
                double y = R * cos;
                for (int j = 0; j < SEGS; j++) {
                    double phi = (2 * Math.PI) * j / SEGS; // 0..2π
                    double x = Math.cos(phi) * ringR;
                    double z = Math.sin(phi) * ringR;
                    pts.add(new Vector(x, y, z));
                }
            }
            return pts;
        };
    }

    /**
     * Returns a flat ring in the XZ plane with given radius and segment count.
     * Use {@link #rotateAroundAxis(Vector, Vector, double)} to orient later if needed.
     */
    public static ParticleShape ring(double radius, int segments) {
        final double R = Math.max(0.0, radius);
        final int SEGS = positive(segments, 64);
        return () -> {
            List<Vector> pts = new ArrayList<>(SEGS);
            for (int j = 0; j < SEGS; j++) {
                double a = (2 * Math.PI) * j / SEGS;
                pts.add(new Vector(Math.cos(a) * R, 0, Math.sin(a) * R));
            }
            return pts;
        };
    }

    /**
     * Returns a cube shell of points (grid per face). If {@code stepsPerEdge} is N,
     * each edge will contain roughly N points.
     */
    public static ParticleShape cube(double sideLength, int stepsPerEdge) {
        final double L = Math.max(0.0, sideLength);
        final int STEPS = positive(stepsPerEdge, 8);
        final double half = L / 2.0;
        final double step = (STEPS <= 1 ? L : L / (STEPS - 1));

        return () -> {
            List<Vector> pts = new ArrayList<>(STEPS * STEPS * 6);
            // Six faces: +/-X, +/-Y, +/-Z
            for (int i = 0; i < STEPS; i++) {
                double t = -half + i * step;
                for (int j = 0; j < STEPS; j++) {
                    double u = -half + j * step;

                    // +X, -X
                    pts.add(new Vector( half, t, u));
                    pts.add(new Vector(-half, t, u));
                    // +Y, -Y
                    pts.add(new Vector(t,  half, u));
                    pts.add(new Vector(t, -half, u));
                    // +Z, -Z
                    pts.add(new Vector(t, u,  half));
                    pts.add(new Vector(t, u, -half));
                }
            }
            return pts;
        };
    }

    /**
     * Returns a cone shell aligned along +Y (origin at base center).
     * Use {@link #orientPoints(Collection, Vector)} to orient along an arbitrary axis.
     *
     * @param radius base radius
     * @param height height of cone
     * @param rings  number of horizontal rings (>=1)
     * @param segments points per ring (>=3)
     */
    public static ParticleShape cone(double radius, double height, int rings, int segments) {
        final double R = Math.max(0.0, radius);
        final double H = Math.max(0.0, height);
        final int RINGS = positive(rings, 12);
        final int SEGS  = Math.max(3, segments);

        return () -> {
            List<Vector> pts = new ArrayList<>(RINGS * SEGS);
            for (int i = 0; i <= RINGS; i++) {
                double y = (H * i) / RINGS;
                double ringR = R * (1.0 - (y / H)); // linear taper to apex
                for (int j = 0; j < SEGS; j++) {
                    double a = (2 * Math.PI) * j / SEGS;
                    double x = Math.cos(a) * ringR;
                    double z = Math.sin(a) * ringR;
                    pts.add(new Vector(x, y, z));
                }
            }
            return pts;
        };
    }

    /**
     * Returns a vertical spiral around Y axis from y=0 to y=height.
     *
     * @param radius spiral radius
     * @param height height climbed
     * @param turns  number of full turns
     * @param points total points along the spiral path
     */
    public static ParticleShape spiral(double radius, double height, double turns, int points) {
        final double R = Math.max(0.0, radius);
        final double H = Math.max(0.0, height);
        final double TURNS = Math.max(0.0, turns);
        final int PTS = positive(points, 256);

        return () -> {
            List<Vector> out = new ArrayList<>(PTS);
            double maxAngle = TURNS * 2.0 * Math.PI;
            for (int i = 0; i < PTS; i++) {
                double t = (double) i / (PTS - 1);
                double angle = maxAngle * t;
                double y = H * t;
                double x = Math.cos(angle) * R;
                double z = Math.sin(angle) * R;
                out.add(new Vector(x, y, z));
            }
            return out;
        };
    }

    /* ======================================================================
     * Orientation helpers
     * ====================================================================== */

    /**
     * Rotates a vector around a normalized axis by an angle (radians).
     * Uses Rodrigues' rotation formula.
     */
    public static Vector rotateAroundAxis(Vector v, Vector axisNormalized, double angle) {
        Objects.requireNonNull(v, "v");
        Objects.requireNonNull(axisNormalized, "axisNormalized");
        Vector k = axisNormalized.clone().normalize();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        // v_rot = v*cos + (k x v)*sin + k*(k·v)*(1-cos)
        Vector cross = k.clone().crossProduct(v).multiply(sin);
        Vector term1 = v.clone().multiply(cos);
        Vector term2 = cross;
        Vector term3 = k.clone().multiply(k.dot(v) * (1.0 - cos));
        return term1.add(term2).add(term3);
    }

    /**
     * Applies a rotation of {@code angle} radians around {@code axis} to each point.
     * Returns a new list (does not mutate the input collection).
     */
    public static List<Vector> rotatePoints(Collection<Vector> points, Vector axis, double angle) {
        Objects.requireNonNull(points, "points");
        Objects.requireNonNull(axis, "axis");
        List<Vector> out = new ArrayList<>(points.size());
        Vector n = axis.clone().normalize();
        for (Vector p : points) {
            out.add(rotateAroundAxis(p, n, angle));
        }
        return out;
    }

    /**
     * Orients points so that the +Y direction aligns to {@code axis} (best-effort).
     * This is a simple “look rotation” utility for shapes that default to +Y.
     */
    public static List<Vector> orientPoints(Collection<Vector> points, Vector axis) {
        Objects.requireNonNull(points, "points");
        Objects.requireNonNull(axis, "axis");
        Vector y = new Vector(0, 1, 0);
        Vector n = axis.clone().normalize();

        double dot = y.dot(n);
        // If vectors are already aligned:
        if (Math.abs(dot - 1.0) < 1e-6) return new ArrayList<>(points);       // same direction
        if (Math.abs(dot + 1.0) < 1e-6) {                                      // opposite: rotate 180° around any perpendicular axis
            Vector perp = new Vector(1, 0, 0);
            if (Math.abs(y.dot(perp)) > 0.99) perp = new Vector(0, 0, 1);
            return rotatePoints(points, perp, Math.PI);
        }

        // Rotation axis is cross product; angle is arccos(dot)
        Vector axisRot = y.clone().crossProduct(n).normalize();
        double angle = Math.acos(dot);
        return rotatePoints(points, axisRot, angle);
    }

    /* ======================================================================
     * Deprecated legacy API (kept to ease migration)
     * ====================================================================== */

    /** @deprecated Use {@link #spawnSingle(World, Particle, Location)} */
    @Deprecated
    public static void spawnParticle(World world, Particle particle, Location location) {
        spawnSingle(world, particle, location);
    }

    /** @deprecated Use {@link #spawnSingle(World, Particle, Vector)} */
    @Deprecated
    public static void spawnParticle(World world, Particle particle, Vector point) {
        spawnSingle(world, particle, point);
    }

    /** @deprecated Use {@link #spawn(World, Particle, Location, int, double, double, double, double)} */
    @Deprecated
    public static void spawnParticle(World world, Particle particle, Location location,
                                     int count, double offsetX, double offsetY, double offsetZ, double speed) {
        spawn(world, particle, location, count, offsetX, offsetY, offsetZ, speed);
    }
}