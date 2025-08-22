package com.dreamfirestudios.dreamcore.DreamBossBar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired after a <see cref="DreamBossBar"/> applies a frame to its viewers.
/// </summary>
/// <remarks>
/// Not cancellable. Dispatched from the constructor (instantiation triggers <c>callEvent</c>).
/// </remarks>
@Getter
public class BossBarFrameAdvancedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The boss bar that advanced.</summary>
    private final DreamBossBar bossBar;

    /// <summary>The index of the frame that was just applied.</summary>
    private final int appliedFrameIndex;

    /// <summary>
    /// Constructs and dispatches a new <see cref="BossBarFrameAdvancedEvent"/>.
    /// </summary>
    /// <param name="bossBar">The boss bar that advanced.</param>
    /// <param name="appliedFrameIndex">The frame index that was just shown.</param>
    public BossBarFrameAdvancedEvent(DreamBossBar bossBar, int appliedFrameIndex) {
        this.bossBar = bossBar;
        this.appliedFrameIndex = appliedFrameIndex;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}