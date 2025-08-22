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

/// <summary>
/// Utility helpers for performing player-centric raycasts against blocks and entities,
/// with optional material/entity filtering and lightweight path rendering.
/// </summary>
/// <remarks>
/// <list type="bullet">
///   <item><description>Block, entity, and combined raycasts using Bukkit <c>rayTrace</c> APIs</description></item>
///   <item><description>Material include/ignore modes (with step-through to skip unwanted blocks)</description></item>
///   <item><description>Entity filter predicate + ray thickness (capsule)</description></item>
///   <item><description>Optional path renderer callback for custom particle trails</description></item>
/// </list>
/// </remarks>
/// <example>
/// <code>
/// // First solid block (ignoring GLASS, LEAVES), with a simple particle trail
/// RaycastHit hit = DreamRaycast.raycast(
///     player, 64.0,
///     0.0, e -&gt; true, // raySize=0 means "no entity test"
///     Set.of(Material.GLASS, Material.OAK_LEAVES), false, // ignore these blocks
///     (w, loc, t, max) -&gt; w.spawnParticle(Particle.CRIT, loc, 1)
/// );
///
/// // Entity-only (no block test), 0.25 ray thickness
/// RaycastHit entityHit = DreamRaycast.raycastEntities(
///     player, 40.0, 0.25, e -&gt; e != player,
///     (w, loc, t, max) -&gt; {} // no trail
/// );
/// </code>
/// </example>
public final class DreamRaycast {

    private DreamRaycast() {}

    // ===== Types =====================================================================================================

    /// <summary>
    /// Result of a raycast; at most one of <see cref="block()"/> or <see cref="entity()"/> is non-null.
    /// </summary>
    public static final class RaycastHit {
        private final @Nullable Block block;
        private final @Nullable Entity entity;
        private final @Nullable Location hitPosition;
        private final double distance;

        /// <summary>
        /// Initializes a raycast hit record.
        /// </summary>
        /// <param name="block">Hit block (if any).</param>
        /// <param name="entity">Hit entity (if any).</param>
        /// <param name="hitPosition">Exact hit position in the world (if available).</param>
        /// <param name="distance">Distance from the ray origin to <paramref name="hitPosition"/>.</param>
        public RaycastHit(@Nullable Block block, @Nullable Entity entity, @Nullable Location hitPosition, double distance) {
            this.block = block;
            this.entity = entity;
            this.hitPosition = hitPosition;
            this.distance = distance;
        }
        /// <summary>Gets the hit block (if any).</summary>
        public @Nullable Block block() { return block; }
        /// <summary>Gets the hit entity (if any).</summary>
        public @Nullable Entity entity() { return entity; }
        /// <summary>Gets the exact world-space hit location (if available).</summary>
        public @Nullable Location hitPosition() { return hitPosition; }
        /// <summary>Gets the distance from the ray origin to the hit.</summary>
        public double distance() { return distance; }
        /// <summary>True if a block was hit.</summary>
        public boolean hitBlock() { return block != null; }
        /// <summary>True if an entity was hit.</summary>
        public boolean hitEntity() { return entity != null; }
    }

    /// <summary>
    /// Draws a path along the ray; <paramref name="t"/> is distance from start in blocks, <paramref name="max"/> is
    /// total distance drawn. Implement to render particles or debug markers.
    /// </summary>
    @FunctionalInterface
    public interface PathRenderer {
        /// <summary>
        /// Called for each step along the visualized ray.
        /// </summary>
        /// <param name="world">World of the ray.</param>
        /// <param name="point">Point along the ray.</param>
        /// <param name="t">Distance from origin (blocks).</param>
        /// <param name="max">Total distance visualized.</param>
        void render(@NotNull World world, @NotNull Location point, double t, double max);
    }

    // ===== Public convenience wrappers ==============================================================================

    /// <summary>
    /// Raycasts from the player's eye for the first non-ignored block (no entity test).
    /// </summary>
    /// <param name="player">Source player.</param>
    /// <param name="range">Max range in blocks.</param>
    /// <param name="particle">Optional particle for a simple trail (null = no trail).</param>
    /// <param name="ignored">Set of materials to ignore when stopping.</param>
    /// <returns>The first non-ignored block or <c>null</c> if none within range.</returns>
    /// <example>
    /// <code>
    /// Block b = DreamRaycast.rayCastFromPlayerIgnore(player, 64, Particle.CRIT, Set.of(Material.GLASS));
    /// </code>
    /// </example>
    public static @Nullable Block rayCastFromPlayerIgnore(Player player, int range, @Nullable Particle particle, Set<Material> ignored) {
        RaycastHit hit = raycast(player, range, 0.0, e -> true, ignored, false,
                particle == null ? null : simpleParticle(particle));
        return hit == null ? null : hit.block();
    }

    /// <summary>
    /// Raycasts from the player's eye for the first block that MUST match any of the provided materials (no entity test).
    /// </summary>
    /// <param name="player">Source player.</param>
    /// <param name="range">Max range in blocks.</param>
    /// <param name="particle">Optional particle for a simple trail (null = no trail).</param>
    /// <param name="mustMatch">Set of materials that the hit must belong to.</param>
    /// <returns>The first matching block or <c>null</c> if none within range.</returns>
    public static @Nullable Block rayCastFromPlayerMust(Player player, int range, @Nullable Particle particle, Set<Material> mustMatch) {
        RaycastHit hit = raycast(player, range, 0.0, e -> true, mustMatch, true,
                particle == null ? null : simpleParticle(particle));
        return hit == null ? null : hit.block();
    }

