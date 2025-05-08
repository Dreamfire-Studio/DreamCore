package com.dreamfirestudios.dreamCore.DreamfireItemDisplay;

import com.dreamfirestudios.dreamCore.DreamfireItemDisplay.Event.ItemDisplaySpawnEvent;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public class DreamfireItemDisplay {
    public static class ItemDisplayBuilder {
        private World world = Bukkit.getWorld("world");
        private Location location = world.getSpawnLocation();
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

        /**
         * Set the world where the display will spawn.
         *
         * @param world The world where the display will appear.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the world is null.
         */
        public ItemDisplayBuilder world(World world) {
            if (world == null) throw new IllegalArgumentException("World cannot be null");
            this.world = world;
            return this;
        }

        /**
         * Set the spawn location of the display.
         *
         * @param location The location to spawn the display.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the location is null.
         */
        public ItemDisplayBuilder location(Location location) {
            if (location == null) throw new IllegalArgumentException("Location cannot be null");
            this.location = location;
            return this;
        }

        /**
         * Set the scale of the displayed item.
         *
         * @param itemScale The scale of the item.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the scale is less than or equal to zero.
         */
        public ItemDisplayBuilder itemScale(double itemScale) {
            if (itemScale <= 0) throw new IllegalArgumentException("Item scale must be greater than zero");
            this.itemScale = itemScale;
            return this;
        }

        /**
         * Set the rotation of the item display.
         *
         * @param leftRotation The rotation vector to apply to the display.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the rotation vector is null.
         */
        public ItemDisplayBuilder leftRotation(Vector3f leftRotation) {
            if (leftRotation == null) throw new IllegalArgumentException("Rotation vector cannot be null");
            this.leftRotation = leftRotation;
            return this;
        }

        /**
         * Set the view range of the display. (0.1 = 16 blocks).
         *
         * @param viewRange The view range to set.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the view range is negative.
         */
        public ItemDisplayBuilder viewRange(float viewRange) {
            if (viewRange < 0) throw new IllegalArgumentException("View range must be non-negative");
            this.viewRange = viewRange;
            return this;
        }

        /**
         * Set the shadow radius of the display.
         *
         * @param shadowRadius The shadow radius.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the shadow radius is negative.
         */
        public ItemDisplayBuilder shadowRadius(float shadowRadius) {
            if (shadowRadius < 0) throw new IllegalArgumentException("Shadow radius must be non-negative");
            this.shadowRadius = shadowRadius;
            return this;
        }

        /**
         * Set the shadow strength of the display.
         *
         * @param shadowStrength The shadow strength.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the shadow strength is negative.
         */
        public ItemDisplayBuilder shadowStrength(float shadowStrength) {
            if (shadowStrength < 0) throw new IllegalArgumentException("Shadow strength must be non-negative");
            this.shadowStrength = shadowStrength;
            return this;
        }

        /**
         * Set the display height.
         *
         * @param displayHeight The display height.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the display height is zero or negative.
         */
        public ItemDisplayBuilder displayHeight(float displayHeight) {
            if (displayHeight <= 0) throw new IllegalArgumentException("Display height must be greater than zero");
            this.displayHeight = displayHeight;
            return this;
        }

        /**
         * Set the display width.
         *
         * @param displayWidth The display width.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the display width is zero or negative.
         */
        public ItemDisplayBuilder displayWidth(float displayWidth) {
            if (displayWidth <= 0) throw new IllegalArgumentException("Display width must be greater than zero");
            this.displayWidth = displayWidth;
            return this;
        }

        /**
         * Set the billboard type.
         *
         * @param billboard The billboard type to use.
         * @return The current ItemDisplayBuilder instance.
         */
        public ItemDisplayBuilder billboard(Display.Billboard billboard) {
            this.billboard = billboard;
            return this;
        }

        /**
         * Set the glow color of the item display.
         *
         * @param itemGlowColor The color to apply to the glow effect.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the glow color is null.
         */
        public ItemDisplayBuilder itemGlowColor(Color itemGlowColor) {
            if (itemGlowColor == null) throw new IllegalArgumentException("Glow color cannot be null");
            this.itemGlowColor = itemGlowColor;
            return this;
        }

        /**
         * Set the brightness of the item display.
         *
         * @param itemBrightness The brightness to apply to the display.
         * @return The current ItemDisplayBuilder instance.
         * @throws IllegalArgumentException If the brightness is null.
         */
        public ItemDisplayBuilder itemBrightness(Display.Brightness itemBrightness) {
            if (itemBrightness == null) throw new IllegalArgumentException("Brightness cannot be null");
            this.itemBrightness = itemBrightness;
            return this;
        }

        /**
         * Spawn the item display with the specified properties.
         *
         * @param itemStack The item to display.
         * @return The spawned ItemDisplay.
         * @throws IllegalArgumentException If the itemStack is null or AIR.
         */
        public ItemDisplay spawn(ItemStack itemStack) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                throw new IllegalArgumentException("ItemStack cannot be null or AIR");
            }

            var itemDisplay = world.spawn(location, ItemDisplay.class);
            itemDisplay.setItemStack(itemStack);

            var transformation = itemDisplay.getTransformation();
            transformation.getScale().set(itemScale);
            transformation.getLeftRotation().x = leftRotation.x;
            transformation.getLeftRotation().y = leftRotation.y;
            transformation.getLeftRotation().z = leftRotation.z;
            itemDisplay.setTransformation(transformation);

            itemDisplay.setViewRange(viewRange);
            itemDisplay.setShadowRadius(shadowRadius); // Correct method
            itemDisplay.setShadowStrength(shadowStrength);
            itemDisplay.setDisplayHeight(displayHeight);
            itemDisplay.setDisplayWidth(displayWidth);
            itemDisplay.setBillboard(billboard);
            itemDisplay.setGlowColorOverride(itemGlowColor);
            itemDisplay.setBrightness(itemBrightness);

            new ItemDisplaySpawnEvent(itemDisplay);
            return itemDisplay;
        }
    }
}
