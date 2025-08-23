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

import org.bukkit.util.EulerAngle;

/// <summary>
/// Builder class for creating an instance of <see cref="ArmorStandAnimationFrameData"/>.
/// </summary>
/// <remarks>
/// This builder allows configuration of animation frame duration and start/end angles
/// for each armor stand body part.
/// Pass <c>null</c> to skip animating a specific body part in the frame.
/// </remarks>
/// <example>
/// Example usage:
/// ```java
/// ArmorStandAnimationFrameData frame = new ArmorStandAnimationFrameDataBuilder()
///     .duration(40)
///     .head(new EulerAngle(0, 0, 0), new EulerAngle(Math.toRadians(30), 0, 0))
///     .leftArm(new EulerAngle(0, 0, 0), new EulerAngle(0, Math.toRadians(45), 0))
///     .build();
/// ```
/// </example>
public class ArmorStandAnimationFrameDataBuilder {

    private long durationTicks = 20;
    private EulerAngle headStart;
    private EulerAngle headEnd;
    private EulerAngle bodyStart;
    private EulerAngle bodyEnd;
    private EulerAngle leftArmStart;
    private EulerAngle leftArmEnd;
    private EulerAngle rightArmStart;
    private EulerAngle rightArmEnd;
    private EulerAngle leftLegStart;
    private EulerAngle leftLegEnd;
    private EulerAngle rightLegStart;
    private EulerAngle rightLegEnd;

    /// <summary>
    /// Sets the duration (in ticks) for this frame.
    /// </summary>
    /// <param name="durationTicks">The duration in ticks; must be greater than zero.</param>
    /// <returns>This builder instance for chaining.</returns>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="durationTicks"/> is less than or equal to zero.
    /// </exception>
    public ArmorStandAnimationFrameDataBuilder duration(long durationTicks) {
        if (durationTicks <= 0) throw new IllegalArgumentException("durationTicks must be > 0");
        this.durationTicks = durationTicks;
        return this;
    }

    /// <summary>
    /// Sets the start and end angles for the head.
    /// </summary>
    /// <param name="start">The starting <see cref="EulerAngle"/> for the head.</param>
    /// <param name="end">The ending <see cref="EulerAngle"/> for the head.</param>
    /// <returns>This builder instance for chaining.</returns>
    public ArmorStandAnimationFrameDataBuilder head(EulerAngle start, EulerAngle end) {
        this.headStart = start;
        this.headEnd = end;
        return this;
    }

    /// <summary>
    /// Sets the start and end angles for the body.
    /// </summary>
    /// <param name="start">The starting <see cref="EulerAngle"/> for the body.</param>
    /// <param name="end">The ending <see cref="EulerAngle"/> for the body.</param>
    /// <returns>This builder instance for chaining.</returns>
    public ArmorStandAnimationFrameDataBuilder body(EulerAngle start, EulerAngle end) {
        this.bodyStart = start;
        this.bodyEnd = end;
        return this;
    }

    /// <summary>
    /// Sets the start and end angles for the left arm.
    /// </summary>
    /// <param name="start">The starting <see cref="EulerAngle"/> for the left arm.</param>
    /// <param name="end">The ending <see cref="EulerAngle"/> for the left arm.</param>
    /// <returns>This builder instance for chaining.</returns>
    public ArmorStandAnimationFrameDataBuilder leftArm(EulerAngle start, EulerAngle end) {
        this.leftArmStart = start;
        this.leftArmEnd = end;
        return this;
    }

    /// <summary>
    /// Sets the start and end angles for the right arm.
    /// </summary>
    /// <param name="start">The starting <see cref="EulerAngle"/> for the right arm.</param>
    /// <param name="end">The ending <see cref="EulerAngle"/> for the right arm.</param>
    /// <returns>This builder instance for chaining.</returns>
    public ArmorStandAnimationFrameDataBuilder rightArm(EulerAngle start, EulerAngle end) {
        this.rightArmStart = start;
        this.rightArmEnd = end;
        return this;
    }

    /// <summary>
    /// Sets the start and end angles for the left leg.
    /// </summary>
    /// <param name="start">The starting <see cref="EulerAngle"/> for the left leg.</param>
    /// <param name="end">The ending <see cref="EulerAngle"/> for the left leg.</param>
    /// <returns>This builder instance for chaining.</returns>
    public ArmorStandAnimationFrameDataBuilder leftLeg(EulerAngle start, EulerAngle end) {
        this.leftLegStart = start;
        this.leftLegEnd = end;
        return this;
    }

    /// <summary>
    /// Sets the start and end angles for the right leg.
    /// </summary>
    /// <param name="start">The starting <see cref="EulerAngle"/> for the right leg.</param>
    /// <param name="end">The ending <see cref="EulerAngle"/> for the right leg.</param>
    /// <returns>This builder instance for chaining.</returns>
    public ArmorStandAnimationFrameDataBuilder rightLeg(EulerAngle start, EulerAngle end) {
        this.rightLegStart = start;
        this.rightLegEnd = end;
        return this;
    }

    /// <summary>
    /// Builds and returns a new <see cref="ArmorStandAnimationFrameData"/> instance
    /// configured with the provided parameters.
    /// </summary>
    /// <returns>A new <see cref="ArmorStandAnimationFrameData"/>.</returns>
    public ArmorStandAnimationFrameData build() {
        return new ArmorStandAnimationFrameData(
                durationTicks,
                headStart, headEnd,
                bodyStart, bodyEnd,
                leftArmStart, leftArmEnd,
                rightArmStart, rightArmEnd,
                leftLegStart, leftLegEnd,
                rightLegStart, rightLegEnd
        );
    }
}