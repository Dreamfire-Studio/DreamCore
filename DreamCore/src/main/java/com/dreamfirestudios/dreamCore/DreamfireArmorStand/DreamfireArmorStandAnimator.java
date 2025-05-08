package com.dreamfirestudios.dreamCore.DreamfireArmorStand;

import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class DreamfireArmorStandAnimator {
    @Getter
    private UUID animatorID;
    @Getter
    private ArmorStand targetArmorStand;
    private List<ArmorStandAnimationFrameData> frames = new CopyOnWriteArrayList<>();
    private int currentFrameIndex = 0;
    private int currentFrameTick = 0;
    @Getter
    private volatile boolean paused = true;

    private DreamfireArmorStandAnimator() {}

    /**
     * Displays the next frame on the target ArmorStand.
     * This method should be called periodically (e.g., each tick) to update the animation.
     *
     * @return true if no frames are available or no target is set, false otherwise.
     */
    public boolean displayNextFrame() {
        if (frames.isEmpty() || targetArmorStand == null) return true;
        if (paused) return false;

        ArmorStandAnimationFrameData frame = frames.get(currentFrameIndex);
        // Calculate interpolation factor t (from 0.0 to 1.0)
        double t = (double) currentFrameTick / frame.getDurationTicks();
        if (t > 1.0) t = 1.0;

        // Update head pose if defined
        if (frame.getHeadStart() != null && frame.getHeadEnd() != null) {
            EulerAngle newHead = new EulerAngle(
                    interpolate(frame.getHeadStart().getX(), frame.getHeadEnd().getX(), t),
                    interpolate(frame.getHeadStart().getY(), frame.getHeadEnd().getY(), t),
                    interpolate(frame.getHeadStart().getZ(), frame.getHeadEnd().getZ(), t)
            );
            targetArmorStand.setHeadPose(newHead);
        }
        // Update body pose if defined
        if (frame.getBodyStart() != null && frame.getBodyEnd() != null) {
            EulerAngle newBody = new EulerAngle(
                    interpolate(frame.getBodyStart().getX(), frame.getBodyEnd().getX(), t),
                    interpolate(frame.getBodyStart().getY(), frame.getBodyEnd().getY(), t),
                    interpolate(frame.getBodyStart().getZ(), frame.getBodyEnd().getZ(), t)
            );
            targetArmorStand.setBodyPose(newBody);
        }
        // Update left arm pose if defined
        if (frame.getLeftArmStart() != null && frame.getLeftArmEnd() != null) {
            EulerAngle newLeftArm = new EulerAngle(
                    interpolate(frame.getLeftArmStart().getX(), frame.getLeftArmEnd().getX(), t),
                    interpolate(frame.getLeftArmStart().getY(), frame.getLeftArmEnd().getY(), t),
                    interpolate(frame.getLeftArmStart().getZ(), frame.getLeftArmEnd().getZ(), t)
            );
            targetArmorStand.setLeftArmPose(newLeftArm);
        }
        // Update right arm pose if defined
        if (frame.getRightArmStart() != null && frame.getRightArmEnd() != null) {
            EulerAngle newRightArm = new EulerAngle(
                    interpolate(frame.getRightArmStart().getX(), frame.getRightArmEnd().getX(), t),
                    interpolate(frame.getRightArmStart().getY(), frame.getRightArmEnd().getY(), t),
                    interpolate(frame.getRightArmStart().getZ(), frame.getRightArmEnd().getZ(), t)
            );
            targetArmorStand.setRightArmPose(newRightArm);
        }
        // Update left leg pose if defined
        if (frame.getLeftLegStart() != null && frame.getLeftLegEnd() != null) {
            EulerAngle newLeftLeg = new EulerAngle(
                    interpolate(frame.getLeftLegStart().getX(), frame.getLeftLegEnd().getX(), t),
                    interpolate(frame.getLeftLegStart().getY(), frame.getLeftLegEnd().getY(), t),
                    interpolate(frame.getLeftLegStart().getZ(), frame.getLeftLegEnd().getZ(), t)
            );
            targetArmorStand.setLeftLegPose(newLeftLeg);
        }
        // Update right leg pose if defined
        if (frame.getRightLegStart() != null && frame.getRightLegEnd() != null) {
            EulerAngle newRightLeg = new EulerAngle(
                    interpolate(frame.getRightLegStart().getX(), frame.getRightLegEnd().getX(), t),
                    interpolate(frame.getRightLegStart().getY(), frame.getRightLegEnd().getY(), t),
                    interpolate(frame.getRightLegStart().getZ(), frame.getRightLegEnd().getZ(), t)
            );
            targetArmorStand.setRightLegPose(newRightLeg);
        }

        // Increment the tick counter for the current frame.
        currentFrameTick++;
        if (currentFrameTick >= frame.getDurationTicks()) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            currentFrameTick = 0;
        }
        return false;
    }

    private double interpolate(double start, double end, double t) {
        return start + (end - start) * t;
    }

    /**
     * Pauses the animation.
     */
    public void pause() {
        if (!paused) paused = true;
    }

    /**
     * Resumes the animation.
     */
    public void play() {
        if (paused) paused = false;
    }

    /**
     * Stops the animation and clears all frames.
     */
    public void stop() {
        paused = true;
        frames.clear();
    }

    /**
     * A builder for constructing a DreamfireArmorStandAnimator.
     */
    public static class Builder {
        private UUID animatorID = UUID.randomUUID();
        private final List<ArmorStandAnimationFrameData> frames = new CopyOnWriteArrayList<>();
        private ArmorStand targetArmorStand;

        /**
         * Sets a custom ID for the animator.
         *
         * @param animatorID The custom UUID.
         * @return The Builder instance.
         */
        public Builder animatorID(UUID animatorID) {
            if (animatorID != null) this.animatorID = animatorID;
            return this;
        }

        /**
         * Sets the target ArmorStand to animate.
         *
         * @param armorStand The target ArmorStand.
         * @return The Builder instance.
         */
        public Builder targetArmorStand(ArmorStand armorStand) {
            if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
            this.targetArmorStand = armorStand;
            return this;
        }

        /**
         * Adds a frame to the animation with a specified number of repeats.
         *
         * @param frame        The frame to add.
         * @param repeatFrames The number of times to repeat this frame.
         * @return The Builder instance.
         */
        public Builder addFrame(ArmorStandAnimationFrameData frame, int repeatFrames) {
            if (frame == null || repeatFrames <= 0) return this;
            for (int i = 0; i < repeatFrames; i++) {
                frames.add(frame);
            }
            return this;
        }

        /**
         * Adds a single frame to the animation.
         *
         * @param frame The frame to add.
         * @return The Builder instance.
         */
        public Builder addFrame(ArmorStandAnimationFrameData frame) {
            if (frame != null) frames.add(frame);
            return this;
        }

        /**
         * Builds and returns the DreamfireArmorStandAnimator.
         *
         * @return The constructed DreamfireArmorStandAnimator.
         */
        public DreamfireArmorStandAnimator build() {
            if (targetArmorStand == null)
                throw new IllegalArgumentException("Target ArmorStand must be set");
            if (frames.isEmpty())
                throw new IllegalArgumentException("At least one frame must be added");
            DreamfireArmorStandAnimator animator = new DreamfireArmorStandAnimator();
            animator.animatorID = animatorID;
            animator.targetArmorStand = targetArmorStand;
            animator.frames = frames;
            return animator;
        }
    }
}