package com.dreamfirestudios.dreamCore.DreamfireBlockDisplay;

import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Utility class providing animation support for BlockDisplay entities.
 */
public class BlockDisplayAnimator {

    /**
     * Animates the scale of a BlockDisplay by incrementing its scale vector on each tick.
     *
     * @param display        The BlockDisplay to animate.
     * @param scaleIncrement The amount to add to the scale per tick.
     * @param period         The period in ticks for the repeating task.
     * @param plugin         Your plugin instance.
     * @return The task ID for the scheduled animation.
     */
    public static int animateScale(BlockDisplay display, Vector3f scaleIncrement, long period, JavaPlugin plugin) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            var transformation = display.getTransformation();
            transformation.getScale().add(scaleIncrement);
            display.setTransformation(transformation);
        }, period, period).getTaskId();
    }

    /**
     * Animates a BlockDisplay with a custom transformation on each tick.
     *
     * @param display  The BlockDisplay to animate.
     * @param animator A Consumer that applies custom changes to the transformation.
     * @param period   The period in ticks for the repeating task.
     * @param plugin   Your plugin instance.
     * @return The task ID for the scheduled animation.
     */
    public static int animateCustom(BlockDisplay display, Consumer<Transformation> animator, long period, JavaPlugin plugin) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            var transformation = display.getTransformation();
            animator.accept(transformation);
            display.setTransformation(transformation);
        }, period, period).getTaskId();
    }
}