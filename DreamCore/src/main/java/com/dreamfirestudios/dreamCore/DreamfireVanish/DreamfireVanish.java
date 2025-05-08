package com.dreamfirestudios.dreamCore.DreamfireVanish;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireVanish.Event.VanishHideTargetEvent;
import com.dreamfirestudios.dreamCore.DreamfireVanish.Event.VanishShowTargetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DreamfireVanish {
    /**
     * Hides the target entity from the specified viewer.
     */
    public static void hideTargetFromViewer(Entity target, Player viewer) {
        if (target == null || viewer == null) return;

        var hiddenViewers = DreamCore.GetDreamfireCore().viewerHideMatrixLinkedHashMap.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>());
        if (!hiddenViewers.contains(viewer.getUniqueId())) {
            hiddenViewers.add(viewer.getUniqueId());
        }

        new VanishHideTargetEvent(target, viewer);
    }

    /**
     * Shows the target entity to the specified viewer.
     */
    public static void showTargetToViewer(Entity target, Player viewer) {
        if (target == null || viewer == null) return;

        var hiddenViewers = DreamCore.GetDreamfireCore().viewerHideMatrixLinkedHashMap.get(target.getUniqueId());
        if (hiddenViewers != null) {
            hiddenViewers.remove(viewer.getUniqueId());
            if (hiddenViewers.isEmpty()) {
                DreamCore.GetDreamfireCore().viewerHideMatrixLinkedHashMap.remove(target.getUniqueId());
            }
        }

        new VanishShowTargetEvent(target, viewer);
    }

    /**
     * Checks if a viewer can see the target entity.
     */
    public static boolean canViewerSeeTarget(Entity target, Player viewer) {
        if (target == null || viewer == null) return false;

        var hiddenViewers = DreamCore.GetDreamfireCore().viewerHideMatrixLinkedHashMap.get(target.getUniqueId());
        return hiddenViewers == null || !hiddenViewers.contains(viewer.getUniqueId());
    }

    public static void updateVanishOnAllPlayers() {
        var onlinePlayers = Bukkit.getOnlinePlayers();
        var viewerHideMatrix = DreamCore.GetDreamfireCore().viewerHideMatrixLinkedHashMap;

        viewerHideMatrix.forEach((targetUUID, hiddenViewers) -> {
            var target = Bukkit.getEntity(targetUUID);
            if (target == null) {
                viewerHideMatrix.remove(targetUUID); // Cleanup invalid entries
                return;
            }

            for (var viewer : onlinePlayers) {
                if (viewer.getUniqueId().equals(targetUUID)) continue;

                if (hiddenViewers.contains(viewer.getUniqueId())) {
                    viewer.hideEntity(DreamCore.GetDreamfireCore(), target);
                } else {
                    viewer.showEntity(DreamCore.GetDreamfireCore(), target);
                }
            }
        });
    }
}
