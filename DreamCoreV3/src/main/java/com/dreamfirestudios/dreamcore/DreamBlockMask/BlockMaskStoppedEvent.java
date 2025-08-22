package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBlockMask"/> is stopped and unregistered.
/// </summary>
/// <remarks>
/// Not cancellable. Emitted after restoration/cleanup logic runs.
/// </remarks>
@Getter
public final class BlockMaskStoppedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DreamBlockMask mask;

    public BlockMaskStoppedEvent(Player player, DreamBlockMask mask) {
        this.player = player;
        this.mask = mask;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}