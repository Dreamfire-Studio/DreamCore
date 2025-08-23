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

/// <summary>
/// Fluent builder utilities for spawning configured <see cref="ItemDisplay"/> entities.
/// </summary>
/// <remarks>
/// All operations are Paper/Bukkit safe and clamp invalid ranges (e.g., negative view range,
/// shadow radius/strength, zero display width/height). Rotations are provided as quaternions or
/// via a convenience Euler degrees method. Non-uniform scaling is supported.
/// <para>⚠ Spawning must occur on the main server thread.</para>
/// </remarks>
/// <example>
/// <code>
/// ItemDisplay display = new DreamItemDisplay.ItemDisplayBuilder()
///     .world(player.getWorld())
///     .location(player.getLocation().add(0, 1.8, 0))
///     .eulerDegrees(0, 45, 0)
///     .scale(0.8f)
///     .viewRange(24.0f)
///     .shadowRadius(0.4f)
///     .shadowStrength(0.7f)
///     .glowColor(Color.AQUA)
///     .billboard(Display.Billboard.CENTER)
///     .spawn(new ItemStack(Material.DIAMOND_SWORD));
/// </code>
/// </example>
public final class DreamItemDisplay {

    private DreamItemDisplay() {}

    /// <summary>
    /// Builder for a single <see cref="ItemDisplay"/> instance.
    /// </summary>
    /// <remarks>
    /// Fields are initialized with safe defaults. If <c>world</c> or <c>location</c> is not specified,
    /// it falls back to the server's first world and its spawn location (when available).
    /// </remarks>
    public static class ItemDisplayBuilder {
        // -------------------- Defaults --------------------
        private World world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        private Location location = (world == null) ? null : world.getSpawnLocation();

        private float uniformScale = 1.0f;
        private Vector3f scale = null;                        // if non-null, overrides uniformScale
        private Quaternionf leftRotation = new Quaternionf(); // identity
        private Quaternionf rightRotation = new Quaternionf();// identity
        private Vector3f translation = new Vector3f(0f, 0f, 0f);

        // Display properties
        /// <summary>Render distance in blocks (>= 0). Paper uses a float in blocks.</summary>
        private float viewRange = 16.0f;

        /// <summary>Shadow radius in blocks (>= 0).</summary>
        private float shadowRadius = 0.3f;

        /// <summary>Shadow strength clamped to [0..1].</summary>
        private float shadowStrength = 0.5f;

        /// <summary>Display width in world units. Clamped to a small positive value.</summary>
        private float displayWidth = 1f;

        /// <summary>Display height in world units. Clamped to a small positive value.</summary>
        private float displayHeight = 1f;

        private Display.Billboard billboard = Display.Billboard.CENTER;
        private Color itemGlowColor = null;                // null = no override
        private Display.Brightness itemBrightness = null;  // null = default brightness

        // -------------------- World / position --------------------

        /// <summary>
        /// Sets the world where the display will be spawned.
        /// </summary>
        /// <param name="world">Non-null world reference.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="world"/> is null.</exception>
        /// <example>
        /// <code>builder.world(player.getWorld());</code>
        /// </example>
        public ItemDisplayBuilder world(World world) {
            if (world == null) throw new IllegalArgumentException("World cannot be null");
            this.world = world;
            if (this.location == null) this.location = world.getSpawnLocation();
            return this;
        }

        /// <summary>
        /// Sets the spawn location (must be in the same world as <see cref="world(World)"/>).
        /// </summary>
        /// <param name="location">Non-null location.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="location"/> is null.</exception>
        /// <remarks>Automatically updates <c>world</c> from the location (if non-null).</remarks>
        /// <example>
        /// <code>builder.location(new Location(world, 10.5, 65, -7.25));</code>
        /// </example>
        public ItemDisplayBuilder location(Location location) {
            if (location == null) throw new IllegalArgumentException("Location cannot be null");
            this.location = location;
            if (location.getWorld() != null) this.world = location.getWorld();
            return this;
        }

        /// <summary>
        /// Applies a local translation (pre-rotation) in the display's local space.
        /// </summary>
        /// <param name="translation">Non-null vector.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="translation"/> is null.</exception>
        /// <example>
        /// <code>builder.translation(new Vector3f(0f, 0.25f, 0f));</code>
        /// </example>
        public ItemDisplayBuilder translation(Vector3f translation) {
            if (translation == null) throw new IllegalArgumentException("translation cannot be null");
            this.translation = new Vector3f(translation);
            return this;
        }

