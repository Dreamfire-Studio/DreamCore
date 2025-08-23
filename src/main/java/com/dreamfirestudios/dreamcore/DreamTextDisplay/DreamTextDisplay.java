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
package com.dreamfirestudios.dreamcore.DreamTextDisplay;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Builder utility for spawning configured {@link TextDisplay} entities.
 *
 * <p>Notes:
 * <ul>
 *   <li>All entity spawning and property calls must occur on the main thread.</li>
 *   <li>Brightness uses block/sky light levels (0-15). Setting a brightness overrides auto brightness.</li>
 *   <li>Rotation uses the left-rotation quaternion; here we fill its xyz components from a Vector3f
 *       for convenience. Provide your own quaternion if you need exact control.</li>
 * </ul>
 */
public final class DreamTextDisplay {

    private DreamTextDisplay() {}

    /** Preferred factory name. */
    public static TextDisplayBuilder textDisplay() { return new TextDisplayBuilder(); }

    /** Backward-compatible alias (was misnamed previously). */
    public static TextDisplayBuilder BlockDisplayBuilder() { return new TextDisplayBuilder(); }

    public static final class TextDisplayBuilder {
        // -------- defaults (safe fallbacks) --------
        private World world = firstLoadedWorld();
        private Location location = world.getSpawnLocation();

        // Display transform/appearance
        private double scale = 1.0D;
        private Vector3f leftRotation = new Vector3f(0f, 0f, 0f);
        private float viewRange = 0.1f;       // 0.1 ~= 16 blocks
        private float shadowRadius = 0.3f;    // 1.0 ~= 1 block
        private float shadowStrength = 5f;    // >=5 looks very dark
        private float displayWidth = 50f;
        private float displayHeight = 50f;
        private Display.Billboard billboard = Display.Billboard.CENTER;
        private Color glowColor = Color.RED;
        private Display.Brightness brightness = new Display.Brightness(15, 15); // force max block/sky

        // Text styling
        private Color backgroundColor = Color.RED;
        private int lineWidth = 50;
        private byte textOpacity = (byte) 0xFF; // 0..255

        private static @NotNull World firstLoadedWorld() {
            return Bukkit.getWorlds().isEmpty()
                    ? Bukkit.createWorld(new WorldCreator("world")) // last-resort create
                    : Bukkit.getWorlds().get(0);
        }

        // ---------------------------------------------------------------------
        // Fluent configuration
        // ---------------------------------------------------------------------

        /** Sets the world where the display will be spawned. */
        public TextDisplayBuilder world(@NotNull World world) {
            this.world = java.util.Objects.requireNonNull(world, "world");
            if (this.location == null || this.location.getWorld() == null) {
                this.location = world.getSpawnLocation();
            }
            return this;
        }

        /** Sets the spawn location (world taken from the location). */
        public TextDisplayBuilder location(@NotNull Location location) {
            this.location = java.util.Objects.requireNonNull(location, "location");
            if (this.location.getWorld() != null) {
                this.world = this.location.getWorld();
            }
            return this;
        }

        /** Uniform scale applied to the text display transform. Must be > 0. */
        public TextDisplayBuilder itemScale(double scale) {
            if (scale <= 0) throw new IllegalArgumentException("Scale must be > 0");
            this.scale = scale;
            return this;
        }

        /**
         * Left-rotation convenience vector (mapped into the underlying quaternion's xyz components).
         * x: forward/back tilt, y: yaw, z: roll.
         */
        public TextDisplayBuilder leftRotation(@NotNull Vector3f leftRotation) {
            this.leftRotation = java.util.Objects.requireNonNull(leftRotation, "leftRotation");
            return this;
        }

        /** View range; 0.1 ~ 16 blocks. Non-negative. */
        public TextDisplayBuilder viewRange(float viewRange) {
            if (viewRange < 0f) throw new IllegalArgumentException("viewRange must be >= 0");
            this.viewRange = viewRange;
            return this;
        }

        /** Shadow radius in blocks. Non-negative. */
        public TextDisplayBuilder shadowRadius(float shadowRadius) {
            if (shadowRadius < 0f) throw new IllegalArgumentException("shadowRadius must be >= 0");
            this.shadowRadius = shadowRadius;
            return this;
        }

