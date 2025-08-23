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
package com.dreamfirestudios.dreamcore.DreamParticles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/// <summary>
/// Particle utilities with a generic, reusable shape system.
/// </summary>
/// <remarks>
/// Provides:
/// <list type="bullet">
///   <item><description>Single-point emission helpers</description></item>
///   <item><description>Parametric shapes (sphere, ring, cube, cone, spiral)</description></item>
///   <item><description>Orientation helpers (rotate/orient)</description></item>
/// </list>
/// The shape API returns relative points around an origin, enabling composition
/// and re-use across effects.
/// </remarks>
public final class DreamParticles {

    private DreamParticles() {}

    /* ======================================================================
     * Basic single-point emission
     * ====================================================================== */

    /// <summary>
    /// Spawns a single particle at an absolute location (no data, not forced).
    /// </summary>
    /// <param name="world">Target world.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="location">Absolute location (world must be non-null).</param>
    /// <example>
    /// <code>
    /// DreamParticles.spawnSingle(world, Particle.FLAME, player.getLocation());
    /// </code>
    /// </example>
    public static void spawnSingle(World world, Particle particle, Location location) {
        if (world == null || particle == null || location == null || location.getWorld() == null) return;
        world.spawnParticle(particle, location, 1, 0, 0, 0, 0);
    }

    /// <summary>
    /// Spawns a single particle at an absolute point (no data, not forced).
    /// </summary>
    /// <param name="world">Target world.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="point">Absolute vector position.</param>
    public static void spawnSingle(World world, Particle particle, Vector point) {
        if (world == null || particle == null || point == null) return;
        world.spawnParticle(particle, point.getX(), point.getY(), point.getZ(), 1, 0, 0, 0, 0);
    }

    /// <summary>
    /// Spawns particles with full control of count, offsets, speed, data, and force.
    /// </summary>
    /// <param name="world">Target world.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="location">Origin location.</param>
    /// <param name="count">Particles per emission point (≥1).</param>
    /// <param name="offsetX">Random X offset per particle.</param>
    /// <param name="offsetY">Random Y offset per particle.</param>
    /// <param name="offsetZ">Random Z offset per particle.</param>
    /// <param name="speed">“Extra”/speed parameter (varies per particle).</param>
    /// <param name="data">Optional particle data (e.g., <c>Particle.DustOptions</c>), may be null.</param>
    /// <param name="force">If true, forces display regardless of distance/settings (Paper).</param>
    /// <example>
    /// <code>
    /// DreamParticles.spawn(world, Particle.CRIT, loc, 8, 0.1, 0.1, 0.1, 0.01, null, false);
    /// </code>
    /// </example>
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

    /// <summary>
    /// Convenience wrapper for simple emission (no data, not forced).
    /// </summary>
    /// <param name="world">Target world.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="location">Origin location.</param>
    /// <param name="count">Particles per emission point.</param>
    /// <param name="offsetX">Random X offset per particle.</param>
    /// <param name="offsetY">Random Y offset per particle.</param>
    /// <param name="offsetZ">Random Z offset per particle.</param>
    /// <param name="speed">Extra speed parameter.</param>
    public static void spawn(World world, Particle particle, Location location,
                             int count, double offsetX, double offsetY, double offsetZ, double speed) {
        spawn(world, particle, location, count, offsetX, offsetY, offsetZ, speed, null, false);
    }

    /* ======================================================================
     * Generic Shape System
     * ====================================================================== */

    /// <summary>
    /// Represents a collection of relative emission points (vectors) around an origin.
    /// </summary>
    /// <remarks>
    /// Implementations should return a deterministic collection for a single sample,
    /// so repeated emissions are visually stable unless randomized explicitly.
    /// </remarks>
    @FunctionalInterface
    public interface ParticleShape {
        /// <summary>
        /// Samples the shape and returns relative points to the origin.
        /// </summary>
        /// <returns>Collection of relative <see cref="Vector"/> points.</returns>
        Collection<Vector> sample();
    }

