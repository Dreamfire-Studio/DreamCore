package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBlockMask"/> transitions from paused to playing.
/// </summary>
/// <remarks>
/// Not cancellable. Emitted after the play state is set.
/// </remarks>
@Getter
public final class BlockMaskStartedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DreamBlockMask mask;

    public BlockMaskStartedEvent(Player player, DreamBlockMask mask) {
        this.player = player;
        this.mask = mask;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}