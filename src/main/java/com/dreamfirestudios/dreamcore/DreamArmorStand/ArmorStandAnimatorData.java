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
package com.dreamfirestudios.dreamcore.DreamArmorStand;

import org.bukkit.entity.ArmorStand;
import java.util.function.Consumer;

/// <summary>
/// Represents a single frame of an armor stand animation.
/// </summary>
/// <remarks>
/// A frame is defined by a <see cref="Consumer{ArmorStand}"/> action
/// that is executed when the frame is displayed, allowing custom
/// transformations or modifications to the armor stand.
/// </remarks>
/// <example>
/// Example usage:
/// ```java
/// ArmorStandAnimatorData frame = new ArmorStandAnimatorData(stand -> {
///     stand.setHeadPose(new EulerAngle(Math.toRadians(30), 0, 0));
/// });
///
/// frame.displayFrame(myArmorStand);
/// ```
/// </example>
public class ArmorStandAnimatorData {
    private final Consumer<ArmorStand> frameAction;

    /// <summary>
    /// Initializes a new instance of the <see cref="ArmorStandAnimatorData"/> class.
    /// </summary>
    /// <param name="frameAction">
    /// The action to apply to the <see cref="ArmorStand"/> when this frame is displayed.
    /// </param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="frameAction"/> is <c>null</c>.
    /// </exception>
    public ArmorStandAnimatorData(Consumer<ArmorStand> frameAction) {
        if (frameAction == null) throw new IllegalArgumentException("Frame action cannot be null");
        this.frameAction = frameAction;
    }

    /// <summary>
    /// Displays this animation frame by applying its action to the specified armor stand.
    /// </summary>
    /// <param name="armorStand">The armor stand to update.</param>
    public void displayFrame(ArmorStand armorStand) {
        frameAction.accept(armorStand);
    }
}