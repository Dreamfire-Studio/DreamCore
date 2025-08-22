package com.dreamfirestudios.dreamcore.DreamTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static helpers + registry for in-flight teleports.
 * If you already keep a global list in DreamCore, you can swap the registry
 * implementation below to use it instead.
 */
public final class DreamTeleportAPI {

    private DreamTeleportAPI() {}

    // Active tasks, keyed by player UUID for quick lookups.
    private static final Map<UUID, DreamTeleport> ACTIVE = new ConcurrentHashMap<>();

    /**
     * Teleport a single player (convenience overload).
     */
    public static void teleportPlayer(
            Plugin plugin,
            Player player,
            LivingEntity liveTarget,
            Location fixedTarget,
            int seconds,
            boolean showCountdown,
            boolean cancelOnMove
    ) {
        teleportPlayers(plugin, Collections.singletonList(player), liveTarget, fixedTarget, seconds, showCountdown, cancelOnMove, 0.1, DreamTeleport.DEFAULT_PERIOD_TICKS);
    }

    /**
     * Teleport multiple players with full control.
     *
     * @param plugin        owning plugin
     * @param players       players to queue
     * @param liveTarget    entity target (nullable)
     * @param fixedTarget   fallback/explicit location (nullable if liveTarget present)
     * @param seconds       countdown duration
     * @param showCountdown show actionbar countdown
     * @param cancelOnMove  cancel when players move
     * @param moveTolerance movement tolerance (blocks) before cancel
     * @param periodTicks   scheduler period (ticks)
     */
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
        // Filter out players already teleporting
        List<Player> eligible = new ArrayList<>();
        for (Player p : players) {
            if (p != null && p.isOnline() && !isPlayerTeleporting(p)) {
                eligible.add(p);
            }
        }
        if (eligible.isEmpty()) return;

        DreamTeleport task = new DreamTeleport(
                plugin,
                eligible,
                liveTarget,
                fixedTarget,
                seconds,
                periodTicks,
                showCountdown,
                cancelOnMove,
                moveTolerance
        );

        // register players -> task
        for (Player p : eligible) {
            ACTIVE.put(p.getUniqueId(), task);
        }

        task.start();

        // When the task ends (no players left), clean up registry.
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (task.isEmpty()) {
                ACTIVE.entrySet().removeIf(e -> e.getValue() == task);
            }
        }, 0L, 20L);
    }

    /** Returns true if the player is currently queued in any teleport task. */
    public static boolean isPlayerTeleporting(Player player) {
        return player != null && ACTIVE.containsKey(player.getUniqueId());
    }

    /** Cancels a player's pending teleport (if any). */
    public static void cancelPlayerTeleport(Player player) {
        if (player == null) return;
        DreamTeleport task = ACTIVE.remove(player.getUniqueId());
        if (task != null) {
            boolean empty = task.remove(player);
            if (empty) task.cancel();
        }
    }

    /** Cancels all teleports for the given players. */
    public static void cancelAll(Collection<Player> players) {
        for (Player p : players) cancelPlayerTeleport(p);
    }
}