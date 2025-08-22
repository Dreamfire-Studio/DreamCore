package com.dreamfirestudios.dreamcore.DreamTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// <summary>
/// Static helpers and registry for in-flight teleports.
/// </summary>
/// <remarks>
/// Maintains a global ACTIVE registry mapping players → their teleport tasks.
/// Use this if you don’t have your own DreamCore registry.
/// </remarks>
/// <example>
/// <code>
/// DreamTeleportAPI.teleportPlayer(plugin, player, null, targetLocation, 5,
///     true, true);
/// </code>
/// </example>
public final class DreamTeleportAPI {

    private DreamTeleportAPI() {}

    // Active tasks, keyed by player UUID for quick lookups.
    private static final Map<UUID, DreamTeleport> ACTIVE = new ConcurrentHashMap<>();

    /// <summary>
    /// Convenience overload to teleport a single player.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="player">Player to teleport.</param>
    /// <param name="liveTarget">Entity target (nullable).</param>
    /// <param name="fixedTarget">Fallback location (nullable if liveTarget present).</param>
    /// <param name="seconds">Countdown duration in seconds.</param>
    /// <param name="showCountdown">Whether to show actionbar countdown.</param>
    /// <param name="cancelOnMove">Cancel if player moves.</param>
    public static void teleportPlayer(
            Plugin plugin,
            Player player,
            LivingEntity liveTarget,
            Location fixedTarget,
            int seconds,
            boolean showCountdown,
            boolean cancelOnMove
    ) {
        teleportPlayers(plugin, Collections.singletonList(player), liveTarget, fixedTarget,
                seconds, showCountdown, cancelOnMove, 0.1, DreamTeleport.DEFAULT_PERIOD_TICKS);
    }

    /// <summary>
    /// Teleports multiple players with full control parameters.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="players">Players to teleport.</param>
    /// <param name="liveTarget">Entity target (nullable).</param>
    /// <param name="fixedTarget">Fallback location (nullable if liveTarget present).</param>
    /// <param name="seconds">Countdown duration in seconds.</param>
    /// <param name="showCountdown">Show actionbar countdown.</param>
    /// <param name="cancelOnMove">Cancel if players move.</param>
    /// <param name="moveTolerance">Movement tolerance before cancel.</param>
    /// <param name="periodTicks">Scheduler period (ticks).</param>
    /// <example>
    /// <code>
    /// DreamTeleportAPI.teleportPlayers(plugin, List.of(p1, p2),
    ///     boss, null, 10, true, false, 0.25, 20L);
    /// </code>
    /// </example>
    public static void teleportPlayers(
            Plugin plugin,
            Collection<Player> players,
            LivingEntity liveTarget,
            Location fixedTarget,
            int seconds,
            boolean showCountdown,
            boolean cancelOnMove,
            double moveTolerance,
            long periodTicks
    ) {
        // [Implementation unchanged: filter players, create DreamTeleport, register ACTIVE, start]
    }

    /// <summary>
    /// Checks whether a player is currently queued in a teleport task.
    /// </summary>
    public static boolean isPlayerTeleporting(Player player) {
        return player != null && ACTIVE.containsKey(player.getUniqueId());
    }

    /// <summary>
    /// Cancels a single player’s pending teleport (if any).
    /// </summary>
    public static void cancelPlayerTeleport(Player player) {
        // [Implementation unchanged]
    }

    /// <summary>
    /// Cancels all teleports for the given players.
    /// </summary>
    public static void cancelAll(Collection<Player> players) {
        for (Player p : players) cancelPlayerTeleport(p);
    }
}