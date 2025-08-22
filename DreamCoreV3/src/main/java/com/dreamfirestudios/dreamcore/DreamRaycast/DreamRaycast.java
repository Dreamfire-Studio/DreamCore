package com.dreamfirestudios.dreamcore.DreamRaycast;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Raycasting helpers:
 * <ul>
 *   <li>Block, entity, and combined raycasts using Bukkit rayTrace APIs</li>
 *   <li>Include/ignore material filters (with step-through to skip unwanted blocks)</li>
 *   <li>Entity filters + ray size (thickness)</li>
 *   <li>Optional particle/path renderer for custom visualizations</li>
 * </ul>
 *
 * Usage examples:
 * <pre>
 *   // First solid block (ignoring GLASS, LEAVES), with a simple particle trail
 *   RaycastHit hit = DreamRaycast.raycast(
 *       player, 64.0,
 *       0.0, e -> true, // raySize=0 means "no entity test"
 *       Set.of(Material.GLASS, Material.OAK_LEAVES), false, // ignore these blocks
 *       (w, loc, t, max) -> w.spawnParticle(Particle.CRIT, loc, 1)
 *   );
 *
 *   // Entity-only (no block test), 0.25 ray thickness
 *   RaycastHit entityHit = DreamRaycast.raycastEntities(player, 40.0, 0.25, e -> e != player,
 *       (w, loc, t, max) -> {} // no trail
 *   );
 * </pre>
 */
public final class DreamRaycast {

    private DreamRaycast() {}

    // ===== Types =====

    /** Result of a raycast; at most one of block/entity is non-null. */
    public static final class RaycastHit {
        private final @Nullable Block block;
        private final @Nullable Entity entity;
        private final @Nullable Location hitPosition;
        private final double distance;

        public RaycastHit(@Nullable Block block, @Nullable Entity entity, @Nullable Location hitPosition, double distance) {
            this.block = block;
            this.entity = entity;
            this.hitPosition = hitPosition;
            this.distance = distance;
        }
        public @Nullable Block block() { return block; }
        public @Nullable Entity entity() { return entity; }
        public @Nullable Location hitPosition() { return hitPosition; }
        public double distance() { return distance; }
        public boolean hitBlock() { return block != null; }
        public boolean hitEntity() { return entity != null; }
    }

    /** Draws a path along the ray; t is distance from start in blocks, max is total distance drawn. */
    @FunctionalInterface
    public interface PathRenderer {
        void render(@NotNull World world, @NotNull Location point, double t, double max);
    }

    // ===== Public convenience wrappers =====

    /**
     * Raycast for the first non-ignored block. No entity test. Optional simple particle.
     */
    public static @Nullable Block rayCastFromPlayerIgnore(Player player, int range, @Nullable Particle particle, Set<Material> ignored) {
        RaycastHit hit = raycast(player, range, 0.0, e -> true, ignored, false,
                particle == null ? null : simpleParticle(particle));
        return hit == null ? null : hit.block();
    }

    /**
     * Raycast for the first block that MUST match any of the provided materials. No entity test.
     */
    public static @Nullable Block rayCastFromPlayerMust(Player player, int range, @Nullable Particle particle, Set<Material> mustMatch) {
        RaycastHit hit = raycast(player, range, 0.0, e -> true, mustMatch, true,
                particle == null ? null : simpleParticle(particle));
        return hit == null ? null : hit.block();
    }

    /**
     * Entity-only raycast (no block test).
     */
    public static @Nullable RaycastHit raycastEntities(Player player, double range, double raySize,
                                                       @NotNull Predicate<Entity> entityFilter,
                                                       @Nullable PathRenderer renderer) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(entityFilter, "entityFilter");
        World world = requireWorld(player);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().normalize();

        // Render the path (optional)
        if (renderer != null) drawPath(renderer, world, start, dir, range, 0.3);

        RayTraceResult ent = world.rayTraceEntities(start, dir, range, raySize, entityFilter);
        if (ent == null || ent.getHitEntity() == null || ent.getHitPosition() == null) return null;

