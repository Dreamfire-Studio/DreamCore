/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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