package com.dreamfirestudios.dreamCore.DreamfireBlockDisplay;

import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireBlockDisplay.BlockDisplayCreatedEvent;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Utility class for building and spawning BlockDisplay entities.
 */
public class DreamfireBlockDisplay {

    public static class BlockDisplayBuilder {
        private World world;
        private Location location;
        private double itemScale = 1.0;
        private Vector3f leftRotation = new Vector3f(0f, 0f, 0f);
        private float viewRange = 0.1f;
        private float shadowRadius = 0.3f;
        private float shadowStrength = 5f;
        private float displayWidth = 50f;
        private float displayHeight = 50f;
        private Display.Billboard billboard = Display.Billboard.CENTER;
        private Color itemGlowColor = Color.RED;
        private Display.Brightness itemBrightness = new Display.Brightness(15, 15);
        private Consumer<Transformation> customTransformation;

        /**
         * Constructs a new BlockDisplayBuilder with default world ("world") and location (spawn).
         *
         * @throws IllegalStateException if the default world is not available.
         */
        public BlockDisplayBuilder() {
            this.world = Bukkit.getWorld("world");
            if (this.world == null) throw new IllegalStateException("Default world 'world' is not available.");
            this.location = this.world.getSpawnLocation();
        }

        public BlockDisplayBuilder world(World world) {
            if (world == null) throw new IllegalArgumentException("World cannot be null");
            this.world = world;
            return this;
        }

        public BlockDisplayBuilder location(Location location) {
            if (location == null) throw new IllegalArgumentException("Location cannot be null");
            this.location = location;
            return this;
        }

        public BlockDisplayBuilder itemScale(double itemScale) {
            if (itemScale <= 0) throw new IllegalArgumentException("Item scale must be greater than zero");
            this.itemScale = itemScale;
            return this;
        }

        public BlockDisplayBuilder leftRotation(Vector3f leftRotation) {
            if (leftRotation == null) throw new IllegalArgumentException("Rotation vector cannot be null");
            this.leftRotation = leftRotation;
            return this;
        }

        /**
         * Sets a custom transformation consumer. Use this to apply additional transformation changes,
         * such as right rotation, translation, or non-uniform scaling.
         *
         * @param customTransformation A Consumer that receives the current transformation.
         * @return The BlockDisplayBuilder instance.
         * @throws IllegalArgumentException if customTransformation is null.
         */
        public BlockDisplayBuilder customTransformation(Consumer<Transformation> customTransformation) {
            if (customTransformation == null) throw new IllegalArgumentException("Custom transformation cannot be null");
            this.customTransformation = customTransformation;
            return this;
        }

        public BlockDisplayBuilder viewRange(float viewRange) {
            if (viewRange < 0) throw new IllegalArgumentException("View range must be non-negative");
            this.viewRange = viewRange;
            return this;
        }

        public BlockDisplayBuilder shadowRadius(float shadowRadius) {
            if (shadowRadius < 0) throw new IllegalArgumentException("Shadow radius must be non-negative");
            this.shadowRadius = shadowRadius;
            return this;
        }

        public BlockDisplayBuilder shadowStrength(float shadowStrength) {
            if (shadowStrength < 0) throw new IllegalArgumentException("Shadow strength must be non-negative");
            this.shadowStrength = shadowStrength;
            return this;
        }

        public BlockDisplayBuilder displayHeight(float displayHeight) {
            if (displayHeight <= 0) throw new IllegalArgumentException("Display height must be greater than zero");
            this.displayHeight = displayHeight;
            return this;
        }

        public BlockDisplayBuilder displayWidth(float displayWidth) {
            if (displayWidth <= 0) throw new IllegalArgumentException("Display width must be greater than zero");
            this.displayWidth = displayWidth;
            return this;
        }

        public BlockDisplayBuilder billboard(Display.Billboard billboard) {
            this.billboard = billboard;
            return this;
        }

        public BlockDisplayBuilder itemGlowColor(Color itemGlowColor) {
            if (itemGlowColor == null) throw new IllegalArgumentException("Glow color cannot be null");
            this.itemGlowColor = itemGlowColor;
            return this;
        }

        public BlockDisplayBuilder itemBrightness(Display.Brightness itemBrightness) {
            if (itemBrightness == null) throw new IllegalArgumentException("Brightness cannot be null");
            this.itemBrightness = itemBrightness;
            return this;
        }

        /**
         * Spawns a BlockDisplay with the specified material and the configured attributes.
         *
         * @param material the material to display; must not be null.
         * @return the spawned BlockDisplay instance.
         * @throws IllegalArgumentException if material is null.
         */
        public BlockDisplay spawn(Material material) {
            if (material == null) throw new IllegalArgumentException("Material cannot be null");
            BlockData blockData = Bukkit.createBlockData(material);
            return spawn(blockData);
        }

        /**
         * Spawns a BlockDisplay with the specified BlockData and the configured attributes.
         *
         * @param blockData the BlockData to display; must not be null.
         * @return the spawned BlockDisplay instance.
         * @throws IllegalArgumentException if blockData is null.
         */
        public BlockDisplay spawn(BlockData blockData) {
            if (blockData == null) throw new IllegalArgumentException("BlockData cannot be null");
            // Optionally, you can use pooling here instead of directly spawning:
            BlockDisplay blockDisplay = world.spawn(location, BlockDisplay.class);
            blockDisplay.setBlock(blockData);
            return configureBlockDisplay(blockDisplay);
        }

        /**
         * Configures a BlockDisplay entity with the attributes set in this builder.
         *
         * @param blockDisplay the BlockDisplay entity to configure.
         * @return the configured BlockDisplay entity.
         */
        private BlockDisplay configureBlockDisplay(BlockDisplay blockDisplay) {
            // Apply transformation settings.
            var transformation = blockDisplay.getTransformation();
            transformation.getScale().set((float) itemScale);
            transformation.getLeftRotation().x = leftRotation.x;
            transformation.getLeftRotation().y = leftRotation.y;
            transformation.getLeftRotation().z = leftRotation.z;
            if (customTransformation != null) customTransformation.accept(transformation);
            blockDisplay.setTransformation(transformation);
            blockDisplay.setViewRange(viewRange);
            blockDisplay.setShadowRadius(shadowRadius);
            blockDisplay.setShadowStrength(shadowStrength);
            blockDisplay.setDisplayHeight(displayHeight);
            blockDisplay.setDisplayWidth(displayWidth);
            blockDisplay.setBillboard(billboard);
            blockDisplay.setGlowColorOverride(itemGlowColor);
            blockDisplay.setBrightness(itemBrightness);
            BlockDisplayCreatedEvent event = new BlockDisplayCreatedEvent(blockDisplay);
            Bukkit.getPluginManager().callEvent(event);
            return blockDisplay;
        }
    }
}