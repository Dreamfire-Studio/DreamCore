/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dreamfirestudios.dreamcore.DreamVanish;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/// <summary>
/// Static helpers for per-viewer vanish/visibility tracking.
/// </summary>
/// <remarks>
/// This class records viewer → hidden-target intent in <see cref="DreamCore#DreamVanishs"/> (UUID → list of viewers).
/// Call <see cref="#updateVanishOnAllPlayers()"/> to apply the intent using Bukkit’s hide/show API.
/// </remarks>
/// <example>
/// <code>
/// DreamVanish.hideTargetFromViewer(targetEntity, viewer);
/// DreamVanish.updateVanishOnAllPlayers();
/// </code>
/// </example>
public class DreamVanish {

    /// <summary>
    /// Hides a target entity from a specific viewer and fires <see cref="VanishHideTargetEvent"/>.
    /// </summary>
    /// <param name="target">Entity to hide.</param>
    /// <param name="viewer">Viewer who should no longer see the entity.</param>
    public static void hideTargetFromViewer(Entity target, Player viewer) {
        if (target == null || viewer == null) return;

        var hiddenViewers = DreamCore.DreamVanishs.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>());
        if (!hiddenViewers.contains(viewer.getUniqueId())) {
            hiddenViewers.add(viewer.getUniqueId());
        }

        new VanishHideTargetEvent(target, viewer);
    }

    /// <summary>
    /// Shows a previously hidden target entity to a specific viewer and fires <see cref="VanishShowTargetEvent"/>.
    /// </summary>
    /// <param name="target">Entity to show.</param>
    /// <param name="viewer">Viewer who should see the entity again.</param>
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

    /// <summary>
    /// Checks whether a viewer can currently see a target entity.
    /// </summary>
    /// <param name="target">Entity being viewed.</param>
    /// <param name="viewer">Player who may see the entity.</param>
    /// <returns>True if visible; false if hidden or if any argument is null.</returns>
    public static boolean canViewerSeeTarget(Entity target, Player viewer) {
        if (target == null || viewer == null) return false;

        var hiddenViewers = DreamCore.DreamVanishs.get(target.getUniqueId());
        return hiddenViewers == null || !hiddenViewers.contains(viewer.getUniqueId());
    }

    /// <summary>
    /// Applies the current vanish matrix to all online players (hide/show).
    /// </summary>
    /// <remarks>
    /// Call on player join/quit/world change or periodically to reconcile visibility.
    /// </remarks>
    public static void updateVanishOnAllPlayers() {
        var onlinePlayers = Bukkit.getOnlinePlayers();
        var viewerHideMatrix = DreamCore.DreamVanishs;

        viewerHideMatrix.forEach((targetUUID, hiddenViewers) -> {
            var target = Bukkit.getEntity(targetUUID);
            if (target == null) {
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