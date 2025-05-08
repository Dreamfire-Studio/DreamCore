package com.dreamfirestudios.dreamCore.DreamfireArmorStand;

import org.bukkit.util.EulerAngle;

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
    private final long durationTicks; // duration for this frame

    /**
     * Constructs a new frame. Pass null for any body part you do not wish to animate.
     *
     * @param durationTicks duration in ticks for this frame.
     * @param headStart starting angle for head
     * @param headEnd ending angle for head
     * @param bodyStart starting angle for body
     * @param bodyEnd ending angle for body
     * @param leftArmStart starting angle for left arm
     * @param leftArmEnd ending angle for left arm
     * @param rightArmStart starting angle for right arm
     * @param rightArmEnd ending angle for right arm
     * @param leftLegStart starting angle for left leg
     * @param leftLegEnd ending angle for left leg
     * @param rightLegStart starting angle for right leg
     * @param rightLegEnd ending angle for right leg
     */
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

    public long getDurationTicks() {
        return durationTicks;
    }

    public EulerAngle getHeadStart() {
        return headStart;
    }

    public EulerAngle getHeadEnd() {
        return headEnd;
    }

    public EulerAngle getBodyStart() {
        return bodyStart;
    }

    public EulerAngle getBodyEnd() {
        return bodyEnd;
    }

    public EulerAngle getLeftArmStart() {
        return leftArmStart;
    }

    public EulerAngle getLeftArmEnd() {
        return leftArmEnd;
    }

    public EulerAngle getRightArmStart() {
        return rightArmStart;
    }

    public EulerAngle getRightArmEnd() {
        return rightArmEnd;
    }

    public EulerAngle getLeftLegStart() {
        return leftLegStart;
    }

    public EulerAngle getLeftLegEnd() {
        return leftLegEnd;
    }

    public EulerAngle getRightLegStart() {
        return rightLegStart;
    }

    public EulerAngle getRightLegEnd() {
        return rightLegEnd;
    }
}