    /// <summary>
    /// Emits a sampled <see cref="ParticleShape"/> at a given origin with full control.
    /// </summary>
    /// <param name="world">Target world.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="origin">Origin location (world must be non-null).</param>
    /// <param name="shape">Shape provider.</param>
    /// <param name="count">Particles per point.</param>
    /// <param name="offsetX">Random X offset.</param>
    /// <param name="offsetY">Random Y offset.</param>
    /// <param name="offsetZ">Random Z offset.</param>
    /// <param name="speed">Extra speed parameter.</param>
    /// <param name="data">Optional particle data.</param>
    /// <param name="force">If true, forces display.</param>
    /// <example>
    /// <code>
    /// DreamParticles.emitShape(world, Particle.ENCHANTMENT_TABLE, loc,
    ///     DreamParticles.sphere(1.5, 10, 20), 1, 0.0, 0.0, 0.0, 0.0, null, false);
    /// </code>
    /// </example>
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

    /// <summary>
    /// Emits a shape with simple parameters (no data, not forced).
    /// </summary>
    /// <param name="world">Target world.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="origin">Origin location.</param>
    /// <param name="shape">Shape provider.</param>
    /// <param name="count">Particles per point.</param>
    /// <param name="offsetXYZ">Uniform random offset applied to X/Y/Z.</param>
    /// <param name="speed">Extra speed parameter.</param>
    public static void emitShape(
            World world, Particle particle, Location origin, ParticleShape shape,
            int count, double offsetXYZ, double speed
    ) {
        emitShape(world, particle, origin, shape, count, offsetXYZ, offsetXYZ, offsetXYZ, speed, null, false);
    }

    /* ======================================================================
     * Ready-made shapes
     * ====================================================================== */

    /// <summary>
    /// Clamps positive integers with a fallback value.
    /// </summary>
    /// <param name="n">Value to test.</param>
    /// <param name="fallback">Value used when <paramref name="n"/> ≤ 0.</param>
    /// <returns>Positive integer.</returns>
    private static int positive(int n, int fallback) {
        return (n > 0) ? n : fallback;
    }

    /// <summary>
    /// Sphere shell distribution (rings × segments).
    /// </summary>
    /// <param name="radius">Sphere radius (≥0).</param>
    /// <param name="rings">Number of latitudinal rings (≥1 recommended).</param>
    /// <param name="segments">Points per ring (≥3 recommended).</param>
    /// <returns>A <see cref="ParticleShape"/> producing a sphere shell.</returns>
    /// <example>
    /// <code>
    /// ParticleShape shape = DreamParticles.sphere(2.0, 12, 32);
    /// DreamParticles.emitShape(world, Particle.SOUL_FIRE_FLAME, center, shape, 1, 0, 0, 0, 0, null, false);
    /// </code>
    /// </example>
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

    /// <summary>
    /// Flat ring in the XZ plane centered at origin.
    /// </summary>
    /// <param name="radius">Ring radius (≥0).</param>
    /// <param name="segments">Points along the ring (≥3 recommended).</param>
    /// <returns>Ring shape in XZ plane.</returns>
    /// <remarks>
    /// Use <see cref="rotateAroundAxis(Vector, Vector, double)"/> or <see cref="orientPoints(Collection, Vector)"/>
    /// to re-orient after sampling.
    /// </remarks>
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

    /// <summary>
    /// Cube shell (grid points per face).
    /// </summary>
    /// <param name="sideLength">Total side length (≥0).</param>
    /// <param name="stepsPerEdge">Grid density per edge (≥1).</param>
    /// <returns>Cube shell shape.</returns>
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

