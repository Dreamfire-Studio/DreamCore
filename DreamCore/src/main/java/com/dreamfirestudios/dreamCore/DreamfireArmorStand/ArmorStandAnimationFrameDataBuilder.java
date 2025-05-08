package com.dreamfirestudios.dreamCore.DreamfireArmorStand;

import org.bukkit.util.EulerAngle;

/**
 * Builder class for creating an ArmorStandAnimationFrameData instance.
 */
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

    /**
     * Sets the duration (in ticks) for this frame.
     *
     * @param durationTicks The duration in ticks; must be greater than 0.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder duration(long durationTicks) {
        if (durationTicks <= 0) throw new IllegalArgumentException("durationTicks must be > 0");
        this.durationTicks = durationTicks;
        return this;
    }

    /**
     * Sets the start and end angles for the head.
     *
     * @param start The starting EulerAngle for the head.
     * @param end   The ending EulerAngle for the head.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder head(EulerAngle start, EulerAngle end) {
        this.headStart = start;
        this.headEnd = end;
        return this;
    }

    /**
     * Sets the start and end angles for the body.
     *
     * @param start The starting EulerAngle for the body.
     * @param end   The ending EulerAngle for the body.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder body(EulerAngle start, EulerAngle end) {
        this.bodyStart = start;
        this.bodyEnd = end;
        return this;
    }

    /**
     * Sets the start and end angles for the left arm.
     *
     * @param start The starting EulerAngle for the left arm.
     * @param end   The ending EulerAngle for the left arm.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder leftArm(EulerAngle start, EulerAngle end) {
        this.leftArmStart = start;
        this.leftArmEnd = end;
        return this;
    }

    /**
     * Sets the start and end angles for the right arm.
     *
     * @param start The starting EulerAngle for the right arm.
     * @param end   The ending EulerAngle for the right arm.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder rightArm(EulerAngle start, EulerAngle end) {
        this.rightArmStart = start;
        this.rightArmEnd = end;
        return this;
    }

    /**
     * Sets the start and end angles for the left leg.
     *
     * @param start The starting EulerAngle for the left leg.
     * @param end   The ending EulerAngle for the left leg.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder leftLeg(EulerAngle start, EulerAngle end) {
        this.leftLegStart = start;
        this.leftLegEnd = end;
        return this;
    }

    /**
     * Sets the start and end angles for the right leg.
     *
     * @param start The starting EulerAngle for the right leg.
     * @param end   The ending EulerAngle for the right leg.
     * @return This builder instance.
     */
    public ArmorStandAnimationFrameDataBuilder rightLeg(EulerAngle start, EulerAngle end) {
        this.rightLegStart = start;
        this.rightLegEnd = end;
        return this;
    }

    /**
     * Builds and returns the ArmorStandAnimationFrameData instance.
     *
     * @return A new ArmorStandAnimationFrameData configured with the provided parameters.
     */
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