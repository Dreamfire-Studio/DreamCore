package com.dreamfirestudios.dreamcore.DreamTeleport;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A scheduled teleport task that can handle one or more players counting down
 * to a target (either a live entity or a fixed location). Optionally cancels
 * when players move away from their start positions.
 *
 * Threading: all logic runs on the main server thread via the Bukkit scheduler.
 */
public final class DreamTeleport implements Runnable {

    /** Default tick period; 20 ticks = 1 second updates. */
    public static final long DEFAULT_PERIOD_TICKS = 20L;

    private final Plugin plugin;
    private final Map<UUID, Location> startLocations = new ConcurrentHashMap<>();
    private final WeakReference<LivingEntity> liveTarget;   // may be null
    private final Location fixedTarget;                     // may be null
    private final boolean showCountdown;
    private final boolean cancelOnMove;
    private final double moveToleranceSq;
    private final long periodTicks;

    private final int totalSeconds;
    private int secondsLeft;

    private BukkitTask task;

    /**
     * @param plugin        owning plugin
     * @param players       players to teleport
     * @param liveTarget    entity to follow at completion (nullable)
     * @param fixedTarget   fallback/explicit location (nullable if liveTarget present)
     * @param seconds       countdown in seconds
     * @param periodTicks   scheduler period (use 20 for per-second updates)
     * @param showCountdown show an actionbar countdown each tick
     * @param cancelOnMove  cancel if a player moves from their start location
     * @param moveTolerance movement tolerance in blocks before cancellation (e.g., 0.1)
     */
    public DreamTeleport(
            Plugin plugin,
            Collection<Player> players,
            LivingEntity liveTarget,
            Location fixedTarget,
            int seconds,
            long periodTicks,
            boolean showCountdown,
            boolean cancelOnMove,
            double moveTolerance
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(players, "players");
        if (liveTarget == null && fixedTarget == null) {
            throw new IllegalArgumentException("Either liveTarget or fixedTarget must be provided");
        }

        for (Player p : players) {
            if (p != null && p.isOnline()) {
                startLocations.put(p.getUniqueId(), p.getLocation().clone());
            }
        }

        this.liveTarget = liveTarget == null ? null : new WeakReference<>(liveTarget);
        this.fixedTarget = fixedTarget == null ? null : fixedTarget.clone();
        this.totalSeconds = Math.max(0, seconds);
        this.secondsLeft = this.totalSeconds;
        this.periodTicks = Math.max(1L, periodTicks);
        this.showCountdown = showCountdown;
        this.cancelOnMove = cancelOnMove;
        this.moveToleranceSq = Math.max(0.0, moveTolerance) * Math.max(0.0, moveTolerance);
    }

    /** Starts the scheduled task. No-op if already running. */
    public void start() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, periodTicks);
    }

    /** Cancels the task immediately without teleporting remaining players. */
    public void cancel() {
        if (task != null) task.cancel();
        task = null;
        startLocations.clear();
    }

    /** Returns true if this task still has players queued. */
    public boolean isEmpty() {
        return startLocations.isEmpty();
    }

    /** Returns true if the given player is queued in this task. */
    public boolean contains(Player player) {
        return player != null && startLocations.containsKey(player.getUniqueId());
    }

    /** Removes a single player from the queue (returns true if the task has no players left). */
    public boolean remove(Player player) {
        if (player == null) return isEmpty();
        startLocations.remove(player.getUniqueId());
        return isEmpty();
    }

    @Override
    public void run() {
        // if no players remain, stop the task
        if (startLocations.isEmpty()) {
            cancel();
            return;
        }

        // Per-tick housekeeping & countdown display
        if (secondsLeft > 0) {
            // show actionbar countdown (optional)
            if (showCountdown) {
                forEachOnlinePlayer(p -> p.sendActionBar(Component.text(
                        "Teleporting in " + secondsLeft + " / " + totalSeconds + "…"
                )));
            }

            // cancel on move (if enabled)
            if (cancelOnMove) {
                final List<UUID> toRemove = new ArrayList<>();
                for (Map.Entry<UUID, Location> e : startLocations.entrySet()) {
                    Player p = Bukkit.getPlayer(e.getKey());
                    if (p == null || !p.isOnline()) {
                        toRemove.add(e.getKey());
                        continue;
                    }
                    Location start = e.getValue();
                    Location now = p.getLocation();

                    if (!sameWorld(start, now) || start.toVector().distanceSquared(now.toVector()) > moveToleranceSq) {
                        p.sendActionBar(Component.text("Teleport cancelled (you moved)."));
                        toRemove.add(e.getKey());
                    }
                }
                toRemove.forEach(startLocations::remove);
                if (startLocations.isEmpty()) {
                    cancel();
                    return;
                }
            }

            secondsLeft--;
            return;
        }

        // Time's up: teleport remaining players
        Location target = resolveTarget();
        if (target == null) {
            // live target vanished AND no fixed fallback; cancel safely
            forEachOnlinePlayer(p -> p.sendActionBar(Component.text("Teleport failed: no valid target.")));
            cancel();
            return;
        }

        forEachOnlinePlayer(p -> p.teleport(target));
        cancel(); // finished
    }

    private void forEachOnlinePlayer(java.util.function.Consumer<Player> consumer) {
        for (UUID id : new ArrayList<>(startLocations.keySet())) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline()) {
                consumer.accept(p);
            } else {
                startLocations.remove(id);
            }
        }
    }

    private static boolean sameWorld(Location a, Location b) {
        World wa = a.getWorld();
        World wb = b.getWorld();
        return wa != null && wa.equals(wb);
    }

    private Location resolveTarget() {
        if (liveTarget != null) {
            LivingEntity e = liveTarget.get();
            if (e != null && e.isValid()) {
                return e.getLocation();
            }
        }
        return fixedTarget;
    }

    // --- Getters ---
    public int getSecondsLeft() { return secondsLeft; }
    public int getTotalSeconds() { return totalSeconds; }
}