        // -------------------- Scale --------------------

        /// <summary>
        /// Sets a uniform scale (x=y=z). Ignored if <see cref="scale(Vector3f)"/> is set.
        /// </summary>
        /// <param name="uniform">Uniform factor &gt; 0.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="uniform"/> ≤ 0.</exception>
        /// <example>
        /// <code>builder.scale(0.75f);</code>
        /// </example>
        public ItemDisplayBuilder scale(float uniform) {
            if (uniform <= 0f) throw new IllegalArgumentException("Scale must be > 0");
            this.uniformScale = uniform;
            this.scale = null;
            return this;
        }

        /// <summary>
        /// Sets a non-uniform scale vector.
        /// </summary>
        /// <param name="scale">Vector with all components &gt; 0.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If any component ≤ 0 or <paramref name="scale"/> is null.</exception>
        /// <example>
        /// <code>builder.scale(new Vector3f(1.0f, 0.5f, 1.25f));</code>
        /// </example>
        public ItemDisplayBuilder scale(Vector3f scale) {
            if (scale == null) throw new IllegalArgumentException("scale cannot be null");
            if (scale.x <= 0f || scale.y <= 0f || scale.z <= 0f)
                throw new IllegalArgumentException("scale components must be > 0");
            this.scale = new Vector3f(scale);
            return this;
        }

        // -------------------- Rotation --------------------

        /// <summary>
        /// Sets the left rotation quaternion.
        /// </summary>
        /// <param name="q">Non-null quaternion.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="q"/> is null.</exception>
        /// <remarks>Left and right rotations form the dual quaternion pair in <see cref="Transformation"/>.</remarks>
        /// <example>
        /// <code>builder.leftRotation(new Quaternionf().rotateY((float)Math.toRadians(90)));</code>
        /// </example>
        public ItemDisplayBuilder leftRotation(Quaternionf q) {
            if (q == null) throw new IllegalArgumentException("leftRotation cannot be null");
            this.leftRotation = new Quaternionf(q);
            return this;
        }

        /// <summary>
        /// Sets the right rotation quaternion.
        /// </summary>
        /// <param name="q">Non-null quaternion.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="q"/> is null.</exception>
        /// <example>
        /// <code>builder.rightRotation(new Quaternionf()); // identity</code>
        /// </example>
        public ItemDisplayBuilder rightRotation(Quaternionf q) {
            if (q == null) throw new IllegalArgumentException("rightRotation cannot be null");
            this.rightRotation = new Quaternionf(q);
            return this;
        }

