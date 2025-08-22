package com.dreamfirestudios.dreamcore.DreamPrompt;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when the prompt continues (chat may be cleared, same step repeats).
/// </summary>
@Getter
public class PromptRestartEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>Player continuing the prompt.</summary>
    private final Player player;
    /// <summary>Last input that caused the restart.</summary>
    private final String input;
    /// <summary>Conversation context.</summary>
    private final ConversationContext context;

    /// <summary>Creates and dispatches the event.</summary>
    public PromptRestartEvent(Player player, String input, ConversationContext context) {
        this.player = player;
        this.input = input;
        this.context = context;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}