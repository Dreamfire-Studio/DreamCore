package com.dreamfirestudios.dreamcore.DreamBossBar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBossBar"/> is resumed (played).
/// </summary>
/// <remarks>
/// Not cancellable. Dispatched from the constructor.
/// </remarks>
@Getter
public class BossBarStartedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The boss bar that started playing.</summary>
    private final DreamBossBar bossBar;

    /// <summary>
    /// Constructs and dispatches a new <see cref="BossBarStartedEvent"/>.
    /// </summary>
    /// <param name="bossBar">The boss bar that started playing.</param>
    public BossBarStartedEvent(DreamBossBar bossBar) {
        this.bossBar = bossBar;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}