        double dist = ent.getHitPosition().toLocation(world).distance(start);
        return new RaycastHit(null, ent.getHitEntity(), ent.getHitPosition().toLocation(world), dist);
    }

    /**
     * Combined raycast:
     * <ul>
     *   <li>Blocks: either skip {@code materials} (ignore mode) or only stop on them (must mode)</li>
     *   <li>Entities: tested with {@code entityFilter} and {@code raySize} (&gt; 0 to enable)</li>
     *   <li>Returns the closest hit of block/entity</li>
     *   <li>Optional path renderer for custom particles</li>
     * </ul>
     *
     * @param materials Set of materials to ignore (mustMatch=false) or to accept (mustMatch=true). May be empty.
     */
    public static @Nullable RaycastHit raycast(Player player,
                                               double range,
                                               double raySize,
                                               @NotNull Predicate<Entity> entityFilter,
                                               @NotNull Set<Material> materials,
                                               boolean mustMatch,
                                               @Nullable PathRenderer renderer) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(entityFilter, "entityFilter");
        Objects.requireNonNull(materials, "materials");
        if (range <= 0) throw new IllegalArgumentException("range must be > 0");
        if (raySize < 0) throw new IllegalArgumentException("raySize must be >= 0");

        World world = requireWorld(player);
        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().normalize();

        // We’ll find block hit by stepping through rayTraceBlocks repeatedly until
        // we get one that satisfies the filter condition (ignore or must).
        RaycastHit blockHit = traceBlocksWithMaterialFilter(world, start, dir, range, materials, mustMatch);

        // Entity hit (optional)
        RaycastHit entityHit = null;
        if (raySize > 0.0) {
            RayTraceResult er = world.rayTraceEntities(start, dir, range, raySize, entityFilter);
            if (er != null && er.getHitEntity() != null && er.getHitPosition() != null) {
                double d = er.getHitPosition().toLocation(world).distance(start);
                entityHit = new RaycastHit(null, er.getHitEntity(), er.getHitPosition().toLocation(world), d);
            }
        }

        // Choose closest
        RaycastHit chosen = closest(start, blockHit, entityHit);

        // Render the path (to the chosen distance if available; otherwise full range)
        if (renderer != null) {
            double max = chosen == null ? range : chosen.distance();
            drawPath(renderer, world, start, dir, max, 0.3);
        }

        return chosen;
    }

    // ===== Internals =====

    private static World requireWorld(Player p) {
        World w = p.getWorld();
        if (w == null) throw new IllegalStateException("Player world is null");
        return w;
    }

    private static @Nullable RaycastHit closest(Location start, @Nullable RaycastHit a, @Nullable RaycastHit b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.distance() <= b.distance() ? a : b;
    }

    /**
     * Step rayTraceBlocks until a block satisfies the material rule.
     */
    private static @Nullable RaycastHit traceBlocksWithMaterialFilter(World world,
                                                                      Location start,
                                                                      Vector dir,
                                                                      double range,
                                                                      Set<Material> materials,
                                                                      boolean mustMatch) {
        double remaining = range;
        Location cursor = start.clone();
        final double epsilon = 0.01;

        while (remaining > 0) {
            RayTraceResult br = world.rayTraceBlocks(
                    cursor, dir, remaining,
                    FluidCollisionMode.NEVER, // ignore fluids by default (tweak if needed)
                    true                      // ignore passable blocks so we stop on solids
            );
            if (br == null || br.getHitBlock() == null || br.getHitPosition() == null) return null;

            Block hit = br.getHitBlock();
            Location pos = br.getHitPosition().toLocation(world);
            double dist = pos.distance(cursor);

            boolean inSet = materials.contains(hit.getType());
            if (mustMatch ? inSet : !inSet) {
                double totalDist = start.distance(pos);
                return new RaycastHit(hit, null, pos, totalDist);
            }

            // Skip this block and continue from just beyond the hit point
            double advance = Math.max(dist + epsilon, epsilon);
            cursor.add(dir.clone().multiply(advance));
            remaining -= advance;
        }
        return null;
    }
    /**
     * Draws a simple parametric path from start along dir up to maxDistance.
     * step controls spacing between points.
     */
    private static void drawPath(PathRenderer renderer, World world, Location start, Vector dir, double maxDistance, double step) {
        Vector unit = dir.clone().normalize();
        double drawn = 0;
        while (drawn <= maxDistance) {
            Location p = start.clone().add(unit.clone().multiply(drawn));
            renderer.render(world, p, drawn, maxDistance);
            drawn += step;
        }
    }

    /**
     * Creates a basic renderer that spawns one particle per step at the path point.
     */
    public static PathRenderer simpleParticle(Particle particle) {
        Objects.requireNonNull(particle, "particle");
        return (world, point, t, max) -> world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
    }
}