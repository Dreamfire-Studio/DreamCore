package com.dreamfirestudios.dreamcore.DreamPrompt;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired just before a prompt starts for a player.
/// </summary>
/// <remarks>
/// This event is cancellable. Cancel to prevent the prompt from starting.
/// </remarks>
@Getter
public class PromptStartedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Player for whom the prompt is starting.</summary>
    private final Player player;

    private boolean cancelled;

    /// <summary>Creates and dispatches the event.</summary>
    public PromptStartedEvent(Player player){
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    public @NotNull HandlerList getHandlers() { return handlers; }

    /// <inheritdoc />
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /// <inheritdoc />
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}