    /// <summary>
    /// Cone shell aligned along +Y (origin at base center).
    /// </summary>
    /// <param name="radius">Base radius (≥0).</param>
    /// <param name="height">Height (≥0).</param>
    /// <param name="rings">Horizontal ring count (≥1 recommended).</param>
    /// <param name="segments">Points per ring (≥3).</param>
    /// <returns>Cone shell shape.</returns>
    /// <remarks>
    /// To orient the cone along an arbitrary axis, use <see cref="orientPoints(Collection, Vector)"/>.
    /// </remarks>
    public static ParticleShape cone(double radius, double height, int rings, int segments) {
        final double R = Math.max(0.0, radius);
        final double H = Math.max(0.0, height);
        final int RINGS = positive(rings, 12);
        final int SEGS  = Math.max(3, segments);

        return () -> {
            List<Vector> pts = new ArrayList<>(RINGS * SEGS);
            for (int i = 0; i <= RINGS; i++) {
                double y = (H * i) / RINGS;
                double ringR = (H == 0.0) ? 0.0 : R * (1.0 - (y / H)); // linear taper
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

    /// <summary>
    /// Vertical spiral around +Y axis from y=0 to <paramref name="height"/>.
    /// </summary>
    /// <param name="radius">Spiral radius (≥0).</param>
    /// <param name="height">Total height climbed (≥0).</param>
    /// <param name="turns">Number of full turns (≥0).</param>
    /// <param name="points">Total points along the path (≥2 recommended).</param>
    /// <returns>Spiral shape.</returns>
    public static ParticleShape spiral(double radius, double height, double turns, int points) {
        final double R = Math.max(0.0, radius);
        final double H = Math.max(0.0, height);
        final double TURNS = Math.max(0.0, turns);
        final int PTS = positive(points, 256);

        return () -> {
            List<Vector> out = new ArrayList<>(PTS);
            double maxAngle = TURNS * 2.0 * Math.PI;
            for (int i = 0; i < PTS; i++) {
                double t = (PTS == 1) ? 0.0 : (double) i / (PTS - 1);
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

    /// <summary>
    /// Rotates a vector around a normalized axis by an angle (radians) using Rodrigues’ formula.
    /// </summary>
    /// <param name="v">Vector to rotate.</param>
    /// <param name="axisNormalized">Rotation axis (will be normalized defensively).</param>
    /// <param name="angle">Rotation angle in radians.</param>
    /// <returns>New rotated vector.</returns>
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

    /// <summary>
    /// Rotates each point in a collection around an axis by an angle (radians).
    /// </summary>
    /// <param name="points">Points to rotate.</param>
    /// <param name="axis">Axis to rotate around.</param>
    /// <param name="angle">Angle in radians.</param>
    /// <returns>New list of rotated points.</returns>
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

    /// <summary>
    /// Orients points so that local +Y aligns with the specified axis (best-effort).
    /// </summary>
    /// <param name="points">Points to orient.</param>
    /// <param name="axis">Desired up axis.</param>
    /// <returns>New list of oriented points.</returns>
    public static List<Vector> orientPoints(Collection<Vector> points, Vector axis) {
        Objects.requireNonNull(points, "points");
        Objects.requireNonNull(axis, "axis");
        Vector y = new Vector(0, 1, 0);
        Vector n = axis.clone().normalize();

        double dot = y.dot(n);
        // Already aligned:
        if (Math.abs(dot - 1.0) < 1e-6) return new ArrayList<>(points); // same direction
        if (Math.abs(dot + 1.0) < 1e-6) {                                // opposite: rotate 180° around any perpendicular
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

    /// <summary>
    /// Legacy alias of <see cref="spawnSingle(World, Particle, Location)"/>.
    /// </summary>
    /// <param name="world">World.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="location">Location.</param>
    /// <remarks>Deprecated: use <see cref="spawnSingle(World, Particle, Location)"/>.</remarks>
    @Deprecated
    public static void spawnParticle(World world, Particle particle, Location location) {
        spawnSingle(world, particle, location);
    }

    /// <summary>
    /// Legacy alias of <see cref="spawnSingle(World, Particle, Vector)"/>.
    /// </summary>
    /// <param name="world">World.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="point">Point.</param>
    /// <remarks>Deprecated: use <see cref="spawnSingle(World, Particle, Vector)"/>.</remarks>
    @Deprecated
    public static void spawnParticle(World world, Particle particle, Vector point) {
        spawnSingle(world, particle, point);
    }

    /// <summary>
    /// Legacy alias of <see cref="spawn(World, Particle, Location, int, double, double, double, double)"/>.
    /// </summary>
    /// <param name="world">World.</param>
    /// <param name="particle">Particle type.</param>
    /// <param name="location">Location.</param>
    /// <param name="count">Count.</param>
    /// <param name="offsetX">Offset X.</param>
    /// <param name="offsetY">Offset Y.</param>
    /// <param name="offsetZ">Offset Z.</param>
    /// <param name="speed">Speed.</param>
    /// <remarks>Deprecated: use modern overloads.</remarks>
    @Deprecated
    public static void spawnParticle(World world, Particle particle, Location location,
                                     int count, double offsetX, double offsetY, double offsetZ, double speed) {
        spawn(world, particle, location, count, offsetX, offsetY, offsetZ, speed);
    }
}