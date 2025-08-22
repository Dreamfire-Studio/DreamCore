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