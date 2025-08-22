package com.dreamfirestudios.dreamcore.DreamPrompt;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when the conversation ends, either normally or via abandon.
/// </summary>
@Getter
public class PromptEndedEvent extends Event {
    /// <summary>End reasons.</summary>
    public enum EndReason { NORMAL, ABANDONED }

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>Player whose prompt ended.</summary>
    private final Player player;
    /// <summary>Last input captured (may be empty if ended without input).</summary>
    private final String lastInput;
    /// <summary>Conversation context.</summary>
    private final ConversationContext context;
    /// <summary>Reason for ending.</summary>
    private final EndReason reason;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="player">Player.</param>
    /// <param name="lastInput">Last input string.</param>
    /// <param name="context">Conversation context.</param>
    /// <param name="reason">End reason.</param>
    public PromptEndedEvent(Player player, String lastInput, ConversationContext context, EndReason reason) {
        this.player = player;
        this.lastInput = lastInput;
        this.context = context;
        this.reason = reason;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}