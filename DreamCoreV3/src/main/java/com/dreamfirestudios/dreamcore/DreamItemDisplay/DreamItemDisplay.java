package com.dreamfirestudios.dreamcore.DreamItemDisplay;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Builder for spawning configured {@link ItemDisplay} entities.
 * Uses Paper-safe defaults and clamps out-of-range values.
 */
public final class DreamItemDisplay {

    private DreamItemDisplay() {}

    public static class ItemDisplayBuilder {
        // Defaults
        private World world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        private Location location = (world == null) ? null : world.getSpawnLocation();

        private float uniformScale = 1.0f;
        private Vector3f scale = null;                        // if non-null, overrides uniformScale
        private Quaternionf leftRotation = new Quaternionf(); // identity
        private Quaternionf rightRotation = new Quaternionf();// identity
        private Vector3f translation = new Vector3f(0f, 0f, 0f);

        // Display properties
        /** Render distance in blocks (Paper uses a float in blocks). Must be >= 0. */
        private float viewRange = 16.0f;
        /** Shadow radius in blocks (>= 0). */
        private float shadowRadius = 0.3f;
        /** Shadow strength clamped to [0..1]. */
        private float shadowStrength = 0.5f;
        /** Width/height are mainly relevant for text displays, but Paper exposes them for all displays. */
        private float displayWidth = 1f;
        private float displayHeight = 1f;
        private Display.Billboard billboard = Display.Billboard.CENTER;
        private Color itemGlowColor = null; // null = no override
        private Display.Brightness itemBrightness = null; // null = default brightness

        // -------------------- World / position --------------------

        /** World where the display will be spawned. */
        public ItemDisplayBuilder world(World world) {
            if (world == null) throw new IllegalArgumentException("World cannot be null");
            this.world = world;
            if (this.location == null) this.location = world.getSpawnLocation();
            return this;
        }

        /** Spawn location (must be in the same world as {@link #world(World)}). */
        public ItemDisplayBuilder location(Location location) {
            if (location == null) throw new IllegalArgumentException("Location cannot be null");
            this.location = location;
            if (location.getWorld() != null) this.world = location.getWorld();
            return this;
        }

        /** Optional local translation applied via {@link Transformation}. */
        public ItemDisplayBuilder translation(Vector3f translation) {
            if (translation == null) throw new IllegalArgumentException("translation cannot be null");
            this.translation = new Vector3f(translation);
            return this;
        }

        // -------------------- Scale --------------------

        /** Uniform scale (x=y=z). Ignored if {@link #scale(Vector3f)} is set. */
        public ItemDisplayBuilder scale(float uniform) {
            if (uniform <= 0f) throw new IllegalArgumentException("Scale must be > 0");
            this.uniformScale = uniform;
            this.scale = null;
            return this;
        }

        /** Non-uniform scale vector. */
        public ItemDisplayBuilder scale(Vector3f scale) {
            if (scale == null) throw new IllegalArgumentException("scale cannot be null");
            if (scale.x <= 0f || scale.y <= 0f || scale.z <= 0f)
                throw new IllegalArgumentException("scale components must be > 0");
            this.scale = new Vector3f(scale);
            return this;
        }

        // -------------------- Rotation --------------------

        /** Set the left rotation quaternion. */
        public ItemDisplayBuilder leftRotation(Quaternionf q) {
            if (q == null) throw new IllegalArgumentException("leftRotation cannot be null");
            this.leftRotation = new Quaternionf(q);
            return this;
        }

        /** Set the right rotation quaternion. */
        public ItemDisplayBuilder rightRotation(Quaternionf q) {
            if (q == null) throw new IllegalArgumentException("rightRotation cannot be null");
            this.rightRotation = new Quaternionf(q);
            return this;
        }

