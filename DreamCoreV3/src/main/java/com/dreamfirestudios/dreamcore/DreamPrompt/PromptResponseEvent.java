package com.dreamfirestudios.dreamcore.DreamPrompt;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/** Fired after a player's message is received and before restart/end logic is applied. */
@Getter
public class PromptResponseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String input;
    private final ConversationContext context;

    public PromptResponseEvent(Player player, String input, ConversationContext context) {
        this.player = player;
        this.input = input;
        this.context = context;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}