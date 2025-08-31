package com.dreamfirestudios.dreamcore.DreamEvent;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@PulseAutoRegister
public final class DreamPlayerMoveSeedListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (DreamCore.DreamPlayerMoveMonitor != null){
            DreamCore.DreamPlayerMoveMonitor.seed(e.getPlayer());
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (DreamCore.DreamPlayerMoveMonitor != null){
            DreamCore.DreamPlayerMoveMonitor.forget(e.getPlayer().getUniqueId());
        }
    }
}