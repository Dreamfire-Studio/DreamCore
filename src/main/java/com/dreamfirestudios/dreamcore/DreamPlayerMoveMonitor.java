package com.dreamfirestudios.dreamcore;

import com.dreamfirestudios.dreamcore.DreamEvent.DreamPlayerMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DreamPlayerMoveMonitor {
    private static final double POS_EPSILON_SQ = 0.0004; // ~2cm (0.02) squared

    private final Map<UUID, Location> last = new ConcurrentHashMap<>();
    private final Plugin plugin;

    public DreamPlayerMoveMonitor(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public void tick() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID id = p.getUniqueId();

            Location current = p.getLocation().clone();
            Location previous = last.get(id);

            if (previous == null) {
                last.put(id, current);
                continue;
            }

            if (!hasMoved(previous, current)) {
                // Not significant; keep previous snapshot
                continue;
            }

            DreamPlayerMoveEvent event = new DreamPlayerMoveEvent(p, previous.clone(), current.clone());
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                // Revert movement to 'from'; do not update 'last'
                // (We set direction to the 'from' direction, preserving previous facing.)
                Location target = event.getFrom().clone();
                p.teleport(target);
            } else {
                // Accept movement; advance baseline
                last.put(id, current);
            }
        }
    }

    /**
     * <summary>Seed or reset the last-known location for a player.</summary>
     */
    public void seed(Player p) {
        if (p == null) return;
        last.put(p.getUniqueId(), p.getLocation().clone());
    }

    /**
     * <summary>Forget a player (e.g., on quit).</summary>
     */
    public void forget(UUID playerId) {
        if (playerId != null) last.remove(playerId);
    }

    private boolean hasMoved(Location a, Location b) {
        World wa = a.getWorld();
        World wb = b.getWorld();
        if (wa == null || wb == null) return true;
        if (!wa.getUID().equals(wb.getUID())) return true;

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return (dx * dx + dy * dy + dz * dz) > POS_EPSILON_SQ;
    }
}