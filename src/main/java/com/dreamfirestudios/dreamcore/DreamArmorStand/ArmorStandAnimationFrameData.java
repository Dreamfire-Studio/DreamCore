package com.dreamfirestudios.dreamcore.DreamArmorStand;

import org.bukkit.util.EulerAngle;

/// <summary>
/// Represents a single animation frame for an armor stand, defining start and end angles
/// for each body part and the duration of the frame in ticks.
/// </summary>
/// <remarks>
/// Pass <c>null</c> for any body part you do not wish to animate in this frame.
/// </remarks>
public class ArmorStandAnimationFrameData {
    private final EulerAngle headStart;
    private final EulerAngle headEnd;
    private final EulerAngle bodyStart;
    private final EulerAngle bodyEnd;
    private final EulerAngle leftArmStart;
    private final EulerAngle leftArmEnd;
    private final EulerAngle rightArmStart;
    private final EulerAngle rightArmEnd;
    private final EulerAngle leftLegStart;
    private final EulerAngle leftLegEnd;
    private final EulerAngle rightLegStart;
    private final EulerAngle rightLegEnd;
    private final long durationTicks;

    /// <summary>
    /// Constructs a new animation frame for an armor stand.
    /// </summary>
    /// <param name="durationTicks">Duration of this frame in ticks. Must be greater than zero.</param>
    /// <param name="headStart">Starting angle for the head.</param>
    /// <param name="headEnd">Ending angle for the head.</param>
    /// <param name="bodyStart">Starting angle for the body.</param>
    /// <param name="bodyEnd">Ending angle for the body.</param>
    /// <param name="leftArmStart">Starting angle for the left arm.</param>
    /// <param name="leftArmEnd">Ending angle for the left arm.</param>
    /// <param name="rightArmStart">Starting angle for the right arm.</param>
    /// <param name="rightArmEnd">Ending angle for the right arm.</param>
    /// <param name="leftLegStart">Starting angle for the left leg.</param>
    /// <param name="leftLegEnd">Ending angle for the left leg.</param>
    /// <param name="rightLegStart">Starting angle for the right leg.</param>
    /// <param name="rightLegEnd">Ending angle for the right leg.</param>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="durationTicks"/> is less than or equal to zero.
    /// </exception>
    public ArmorStandAnimationFrameData(long durationTicks,
                                        EulerAngle headStart, EulerAngle headEnd,
                                        EulerAngle bodyStart, EulerAngle bodyEnd,
                                        EulerAngle leftArmStart, EulerAngle leftArmEnd,
                                        EulerAngle rightArmStart, EulerAngle rightArmEnd,
                                        EulerAngle leftLegStart, EulerAngle leftLegEnd,
                                        EulerAngle rightLegStart, EulerAngle rightLegEnd) {
        if (durationTicks <= 0)
            throw new IllegalArgumentException("durationTicks must be > 0");
        this.durationTicks = durationTicks;
        this.headStart = headStart;
        this.headEnd = headEnd;
        this.bodyStart = bodyStart;
        this.bodyEnd = bodyEnd;
        this.leftArmStart = leftArmStart;
        this.leftArmEnd = leftArmEnd;
        this.rightArmStart = rightArmStart;
        this.rightArmEnd = rightArmEnd;
        this.leftLegStart = leftLegStart;
        this.leftLegEnd = leftLegEnd;
        this.rightLegStart = rightLegStart;
        this.rightLegEnd = rightLegEnd;
    }

    /// <summary>
    /// Gets the duration of this frame in ticks.
    /// </summary>
    public long getDurationTicks() {
        return durationTicks;
    }

    /// <summary>Gets the starting angle for the head.</summary>
    public EulerAngle getHeadStart() { return headStart; }

    /// <summary>Gets the ending angle for the head.</summary>
    public EulerAngle getHeadEnd() { return headEnd; }

    /// <summary>Gets the starting angle for the body.</summary>
    public EulerAngle getBodyStart() { return bodyStart; }

    /// <summary>Gets the ending angle for the body.</summary>
    public EulerAngle getBodyEnd() { return bodyEnd; }

    /// <summary>Gets the starting angle for the left arm.</summary>
    public EulerAngle getLeftArmStart() { return leftArmStart; }

    /// <summary>Gets the ending angle for the left arm.</summary>
    public EulerAngle getLeftArmEnd() { return leftArmEnd; }

    /// <summary>Gets the starting angle for the right arm.</summary>
    public EulerAngle getRightArmStart() { return rightArmStart; }

    /// <summary>Gets the ending angle for the right arm.</summary>
    public EulerAngle getRightArmEnd() { return rightArmEnd; }

    /// <summary>Gets the starting angle for the left leg.</summary>
    public EulerAngle getLeftLegStart() { return leftLegStart; }

    /// <summary>Gets the ending angle for the left leg.</summary>
    public EulerAngle getLeftLegEnd() { return leftLegEnd; }

    /// <summary>Gets the starting angle for the right leg.</summary>
    public EulerAngle getRightLegStart() { return rightLegStart; }

    /// <summary>Gets the ending angle for the right leg.</summary>
    public EulerAngle getRightLegEnd() { return rightLegEnd; }
}