        /// <summary>
        /// Convenience method to set the left rotation using Euler angles in degrees.
        /// </summary>
        /// <param name="yawDeg">Rotation around Z in degrees.</param>
        /// <param name="pitchDeg">Rotation around X in degrees.</param>
        /// <param name="rollDeg">Rotation around Y in degrees.</param>
        /// <returns>This builder for chaining.</returns>
        /// <remarks>
        /// Applies rotations in the order: yaw(Z), pitch(X), roll(Y), to a fresh quaternion.
        /// </remarks>
        /// <example>
        /// <code>builder.eulerDegrees(0f, 45f, 0f);</code>
        /// </example>
        public ItemDisplayBuilder eulerDegrees(float yawDeg, float pitchDeg, float rollDeg) {
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

        /// <summary>
        /// Sets the render distance in blocks (>= 0).
        /// </summary>
        /// <param name="viewRange">Distance in blocks.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="viewRange"/> &lt; 0.</exception>
        /// <example>
        /// <code>builder.viewRange(32.0f);</code>
        /// </example>
        public ItemDisplayBuilder viewRange(float viewRange) {
            if (viewRange < 0f) throw new IllegalArgumentException("viewRange must be >= 0");
            this.viewRange = viewRange;
            return this;
        }

        /// <summary>
        /// Sets the display's shadow radius.
        /// </summary>
        /// <param name="shadowRadius">Radius in blocks (>= 0).</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="shadowRadius"/> &lt; 0.</exception>
        public ItemDisplayBuilder shadowRadius(float shadowRadius) {
            if (shadowRadius < 0f) throw new IllegalArgumentException("shadowRadius must be >= 0");
            this.shadowRadius = shadowRadius;
            return this;
        }

        /// <summary>
        /// Sets the display's shadow strength.
        /// </summary>
        /// <param name="shadowStrength">Strength (0..1); clamped when applied.</param>
        /// <returns>This builder for chaining.</returns>
        public ItemDisplayBuilder shadowStrength(float shadowStrength) {
            this.shadowStrength = shadowStrength;
            return this;
        }

        /// <summary>
        /// Sets the display height (world units).
        /// </summary>
        /// <param name="displayHeight">Height &gt; 0.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="displayHeight"/> ≤ 0.</exception>
        public ItemDisplayBuilder displayHeight(float displayHeight) {
            if (displayHeight <= 0f) throw new IllegalArgumentException("displayHeight must be > 0");
            this.displayHeight = displayHeight;
            return this;
        }

        /// <summary>
        /// Sets the display width (world units).
        /// </summary>
        /// <param name="displayWidth">Width &gt; 0.</param>
        /// <returns>This builder for chaining.</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="displayWidth"/> ≤ 0.</exception>
        public ItemDisplayBuilder displayWidth(float displayWidth) {
            if (displayWidth <= 0f) throw new IllegalArgumentException("displayWidth must be > 0");
            this.displayWidth = displayWidth;
            return this;
        }

        /// <summary>
        /// Sets the billboard mode for the display.
        /// </summary>
        /// <param name="billboard">Billboard mode, defaults to <c>CENTER</c> if null.</param>
        /// <returns>This builder for chaining.</returns>
        /// <example>
        /// <code>builder.billboard(Display.Billboard.FIXED);</code>
        /// </example>
        public ItemDisplayBuilder billboard(Display.Billboard billboard) {
            this.billboard = (billboard == null) ? Display.Billboard.CENTER : billboard;
            return this;
        }

        /// <summary>
        /// Sets the glow color override (or clears it if null).
        /// </summary>
        /// <param name="color">Glow color or null to clear.</param>
        /// <returns>This builder for chaining.</returns>
        public ItemDisplayBuilder glowColor(Color color) {
            this.itemGlowColor = color; // null clears
            return this;
        }

        /// <summary>
        /// Sets the display brightness override (or clears if null).
        /// </summary>
        /// <param name="brightness">Brightness or null for default.</param>
        /// <returns>This builder for chaining.</returns>
        public ItemDisplayBuilder brightness(Display.Brightness brightness) {
            this.itemBrightness = brightness; // null = default
            return this;
        }

        // -------------------- Spawn --------------------

        /// <summary>
        /// Spawns the <see cref="ItemDisplay"/> with the configured properties.
        /// </summary>
        /// <param name="itemStack">Item to display (must not be null or AIR).</param>
        /// <returns>The spawned <see cref="ItemDisplay"/>.</returns>
        /// <exception cref="IllegalArgumentException">If the item is null or AIR.</exception>
        /// <exception cref="IllegalStateException">If no world is available to spawn.</exception>
        /// <remarks>
        /// Emits an <see cref="ItemDisplaySpawnEvent"/> immediately after configuring the display.
        /// </remarks>
        /// <example>
        /// <code>
        /// ItemDisplay d = builder.spawn(new ItemStack(Material.EMERALD));
        /// </code>
        /// </example>
        public ItemDisplay spawn(ItemStack itemStack) {
            if (itemStack == null || itemStack.getType() == Material.AIR)
                throw new IllegalArgumentException("ItemStack cannot be null or AIR");
            if (world == null)
                throw new IllegalStateException("No world available to spawn ItemDisplay");
            if (location == null)
                location = world.getSpawnLocation();

            final ItemDisplay display = world.spawn(location, ItemDisplay.class);
            display.setItemStack(itemStack);

            // Apply transformation
            final Vector3f sc = (scale != null) ? new Vector3f(scale) : new Vector3f(uniformScale);
            final Transformation tf = new Transformation(
                    new Vector3f(translation),
                    new Quaternionf(leftRotation),
                    sc,
                    new Quaternionf(rightRotation)
            );
            display.setTransformation(tf);

            // Clamp & apply display properties
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