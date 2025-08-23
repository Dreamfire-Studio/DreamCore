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
package com.dreamfirestudios.dreamcore.DreamBlockDisplay;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.function.Consumer;

/// <summary>
/// Utility wrapper for building and spawning <see cref="BlockDisplay"/> entities with a fluent API.
/// </summary>
/// <remarks>
/// Use <see cref="DreamBlockDisplay.BlockDisplayBuilder"/> to configure display properties
/// (scale, rotation, brightness, billboard, etc.) and then spawn a configured <see cref="BlockDisplay"/>.
/// A <see cref="BlockDisplayCreatedEvent"/> is fired after configuration.
/// </remarks>
/// <example>
/// ```java
/// var display = new DreamBlockDisplay.BlockDisplayBuilder()
///     .world(Bukkit.getWorld("world"))
///     .location(new Location(Bukkit.getWorld("world"), 0, 64, 0))
///     .itemScale(0.75)
///     .leftRotation(new Vector3f(0f, (float)Math.toRadians(45), 0f))
///     .billboard(Display.Billboard.CENTER)
///     .spawn(Material.DIAMOND_BLOCK);
/// ```
/// </example>
public final class DreamBlockDisplay {

    /// <summary>
    /// Fluent builder for configuring and spawning <see cref="BlockDisplay"/> entities.
    /// </summary>
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

        /// <summary>
        /// Initializes a builder using the default world <c>"world"</c> and its spawn location.
        /// </summary>
        /// <exception cref="IllegalStateException">Thrown if the default world is not available.</exception>
        public BlockDisplayBuilder() {
            this.world = Bukkit.getWorld("world");
            if (this.world == null) throw new IllegalStateException("Default world 'world' is not available.");
            this.location = this.world.getSpawnLocation();
        }

        /// <summary>Sets the world for the display.</summary>
        /// <param name="world">World in which to spawn the display.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder world(World world) {
            if (world == null) throw new IllegalArgumentException("World cannot be null");
            this.world = world;
            return this;
        }

        /// <summary>Sets the spawn location for the display.</summary>
        /// <param name="location">Target location.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder location(Location location) {
            if (location == null) throw new IllegalArgumentException("Location cannot be null");
            this.location = location;
            return this;
        }

        /// <summary>Sets a uniform item scale (applied to X/Y/Z).</summary>
        /// <param name="itemScale">Scale factor &gt; 0.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder itemScale(double itemScale) {
            if (itemScale <= 0) throw new IllegalArgumentException("Item scale must be greater than zero");
            this.itemScale = itemScale;
            return this;
        }

        /// <summary>Sets the left rotation (applied in transformation matrix).</summary>
        /// <param name="leftRotation">Quaternion (x,y,z) components; w is implied by engine.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder leftRotation(Vector3f leftRotation) {
            if (leftRotation == null) throw new IllegalArgumentException("Rotation vector cannot be null");
            this.leftRotation = leftRotation;
            return this;
        }

        /// <summary>Provides a custom per-spawn transformation mutator.</summary>
        /// <param name="customTransformation">Consumer that can mutate translation/rotation/scale.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder customTransformation(Consumer<Transformation> customTransformation) {
            if (customTransformation == null) throw new IllegalArgumentException("Custom transformation cannot be null");
            this.customTransformation = customTransformation;
            return this;
        }

        /// <summary>Sets the render view range.</summary>
        /// <param name="viewRange">Non-negative view range.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder viewRange(float viewRange) {
            if (viewRange < 0) throw new IllegalArgumentException("View range must be non-negative");
            this.viewRange = viewRange;
            return this;
        }

        /// <summary>Sets the shadow radius.</summary>
        /// <param name="shadowRadius">Non-negative shadow radius.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder shadowRadius(float shadowRadius) {
            if (shadowRadius < 0) throw new IllegalArgumentException("Shadow radius must be non-negative");
            this.shadowRadius = shadowRadius;
            return this;
        }

        /// <summary>Sets the shadow strength.</summary>
        /// <param name="shadowStrength">Non-negative shadow strength.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder shadowStrength(float shadowStrength) {
            if (shadowStrength < 0) throw new IllegalArgumentException("Shadow strength must be non-negative");
            this.shadowStrength = shadowStrength;
            return this;
        }

        /// <summary>Sets the logical display height.</summary>
        /// <param name="displayHeight">Positive display height.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder displayHeight(float displayHeight) {
            if (displayHeight <= 0) throw new IllegalArgumentException("Display height must be greater than zero");
            this.displayHeight = displayHeight;
            return this;
        }

        /// <summary>Sets the logical display width.</summary>
        /// <param name="displayWidth">Positive display width.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder displayWidth(float displayWidth) {
            if (displayWidth <= 0) throw new IllegalArgumentException("Display width must be greater than zero");
            this.displayWidth = displayWidth;
            return this;
        }

        /// <summary>Sets billboard mode.</summary>
        /// <param name="billboard">Billboard type.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder billboard(Display.Billboard billboard) {
            this.billboard = billboard;
            return this;
        }

        /// <summary>Sets glow color override for the display.</summary>
        /// <param name="itemGlowColor">Non-null color.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder itemGlowColor(Color itemGlowColor) {
            if (itemGlowColor == null) throw new IllegalArgumentException("Glow color cannot be null");
            this.itemGlowColor = itemGlowColor;
            return this;
        }

        /// <summary>Sets emissive brightness.</summary>
        /// <param name="itemBrightness">Non-null brightness.</param>
        /// <returns>This builder.</returns>
        public BlockDisplayBuilder itemBrightness(Display.Brightness itemBrightness) {
            if (itemBrightness == null) throw new IllegalArgumentException("Brightness cannot be null");
            this.itemBrightness = itemBrightness;
            return this;
        }

        /// <summary>Spawns a <see cref="BlockDisplay"/> using a <see cref="Material"/>.</summary>
        /// <param name="material">Material to represent.</param>
        /// <returns>The spawned display.</returns>
        public BlockDisplay spawn(Material material) {
            if (material == null) throw new IllegalArgumentException("Material cannot be null");
            BlockData blockData = Bukkit.createBlockData(material);
            return spawn(blockData);
        }

        /// <summary>Spawns a <see cref="BlockDisplay"/> using a <see cref="BlockData"/>.</summary>
        /// <param name="blockData">Block data to represent.</param>
        /// <returns>The spawned display.</returns>
        public BlockDisplay spawn(BlockData blockData) {
            if (blockData == null) throw new IllegalArgumentException("BlockData cannot be null");
            BlockDisplay blockDisplay = world.spawn(location, BlockDisplay.class);
            blockDisplay.setBlock(blockData);
            return configureBlockDisplay(blockDisplay);
        }

        /// <summary>
        /// Applies all configured properties to the supplied display and fires <see cref="BlockDisplayCreatedEvent"/>.
        /// </summary>
        /// <param name="blockDisplay">Display to configure.</param>
        /// <returns>The configured display.</returns>
        private BlockDisplay configureBlockDisplay(BlockDisplay blockDisplay) {
            Transformation transformation = blockDisplay.getTransformation();
            transformation.getScale().set((float) itemScale);
            transformation.getLeftRotation().x = leftRotation.x;
            transformation.getLeftRotation().y = leftRotation.y;
            transformation.getLeftRotation().z = leftRotation.z;

            if (customTransformation != null) {
                customTransformation.accept(transformation);
            }

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