        /**
         * Convenience: set rotations using Euler angles in degrees.
         * Angles are applied yaw(Z), pitch(X), roll(Y) in that order to a fresh quaternion.
         */
        public ItemDisplayBuilder eulerDegrees(float yawDeg, float pitchDeg, float rollDeg) {
            // Convert degrees to radians and compose a quaternion
            final float yaw = (float) Math.toRadians(yawDeg);
            final float pitch = (float) Math.toRadians(pitchDeg);
            final float roll = (float) Math.toRadians(rollDeg);

            this.leftRotation = new Quaternionf()
                    .rotateZ(yaw)
                    .rotateX(pitch)
                    .rotateY(roll);
            return this;
        }

        // -------------------- Display props --------------------

        /**
         * Render distance in blocks (>= 0).
         * Paper uses this directly as the distance at which the display stops rendering.
         */
        public ItemDisplayBuilder viewRange(float viewRange) {
            if (viewRange < 0f) throw new IllegalArgumentException("viewRange must be >= 0");
            this.viewRange = viewRange;
            return this;
        }

        public ItemDisplayBuilder shadowRadius(float shadowRadius) {
            if (shadowRadius < 0f) throw new IllegalArgumentException("shadowRadius must be >= 0");
            this.shadowRadius = shadowRadius;
            return this;
        }

        /** Strength is clamped to [0..1] when applied. */
        public ItemDisplayBuilder shadowStrength(float shadowStrength) {
            this.shadowStrength = shadowStrength;
            return this;
        }

        public ItemDisplayBuilder displayHeight(float displayHeight) {
            if (displayHeight <= 0f) throw new IllegalArgumentException("displayHeight must be > 0");
            this.displayHeight = displayHeight;
            return this;
        }

        public ItemDisplayBuilder displayWidth(float displayWidth) {
            if (displayWidth <= 0f) throw new IllegalArgumentException("displayWidth must be > 0");
            this.displayWidth = displayWidth;
            return this;
        }

        public ItemDisplayBuilder billboard(Display.Billboard billboard) {
            this.billboard = (billboard == null) ? Display.Billboard.CENTER : billboard;
            return this;
        }

        public ItemDisplayBuilder glowColor(Color color) {
            this.itemGlowColor = color; // null clears
            return this;
        }

        public ItemDisplayBuilder brightness(Display.Brightness brightness) {
            this.itemBrightness = brightness; // null = default
            return this;
        }

        // -------------------- Spawn --------------------

        /**
         * Spawns the {@link ItemDisplay} with all configured properties.
         *
         * @param itemStack item to display (must not be null or AIR)
         * @return the spawned item display
         */
        public ItemDisplay spawn(ItemStack itemStack) {
            if (itemStack == null || itemStack.getType() == Material.AIR)
                throw new IllegalArgumentException("ItemStack cannot be null or AIR");
            if (world == null)
                throw new IllegalStateException("No world available to spawn ItemDisplay");
            if (location == null)
                location = world.getSpawnLocation();

            final ItemDisplay display = world.spawn(location, ItemDisplay.class);
            display.setItemStack(itemStack);

            // Build a fresh Transformation and set it (avoid in-place mutation)
            final Vector3f sc = (scale != null) ? new Vector3f(scale) : new Vector3f(uniformScale);
            final Transformation tf = new Transformation(
                    new Vector3f(translation),
                    new Quaternionf(leftRotation),
                    sc,
                    new Quaternionf(rightRotation)
            );
            display.setTransformation(tf);

            display.setViewRange(Math.max(0f, viewRange));
            display.setShadowRadius(Math.max(0f, shadowRadius));
            display.setShadowStrength(Math.max(0f, Math.min(1f, shadowStrength)));
            display.setDisplayWidth(Math.max(0.0001f, displayWidth));
            display.setDisplayHeight(Math.max(0.0001f, displayHeight));
            display.setBillboard(billboard);

            if (itemGlowColor != null) display.setGlowColorOverride(itemGlowColor);
            if (itemBrightness != null) display.setBrightness(itemBrightness);

            new ItemDisplaySpawnEvent(display);
            return display;
        }
    }
}