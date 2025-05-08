package com.dreamfirestudios.dreamCore.DreamfireCam;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector2f;

import java.util.List;

public record DreamfireCamSet(List<Location> points, LookAtType lookAtType, Object object) {
    public Location SetRotation(Location location, Location nextLocation){
        var currentLocation = location.clone();
        Vector2f newRotation = null;

        if(lookAtType == LookAtType.NoFocus || object == null) newRotation = ReturnRotation(currentLocation, nextLocation);
        if(lookAtType == LookAtType.FixedFocus) newRotation = ReturnRotation(currentLocation, (Location) object);
        if(lookAtType == LookAtType.MovingFocus){
            var entity = ((Entity) object);
            if(entity == null) newRotation = ReturnRotation(currentLocation, nextLocation);
            else newRotation = ReturnRotation(currentLocation, entity.getLocation());
        }
        if(newRotation == null) return currentLocation;

        currentLocation.setYaw(newRotation.x);
        currentLocation.setPitch(newRotation.y);
        return currentLocation;
    }

    private Vector2f ReturnRotation(Location playerLocation, Location targetLocation){
        var dx = targetLocation.getX() - playerLocation.getX();
        var dy = targetLocation.getY() - playerLocation.getY();
        var dz = targetLocation.getZ() - playerLocation.getZ();
        var distanceHorizontal = Math.sqrt(dx * dx + dz * dz);
        var yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        var pitch = (float) Math.toDegrees(Math.atan2(dy, distanceHorizontal));
        return new Vector2f(yaw, pitch);
    }
}
