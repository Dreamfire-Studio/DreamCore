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