    /// <summary>
    /// Entity-only raycast (no block test).
    /// </summary>
    /// <param name="player">Source player.</param>
    /// <param name="range">Max range in blocks.</param>
    /// <param name="raySize">Ray thickness (capsule radius). Use 0 to disable entity test.</param>
    /// <param name="entityFilter">Filter predicate for entities; return true to consider as hittable.</param>
    /// <param name="renderer">Optional path renderer for visualization (null = none).</param>
    /// <returns>Raycast hit or <c>null</c> if no entity was hit.</returns>
    /// <example>
    /// <code>
    /// RaycastHit ent = DreamRaycast.raycastEntities(player, 32, 0.3, e -&gt; e != player, null);
    /// </code>
    /// </example>
    public static @Nullable RaycastHit raycastEntities(Player player, double range, double raySize,
                                                       @NotNull Predicate<Entity> entityFilter,
                                                       @Nullable PathRenderer renderer) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(entityFilter, "entityFilter");
        World world = requireWorld(player);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().normalize();

        // Render path (optional)
        if (renderer != null) drawPath(renderer, world, start, dir, range, 0.3);

        RayTraceResult ent = world.rayTraceEntities(start, dir, range, raySize, entityFilter);
        if (ent == null || ent.getHitEntity() == null || ent.getHitPosition() == null) return null;

        double dist = ent.getHitPosition().toLocation(world).distance(start);
        return new RaycastHit(null, ent.getHitEntity(), ent.getHitPosition().toLocation(world), dist);
    }

    /// <summary>
    /// Combined raycast that tests blocks (with material include/ignore rules) and optionally entities.
    /// Returns the closest of the two (if both are hit).
    /// </summary>
    /// <param name="player">Source player.</param>
    /// <param name="range">Max range in blocks (&gt; 0).</param>
    /// <param name="raySize">Entity ray thickness (capsule radius; 0 disables entity test).</param>
    /// <param name="entityFilter">Entity filter predicate (required; ignored if <paramref name="raySize"/> ≤ 0).</param>
    /// <param name="materials">Set of materials (see <paramref name="mustMatch"/>).</param>
    /// <param name="mustMatch"><c>true</c> = stop only on materials in the set; <c>false</c> = skip those and stop otherwise.</param>
    /// <param name="renderer">Optional path renderer (null = none).</param>
    /// <returns>Closest hit (block or entity) or <c>null</c> if nothing was hit.</returns>
    /// <example>
    /// <code>
    /// RaycastHit best = DreamRaycast.raycast(
    ///     player, 50, 0.2, e -&gt; e != player,
    ///     Set.of(Material.GLASS, Material.OAK_LEAVES), false,
    ///     DreamRaycast.simpleParticle(Particle.END_ROD)
    /// );
    /// </code>
    /// </example>
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

        // Block hit (respect include/ignore rule)
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

        // Render path (to chosen distance if available; otherwise full range)
        if (renderer != null) {
            double max = chosen == null ? range : chosen.distance();
            drawPath(renderer, world, start, dir, max, 0.3);
        }

        return chosen;
    }

    // ===== Internals =================================================================================================

    /// <summary>Ensures the player's world is non-null.</summary>
    private static World requireWorld(Player p) {
        World w = p.getWorld();
        if (w == null) throw new IllegalStateException("Player world is null");
        return w;
    }

    /// <summary>Returns the closer non-null hit relative to <paramref name="start"/>.</summary>
    private static @Nullable RaycastHit closest(Location start, @Nullable RaycastHit a, @Nullable RaycastHit b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.distance() <= b.distance() ? a : b;
    }

    /// <summary>
    /// Steps <c>rayTraceBlocks</c> repeatedly until a hit satisfies the material rule.
    /// </summary>
    /// <param name="world">World.</param>
    /// <param name="start">Ray start.</param>
    /// <param name="dir">Normalized direction.</param>
    /// <param name="range">Max range.</param>
    /// <param name="materials">Material set.</param>
    /// <param name="mustMatch">Include vs ignore mode.</param>
    /// <returns>Matching hit or <c>null</c>.</returns>
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
                    FluidCollisionMode.NEVER, // ignore fluids by default
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

    /// <summary>
    /// Draws a simple parametric path from start along <paramref name="dir"/> up to <paramref name="maxDistance"/>.
    /// </summary>
    /// <param name="renderer">Renderer callback.</param>
    /// <param name="world">World.</param>
    /// <param name="start">Start location.</param>
    /// <param name="dir">Direction (normalized internally).</param>
    /// <param name="maxDistance">Maximum distance to draw.</param>
    /// <param name="step">Spacing between samples (blocks).</param>
    private static void drawPath(PathRenderer renderer, World world, Location start, Vector dir, double maxDistance, double step) {
        Vector unit = dir.clone().normalize();
        double drawn = 0;
        while (drawn <= maxDistance) {
            Location p = start.clone().add(unit.clone().multiply(drawn));
            renderer.render(world, p, drawn, maxDistance);
            drawn += step;
        }
    }

    /// <summary>
    /// Creates a basic renderer that spawns one particle per step at the path point.
    /// </summary>
    /// <param name="particle">Particle type to spawn.</param>
    /// <returns>A <see cref="PathRenderer"/> that spawns the particle.</returns>
    /// <example>
    /// <code>
    /// DreamRaycast.PathRenderer trail = DreamRaycast.simpleParticle(Particle.END_ROD);
    /// </code>
    /// </example>
    public static PathRenderer simpleParticle(Particle particle) {
        Objects.requireNonNull(particle, "particle");
        return (world, point, t, max) -> world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
    }
}