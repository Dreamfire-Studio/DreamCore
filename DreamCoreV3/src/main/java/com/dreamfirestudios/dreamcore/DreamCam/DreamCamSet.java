package com.dreamfirestudios.dreamcore.DreamCam;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector2f;

import java.util.List;

/// <summary>
/// Represents a segment of a camera path containing ordered points
/// and rotation rules (look-at behavior).
/// </summary>
public record DreamCamSet(List<Location> points, LookAtType lookAtType, Object object) {

    /// <summary>
    /// Adjusts yaw/pitch rotation for a given point based on the lookAt mode.
    /// </summary>
    public Location setRotation(Location location, Location fallbackTarget) {
        var current = location.clone();
        Vector2f newRotation = null;

        if (lookAtType == LookAtType.NoFocus || object == null) {
            newRotation = computeRotation(current, fallbackTarget);
        } else if (lookAtType == LookAtType.FixedFocus) {
            newRotation = computeRotation(current, (Location) object);
        } else if (lookAtType == LookAtType.MovingFocus) {
            var entity = (Entity) object;
            newRotation = entity != null ? computeRotation(current, entity.getLocation()) : computeRotation(current, fallbackTarget);
        }

        if (newRotation == null) return current;
        current.setYaw(newRotation.x);
        current.setPitch(newRotation.y);
        return current;
    }

    private Vector2f computeRotation(Location from, Location to) {
        var dx = to.getX() - from.getX();
        var dy = to.getY() - from.getY();
        var dz = to.getZ() - from.getZ();
        var distanceHorizontal = Math.sqrt(dx * dx + dz * dz);
        var yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        var pitch = (float) Math.toDegrees(Math.atan2(dy, distanceHorizontal));
        return new Vector2f(yaw, pitch);
    }
}