        /** Shadow strength; higher = darker. Non-negative. */
        public TextDisplayBuilder shadowStrength(float shadowStrength) {
            if (shadowStrength < 0f) throw new IllegalArgumentException("shadowStrength must be >= 0");
            this.shadowStrength = shadowStrength;
            return this;
        }

        public TextDisplayBuilder displayHeight(float displayHeight) {
            if (displayHeight <= 0f) throw new IllegalArgumentException("displayHeight must be > 0");
            this.displayHeight = displayHeight;
            return this;
        }

        public TextDisplayBuilder displayWidth(float displayWidth) {
            if (displayWidth <= 0f) throw new IllegalArgumentException("displayWidth must be > 0");
            this.displayWidth = displayWidth;
            return this;
        }

        public TextDisplayBuilder billboard(@NotNull Display.Billboard billboard) {
            this.billboard = java.util.Objects.requireNonNull(billboard, "billboard");
            return this;
        }

        public TextDisplayBuilder itemGlowColor(@NotNull Color glowColor) {
            this.glowColor = java.util.Objects.requireNonNull(glowColor, "glowColor");
            return this;
        }

        public TextDisplayBuilder backgroundColor(@NotNull Color backgroundColor) {
            this.backgroundColor = java.util.Objects.requireNonNull(backgroundColor, "backgroundColor");
            return this;
        }

        public TextDisplayBuilder lineWidth(int lineWidth) {
            if (lineWidth < 0) throw new IllegalArgumentException("lineWidth must be >= 0");
            this.lineWidth = lineWidth;
            return this;
        }

        /**
         * Sets explicit brightness (block, sky). Each value 0..15.
         * Passing this overrides automatic brightness.
         */
        public TextDisplayBuilder itemBrightness(@NotNull Display.Brightness brightness) {
            this.brightness = java.util.Objects.requireNonNull(brightness, "brightness");
            return this;
        }

        /** Sets text opacity (0..255). */
        public TextDisplayBuilder textOpacity(int opacity0to255) {
            if (opacity0to255 < 0 || opacity0to255 > 255) {
                throw new IllegalArgumentException("textOpacity must be in range 0..255");
            }
            this.textOpacity = (byte) (opacity0to255 & 0xFF);
            return this;
        }

        // ---------------------------------------------------------------------
        // Spawning
        // ---------------------------------------------------------------------

        /**
         * Spawns a {@link TextDisplay} with the configured properties and the given MiniMessage text.
         *
         * @param text MiniMessage-formatted text (processed through {@link DreamMessageFormatter}).
         * @return the spawned {@link TextDisplay}
         */
        public @NotNull TextDisplay spawn(@NotNull String text) {
            java.util.Objects.requireNonNull(text, "text");
            if (world == null) throw new IllegalStateException("World is null");
            if (location == null || location.getWorld() == null) {
                throw new IllegalStateException("Location/world must be set before spawning");
            }

            final TextDisplay td = world.spawn(location, TextDisplay.class);

            // content
            td.text(DreamMessageFormatter.format(text, DreamMessageSettings.all()));

            // transform
            var t = td.getTransformation();
            t.getScale().set(scale);
            // Fill quaternion components from provided vector (convenience approach)
            t.getLeftRotation().x = leftRotation.x;
            t.getLeftRotation().y = leftRotation.y;
            t.getLeftRotation().z = leftRotation.z;
            td.setTransformation(t);

            // display params
            td.setViewRange(viewRange);
            td.setShadowRadius(shadowRadius);       // fixed: radius setter
            td.setShadowStrength(shadowStrength);
            td.setDisplayHeight(displayHeight);
            td.setDisplayWidth(displayWidth);
            td.setBillboard(billboard);
            td.setGlowColorOverride(glowColor);
            td.setBrightness(brightness);
            td.setBackgroundColor(backgroundColor);
            td.setLineWidth(lineWidth);
            td.setTextOpacity(textOpacity);

            new TextDisplaySpawnEvent(td);
            return td;
        }
    }
}