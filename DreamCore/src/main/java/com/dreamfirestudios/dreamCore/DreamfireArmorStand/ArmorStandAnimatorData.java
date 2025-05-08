package com.dreamfire.dreamfirecore.DreamfireArmorStand;

import org.bukkit.entity.ArmorStand;
import java.util.function.Consumer;

/**
 * Represents a single frame of an ArmorStand animation.
 * The frameAction is executed to update the ArmorStand.
 */
public class ArmorStandAnimatorData {
    private final Consumer<ArmorStand> frameAction;

    public ArmorStandAnimatorData(Consumer<ArmorStand> frameAction) {
        if(frameAction == null) throw new IllegalArgumentException("Frame action cannot be null");
        this.frameAction = frameAction;
    }

    public void displayFrame(ArmorStand armorStand) {
        frameAction.accept(armorStand);
    }
}
