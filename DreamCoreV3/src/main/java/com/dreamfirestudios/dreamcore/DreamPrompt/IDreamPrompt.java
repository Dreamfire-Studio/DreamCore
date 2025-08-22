package com.dreamfirestudios.dreamcore.DreamPrompt;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Adapters supply text + behavior for {@link DreamPrompt#start(Player, IDreamPrompt, boolean)}.
 */
public interface IDreamPrompt {
    String promptText(Player player);

    default HashMap<Object, Object> defaultData(Player player) { return new HashMap<>(); }

    default boolean clearPlayerChatOnStart(Player player)   { return true; }
    default boolean clearPlayerChatOnRestart(Player player) { return true; }
    default boolean clearPlayerChatOnEnd(Player player)     { return true; }

    /** Called when the player submits input for this prompt. */
    Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponseCallback();

    /** Called if the prompt continues (same step repeats). */
    default Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onConversationRestartCallback() {
        return triplet -> { /* no-op */ };
    }

    /** Called when the prompt ends normally via END_KEY or abandon. */
    Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEndConversationCallback();
}