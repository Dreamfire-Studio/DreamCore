package com.dreamfirestudios.dreamcore.DreamPrompt;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/** Fired when the conversation ends, either normally or via abandon. */
@Getter
public class PromptEndedEvent extends Event {
    public enum EndReason { NORMAL, ABANDONED }

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String lastInput;
    private final ConversationContext context;
    private final EndReason reason;

    public PromptEndedEvent(Player player, String lastInput, ConversationContext context, EndReason reason) {
        this.player = player;
        this.lastInput = lastInput;
        this.context = context;
        this.reason = reason;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}