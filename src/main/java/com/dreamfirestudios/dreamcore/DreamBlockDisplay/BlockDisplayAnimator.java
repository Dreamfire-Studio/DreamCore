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

import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

import java.util.function.Consumer;

/// <summary>
/// Utility class providing simple animation helpers for <see cref="BlockDisplay"/> entities
/// using scheduled server ticks.
/// </summary>
/// <remarks>
/// These helpers mutate the display’s <see cref="Transformation"/> in place each tick.
/// Call <c>Bukkit.getScheduler().cancelTask(taskId)</c> to stop a scheduled animation.
/// </remarks>
public final class BlockDisplayAnimator {

    private BlockDisplayAnimator() {}

    /// <summary>
    /// Animates the scale of a <see cref="BlockDisplay"/> by incrementing its scale vector each tick.
    /// </summary>
    /// <param name="display">The <see cref="BlockDisplay"/> to animate.</param>
    /// <param name="scaleIncrement">Amount added to the scale per tick (x,y,z).</param>
    /// <param name="period">Tick period between updates (e.g., 1 = every tick).</param>
    /// <param name="plugin">Your plugin instance used for scheduling.</param>
    /// <returns>The Bukkit scheduler task ID for the repeating animation.</returns>
    /// <example>
    /// ```java
    /// int taskId = BlockDisplayAnimator.animateScale(display, new Vector3f(0.01f, 0.01f, 0.01f), 1L, plugin);
    /// // Later: Bukkit.getScheduler().cancelTask(taskId);
    /// ```
    /// </example>
    public static int animateScale(BlockDisplay display, org.joml.Vector3f scaleIncrement, long period, JavaPlugin plugin) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Transformation transformation = display.getTransformation();
            transformation.getScale().add(scaleIncrement);
            display.setTransformation(transformation);
        }, period, period).getTaskId();
    }

    /// <summary>
    /// Animates a <see cref="BlockDisplay"/> by applying a custom transformation function each tick.
    /// </summary>
    /// <param name="display">The <see cref="BlockDisplay"/> to animate.</param>
    /// <param name="animator">A function that mutates the current <see cref="Transformation"/>.</param>
    /// <param name="period">Tick period between updates (e.g., 1 = every tick).</param>
    /// <param name="plugin">Your plugin instance used for scheduling.</param>
    /// <returns>The Bukkit scheduler task ID for the repeating animation.</returns>
    /// <remarks>
    /// The provided <paramref name="animator"/> receives the current transformation each tick and can mutate
    /// translation, rotation, and scale as needed before it is reapplied to the display.
    /// </remarks>
    /// <example>
    /// ```java
    /// int taskId = BlockDisplayAnimator.animateCustom(display, t -> {
    ///     // rotate a little around Y each tick
    ///     t.getLeftRotation().rotateY((float) Math.toRadians(2.0));
    /// }, 1L, plugin);
    /// // Later: Bukkit.getScheduler().cancelTask(taskId);
    /// ```
    /// </example>
    public static int animateCustom(BlockDisplay display, Consumer<Transformation> animator, long period, JavaPlugin plugin) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Transformation transformation = display.getTransformation();
            animator.accept(transformation);
            display.setTransformation(transformation);
        }, period, period).getTaskId();
    }
}