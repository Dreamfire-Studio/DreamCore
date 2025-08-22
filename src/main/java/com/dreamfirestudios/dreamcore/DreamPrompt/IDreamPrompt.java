package com.dreamfirestudios.dreamcore.DreamPrompt;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

/// <summary>
/// Adapters supply text and behavior for <see cref="DreamPrompt.start(Player, IDreamPrompt, boolean)"/> flows.
/// </summary>
/// <remarks>
/// Implementors may control chat clearing and provide default session data.
/// </remarks>
public interface IDreamPrompt {
    /// <summary>Returns the prompt text to display for the player.</summary>
    String promptText(Player player);

    /// <summary>Default session data inserted when the prompt begins.</summary>
    default HashMap<Object, Object> defaultData(Player player) { return new HashMap<>(); }

    /// <summary>Whether to clear the player's chat on start.</summary>
    default boolean clearPlayerChatOnStart(Player player)   { return true; }
    /// <summary>Whether to clear the player's chat when the step repeats.</summary>
    default boolean clearPlayerChatOnRestart(Player player) { return true; }
    /// <summary>Whether to clear the player's chat on end.</summary>
    default boolean clearPlayerChatOnEnd(Player player)     { return true; }

    /// <summary>
    /// Called when the player submits input for this prompt.
    /// </summary>
    /// <returns>Consumer receiving (player, input, context) triplet.</returns>
    Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponseCallback();

    /// <summary>
    /// Called if the prompt continues (same step repeats).
    /// </summary>
    /// <returns>Consumer receiving (player, input, context) triplet.</returns>
    default Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onConversationRestartCallback() {
        return triplet -> { /* no-op */ };
    }

    /// <summary>
    /// Called when the prompt ends normally or via abandon.
    /// </summary>
    /// <returns>Consumer receiving (player, input, context) triplet.</returns>
    Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEndConversationCallback();
}