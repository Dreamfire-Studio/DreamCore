package com.dreamfirestudios.dreamcore.DreamArmorStand;

import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/// <summary>
/// Provides functionality to animate an <see cref="ArmorStand"/> by interpolating
/// between <see cref="ArmorStandAnimationFrameData"/> frames over time.
/// </summary>
/// <remarks>
/// Call <see cref="displayNextFrame"/> periodically (e.g., each server tick) to update the animation.
/// Use the <see cref="Builder"/> to construct an instance.
/// </remarks>
public class DreamArmorStandAnimator {
    @Getter
    private UUID animatorID;
    @Getter
    private ArmorStand targetArmorStand;
    private List<ArmorStandAnimationFrameData> frames = new CopyOnWriteArrayList<>();
    private int currentFrameIndex = 0;
    private int currentFrameTick = 0;
    @Getter
    private volatile boolean paused = true;

    private DreamArmorStandAnimator() {
    }

    /// <summary>
    /// Displays the next frame on the target armor stand.
    /// </summary>
    /// <remarks>
    /// This method should be called periodically (e.g., once per tick) to update the animation state.
    /// </remarks>
    /// <returns>
    /// <c>true</c> if no frames are available or no target is set;
    /// <c>false</c> otherwise.
    /// </returns>
    public boolean displayNextFrame() {
        if (frames.isEmpty() || targetArmorStand == null) return true;
        if (paused) return false;

        ArmorStandAnimationFrameData frame = frames.get(currentFrameIndex);
        double t = (double) currentFrameTick / frame.getDurationTicks();
        if (t > 1.0) t = 1.0;

        if (frame.getHeadStart() != null && frame.getHeadEnd() != null) {
            EulerAngle newHead = new EulerAngle(
                    interpolate(frame.getHeadStart().getX(), frame.getHeadEnd().getX(), t),
                    interpolate(frame.getHeadStart().getY(), frame.getHeadEnd().getY(), t),
                    interpolate(frame.getHeadStart().getZ(), frame.getHeadEnd().getZ(), t)
            );
            targetArmorStand.setHeadPose(newHead);
        }

        if (frame.getBodyStart() != null && frame.getBodyEnd() != null) {
            EulerAngle newBody = new EulerAngle(
                    interpolate(frame.getBodyStart().getX(), frame.getBodyEnd().getX(), t),
                    interpolate(frame.getBodyStart().getY(), frame.getBodyEnd().getY(), t),
                    interpolate(frame.getBodyStart().getZ(), frame.getBodyEnd().getZ(), t)
            );
            targetArmorStand.setBodyPose(newBody);
        }

        if (frame.getLeftArmStart() != null && frame.getLeftArmEnd() != null) {
            EulerAngle newLeftArm = new EulerAngle(
                    interpolate(frame.getLeftArmStart().getX(), frame.getLeftArmEnd().getX(), t),
                    interpolate(frame.getLeftArmStart().getY(), frame.getLeftArmEnd().getY(), t),
                    interpolate(frame.getLeftArmStart().getZ(), frame.getLeftArmEnd().getZ(), t)
            );
            targetArmorStand.setLeftArmPose(newLeftArm);
        }

        if (frame.getRightArmStart() != null && frame.getRightArmEnd() != null) {
            EulerAngle newRightArm = new EulerAngle(
                    interpolate(frame.getRightArmStart().getX(), frame.getRightArmEnd().getX(), t),
                    interpolate(frame.getRightArmStart().getY(), frame.getRightArmEnd().getY(), t),
                    interpolate(frame.getRightArmStart().getZ(), frame.getRightArmEnd().getZ(), t)
            );
            targetArmorStand.setRightArmPose(newRightArm);
        }

        if (frame.getLeftLegStart() != null && frame.getLeftLegEnd() != null) {
            EulerAngle newLeftLeg = new EulerAngle(
                    interpolate(frame.getLeftLegStart().getX(), frame.getLeftLegEnd().getX(), t),
                    interpolate(frame.getLeftLegStart().getY(), frame.getLeftLegEnd().getY(), t),
                    interpolate(frame.getLeftLegStart().getZ(), frame.getLeftLegEnd().getZ(), t)
            );
            targetArmorStand.setLeftLegPose(newLeftLeg);
        }

        if (frame.getRightLegStart() != null && frame.getRightLegEnd() != null) {
            EulerAngle newRightLeg = new EulerAngle(
                    interpolate(frame.getRightLegStart().getX(), frame.getRightLegEnd().getX(), t),
                    interpolate(frame.getRightLegStart().getY(), frame.getRightLegEnd().getY(), t),
                    interpolate(frame.getRightLegStart().getZ(), frame.getRightLegEnd().getZ(), t)
            );
            targetArmorStand.setRightLegPose(newRightLeg);
        }

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

    /// <summary>
    /// Pauses the animation.
    /// </summary>
    public void pause() {
        if (!paused) paused = true;
    }

    /// <summary>
    /// Resumes the animation.
    /// </summary>
    public void play() {
        if (paused) paused = false;
    }

    /// <summary>
    /// Stops the animation and clears all frames.
    /// </summary>
    public void stop() {
        paused = true;
        frames.clear();
    }

    /// <summary>
    /// Builder for creating a <see cref="DreamArmorStandAnimator"/>.
    /// </summary>
    public static class Builder {
        private UUID animatorID = UUID.randomUUID();
        private final List<ArmorStandAnimationFrameData> frames = new CopyOnWriteArrayList<>();
        private ArmorStand targetArmorStand;

        /// <summary>
        /// Sets a custom ID for the animator.
        /// </summary>
        /// <param name="animatorID">The custom UUID.</param>
        /// <returns>The builder instance.</returns>
        public Builder animatorID(UUID animatorID) {
            if (animatorID != null) this.animatorID = animatorID;
            return this;
        }

        /// <summary>
        /// Sets the target armor stand to animate.
        /// </summary>
        /// <param name="armorStand">The target armor stand.</param>
        /// <returns>The builder instance.</returns>
        /// <exception cref="IllegalArgumentException">
        /// Thrown if <paramref name="armorStand"/> is <c>null</c>.
        /// </exception>
        public Builder targetArmorStand(ArmorStand armorStand) {
            if (armorStand == null) throw new IllegalArgumentException("ArmorStand cannot be null");
            this.targetArmorStand = armorStand;
            return this;
        }

        /// <summary>
        /// Adds a frame to the animation with a specified number of repeats.
        /// </summary>
        /// <param name="frame">The frame to add.</param>
        /// <param name="repeatFrames">The number of times to repeat the frame.</param>
        /// <returns>The builder instance.</returns>
        public Builder addFrame(ArmorStandAnimationFrameData frame, int repeatFrames) {
            if (frame == null || repeatFrames <= 0) return this;
            for (int i = 0; i < repeatFrames; i++) {
                frames.add(frame);
            }
            return this;
        }

        /// <summary>
        /// Adds a single frame to the animation.
        /// </summary>
        /// <param name="frame">The frame to add.</param>
        /// <returns>The builder instance.</returns>
        public Builder addFrame(ArmorStandAnimationFrameData frame) {
            if (frame != null) frames.add(frame);
            return this;
        }

        /// <summary>
        /// Builds and returns a <see cref="DreamArmorStandAnimator"/>.
        /// </summary>
        /// <returns>The constructed animator instance.</returns>
        /// <exception cref="IllegalArgumentException">
        /// Thrown if no target armor stand is set or if no frames have been added.
        /// </exception>
        public DreamArmorStandAnimator build() {
            if (targetArmorStand == null)
                throw new IllegalArgumentException("Target ArmorStand must be set");
            if (frames.isEmpty())
                throw new IllegalArgumentException("At least one frame must be added");
            DreamArmorStandAnimator animator = new DreamArmorStandAnimator();
            animator.animatorID = animatorID;
            animator.targetArmorStand = targetArmorStand;
            animator.frames = frames;
            return animator;
        }
    }
}