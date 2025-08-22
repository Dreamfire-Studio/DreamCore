package com.dreamfirestudios.dreamcore.DreamVanish;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * <summary>
 * Static helpers for per-viewer entity vanish/visibility control.
 * Maintains a mapping in {@link DreamCore#DreamVanishs} from target entity UUID → list of viewer UUIDs who cannot see it.
 * </summary>
 *
 * <remarks>
 * This class does not perform the actual hide/show; it records intent and fires events.
 * Call {@link #updateVanishOnAllPlayers()} to apply hide/show state to connected players.
 * </remarks>
 */
public class DreamVanish {

    /**
     * <summary>Hides a target entity from a specific viewer and fires {@link VanishHideTargetEvent}.</summary>
     *
     * <param name="target">Entity to hide.</param>
     * <param name="viewer">Player who should no longer see the target.</param>
     */
    public static void hideTargetFromViewer(Entity target, Player viewer) {
        if (target == null || viewer == null) return;

        var hiddenViewers = DreamCore.DreamVanishs.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>());
        if (!hiddenViewers.contains(viewer.getUniqueId())) {
            hiddenViewers.add(viewer.getUniqueId());
        }

        new VanishHideTargetEvent(target, viewer);
    }

    /**
     * <summary>Shows a previously hidden target entity to a specific viewer and fires {@link VanishShowTargetEvent}.</summary>
     *
     * <param name="target">Entity to show.</param>
     * <param name="viewer">Player who should be able to see the target again.</param>
     */
    public static void showTargetToViewer(Entity target, Player viewer) {
        if (target == null || viewer == null) return;

        var hiddenViewers = DreamCore.DreamVanishs.get(target.getUniqueId());
        if (hiddenViewers != null) {
            hiddenViewers.remove(viewer.getUniqueId());
            if (hiddenViewers.isEmpty()) {
                DreamCore.DreamVanishs.remove(target.getUniqueId());
            }
        }

        new VanishShowTargetEvent(target, viewer);
    }

    /**
     * <summary>Checks whether a viewer can currently see a target entity.</summary>
     *
     * <param name="target">The entity being viewed.</param>
     * <param name="viewer">The player who might see (or not see) the entity.</param>
     * <returns>True if the viewer can see the target; false if hidden or inputs are null.</returns>
     *
     * <remarks>
     * Returns <code>false</code> when either argument is <code>null</code>.
     * </remarks>
     */
    public static boolean canViewerSeeTarget(Entity target, Player viewer) {
        if (target == null || viewer == null) return false;

        var hiddenViewers = DreamCore.DreamVanishs.get(target.getUniqueId());
        return hiddenViewers == null || !hiddenViewers.contains(viewer.getUniqueId());
    }

    /**
     * <summary>
     * Applies the current vanish matrix to all online players by calling
     * {@link Player#hideEntity(org.bukkit.plugin.Plugin, Entity)} or
     * {@link Player#showEntity(org.bukkit.plugin.Plugin, Entity)} as appropriate.
     * </summary>
     *
     * <remarks>
     * Designed to be called periodically (e.g., on join/quit/world change or a scheduler tick)
     * to reconcile intent with actual in-game visibility.
     * </remarks>
     */
    public static void updateVanishOnAllPlayers() {
        var onlinePlayers = Bukkit.getOnlinePlayers();
        var viewerHideMatrix = DreamCore.DreamVanishs;

        viewerHideMatrix.forEach((targetUUID, hiddenViewers) -> {
            var target = Bukkit.getEntity(targetUUID);
            if (target == null) {
                // Cleanup invalid entries
                viewerHideMatrix.remove(targetUUID);
                return;
            }

            for (var viewer : onlinePlayers) {
                if (viewer.getUniqueId().equals(targetUUID)) continue;

                if (hiddenViewers.contains(viewer.getUniqueId())) {
                    viewer.hideEntity(DreamCore.DreamCore, target);
                } else {
                    viewer.showEntity(DreamCore.DreamCore, target);
                }
            }
        });
    }
}