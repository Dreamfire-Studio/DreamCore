package com.dreamfirestudios.dreamCore.DreamfireTextDisplay;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfireTextDisplay.Events.TextDisplaySpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

public class DreamfireTextDisplay {
    public static TextDisplayBuilder BlockDisplayBuilder(){return new TextDisplayBuilder();}
    public static class TextDisplayBuilder{
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
        private Color backgroundColor = Color.RED;
        private int lineWidth = 50;
        private Byte textOpacity = Byte.MAX_VALUE;


        /**
         * World ti display in!
         * @param world
         * @return
         */
        public TextDisplayBuilder world(World world){
            this.world = world;
            return this;
        }

        /**
         * Location to spawn at
         * @param location
         * @return
         */
        public TextDisplayBuilder location(Location location){
            this.location = location;
            return this;
        }

        /**
         * Item display scale
         * @param itemScale
         * @return
         */
        public TextDisplayBuilder itemScale(double itemScale){
            this.itemScale = itemScale;
            return this;
        }

        /**
         * x: 1 to -1, forward/backward lay
         * y: 1 to -1, horizontal rotation
         * z: 1 to -1, right/left tilt
         * @param leftRotation
         * @return
         */
        public TextDisplayBuilder leftRotation(Vector3f leftRotation){
            this.leftRotation = leftRotation;
            return this;
        }

        /**
         * 0.1 = 16 blocks
         * @param viewRange
         * @return
         */
        public TextDisplayBuilder viewRange(float viewRange){
            this.viewRange = viewRange;
            return this;
        }

        /**
         *  1 = 1 block
         * @param shadowRadius
         * @return
         */
        public TextDisplayBuilder shadowRadius(float shadowRadius){
            this.shadowRadius = shadowRadius;
            return this;
        }

        /**
         * >= 5F = "black hole"
         * @param shadowStrength
         * @return
         */
        public TextDisplayBuilder shadowStrength(float shadowStrength){
            this.shadowStrength = shadowStrength;
            return this;
        }

        public TextDisplayBuilder displayHeight(float displayHeight){
            this.displayHeight = displayHeight;
            return this;
        }

        public TextDisplayBuilder displayWidth(float displayWidth){
            this.displayWidth = displayWidth;
            return this;
        }

        public TextDisplayBuilder billboard(Display.Billboard billboard){
            this.billboard = billboard;
            return this;
        }

        public TextDisplayBuilder itemGlowColor(Color itemGlowColor){
            this.itemGlowColor = itemGlowColor;
            return this;
        }

        public TextDisplayBuilder backgroundColor(Color backgroundColor){
            this.backgroundColor = backgroundColor;
            return this;
        }

        public TextDisplayBuilder lineWidth(int lineWidth){
            this.lineWidth = lineWidth;
            return this;
        }

        /**
         * 0-15, will override auto brightness
         * @param itemBrightness
         * @return
         */
        public TextDisplayBuilder itemBrightness(Display.Brightness itemBrightness){
            this.itemBrightness = itemBrightness;
            return this;
        }

        public TextDisplayBuilder textOpacity(Byte textOpacity){
            this.textOpacity = textOpacity;
            return this;
        }

        public TextDisplay Spawn(String text){
            var textDisplay = world.spawn(location, TextDisplay.class);
            textDisplay.text(DreamfireMessage.formatMessage(text));

            var transformation = textDisplay.getTransformation();
            transformation.getScale().set(itemScale);
            transformation.getLeftRotation().x = leftRotation.x;
            transformation.getLeftRotation().y = leftRotation.y;
            transformation.getLeftRotation().z = leftRotation.z;
            textDisplay.setTransformation(transformation);

            textDisplay.setViewRange(viewRange);
            textDisplay.setShadowStrength(shadowRadius);
            textDisplay.setShadowStrength(shadowStrength);
            textDisplay.setDisplayHeight(displayHeight);
            textDisplay.setDisplayWidth(displayWidth);
            textDisplay.setBillboard(billboard);
            textDisplay.setGlowColorOverride(itemGlowColor);
            textDisplay.setBrightness(itemBrightness);
            textDisplay.setBackgroundColor(backgroundColor);
            textDisplay.setLineWidth(lineWidth);
            textDisplay.setTextOpacity(textOpacity);

            new TextDisplaySpawnEvent(textDisplay);
            return textDisplay;
        }
    }
}
