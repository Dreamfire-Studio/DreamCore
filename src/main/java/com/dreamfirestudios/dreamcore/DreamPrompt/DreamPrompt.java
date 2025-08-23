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

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/// <summary>
/// Single-step string prompt with start/response/restart/end events and safe cleanup.
/// </summary>
/// <remarks>
/// Features:
/// <list type="bullet">
///   <item><description>Start/response/restart/end events</description></item>
///   <item><description>Optional chat-clearing on start/restart/end</description></item>
///   <item><description>Safe cleanup via <see cref="ConversationAbandonedListener"/></description></item>
///   <item><description>Convenience static launcher for <see cref="IDreamPrompt"/> flows</description></item>
/// </list>
/// To end a running prompt from callbacks, put <see cref="END_KEY"/> = <c>Boolean.TRUE</c>
/// into the session data.
/// </remarks>
/// <example>
/// <code>
/// IDreamPrompt myPrompt = new MyPrompt();
/// DreamPrompt.start(player, myPrompt, true);
/// </code>
/// </example>
public class DreamPrompt extends StringPrompt {

    /// <summary>SessionData key that signals the prompt to end on next input cycle.</summary>
    public static final String END_KEY = "DreamfirePrompt::END";

    private static final int CLEAR_LINES = 100;

    private final String promptText;
    private final boolean clearOnRestart;
    private final boolean clearOnEnd;

    private final Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponse;
    private final Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onRestart;
    private final Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEnd;

    /// <summary>
    /// Creates a prompt instance (normally constructed through the Builder or IDreamPrompt wrapper).
    /// </summary>
    /// <param name="promptText">The text to display for the prompt (MiniMessage supported).</param>
    /// <param name="clearOnRestart">Whether to clear the chat when the same step repeats.</param>
    /// <param name="clearOnEnd">Whether to clear the chat on end.</param>
    /// <param name="onResponse">Callback invoked when input is received.</param>
    /// <param name="onRestart">Callback invoked when prompt continues.</param>
    /// <param name="onEnd">Callback invoked when prompt ends.</param>
    public DreamPrompt(
            @NotNull String promptText,
            boolean clearOnRestart,
            boolean clearOnEnd,
            @NotNull Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponse,
            @NotNull Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onRestart,
            @NotNull Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEnd
    ) {
        this.promptText = Objects.requireNonNull(promptText, "promptText");
        this.clearOnRestart = clearOnRestart;
        this.clearOnEnd = clearOnEnd;
        this.onResponse = Objects.requireNonNull(onResponse, "onResponse");
        this.onRestart = Objects.requireNonNull(onRestart, "onRestart");
        this.onEnd = Objects.requireNonNull(onEnd, "onEnd");
    }

    /// <summary>
    /// Formats the visible prompt line (MiniMessage → Component → plain text).
    /// </summary>
    /// <param name="context">Conversation context.</param>
    /// <returns>Displayable string.</returns>
    @Override
    public String getPromptText(ConversationContext context) {
        return PlainTextComponentSerializer.plainText()
                .serialize(DreamMessageFormatter.format(promptText, DreamMessageSettings.all()));
    }

    /// <summary>
    /// Handles user input for this step. If <see cref="END_KEY"/> is set to true,
    /// the conversation ends after calling the end callback.
    /// </summary>
    /// <param name="context">Conversation context.</param>
    /// <param name="input">User input.</param>
    /// <returns>This prompt again (to continue) or <see cref="Prompt#END_OF_CONVERSATION"/>.</returns>
    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!(context.getForWhom() instanceof Player player)) return END_OF_CONVERSATION;

        // Fire response callback + event
        DreamPromptTriplet<Player, String, ConversationContext> payload =
                new DreamPromptTriplet<>(player, input, context);
        onResponse.accept(payload);
        new PromptResponseEvent(player, input, context);

        // Should we end?
        boolean endNow = Boolean.TRUE.equals(getSessionFlag(context.getAllSessionData(), END_KEY));
        if (endNow) {
            if (clearOnEnd) clearChat(context);
            onEnd.accept(payload);
            new PromptEndedEvent(player, input, context, PromptEndedEvent.EndReason.NORMAL);
            DreamCore.Conversations.remove(player.getUniqueId());
            return END_OF_CONVERSATION;
        }

        // Continue same prompt (single-step loop)
        if (clearOnRestart) clearChat(context);
        onRestart.accept(payload);
        new PromptRestartEvent(player, input, context);
        return this;
    }

    /// <summary>
    /// Looks up a session flag from the context's session data.
    /// </summary>
    private static Object getSessionFlag(Map<Object, Object> data, String key) {
        return data == null ? null : data.get(key);
    }

    /// <summary>
    /// Clears the user's visible chat by sending N blank lines.
    /// </summary>
    private static void clearChat(ConversationContext ctx) {
        for (int i = 0; i < CLEAR_LINES; i++) {
            ctx.getForWhom().sendRawMessage("");
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*  Static convenience entrypoint for IDreamPrompt flows                                                          */
    /* -------------------------------------------------------------------------------------------------------------- */

    /// <summary>
    /// Launches a conversation for the given player using an <see cref="IDreamPrompt"/> provider.
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="prompt">Prompt adapter providing text and callbacks.</param>
    /// <param name="overrideExisting">If false and a conversation exists, this call is ignored.</param>
    /// <example>
    /// <code>
    /// DreamPrompt.start(player, new MyPrompt(), true);
    /// </code>
    /// </example>
    public static void start(Player player, IDreamPrompt prompt, boolean overrideExisting) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(prompt, "prompt");

        Bukkit.getScheduler().runTask(DreamCore.DreamCore, () -> {
            var existing = DreamCore.Conversations.get(player.getUniqueId());
            if (existing != null && !overrideExisting) return;

            PromptStartedEvent startEvent = new PromptStartedEvent(player);
            if (startEvent.isCancelled()) return;

            if (existing != null) existing.abandon();

            if (prompt.clearPlayerChatOnStart(player)) {
                for (int i = 0; i < CLEAR_LINES; i++) player.sendMessage("");
            }

            ConversationFactory factory = new ConversationFactory(DreamCore.DreamCore)
                    .withFirstPrompt(new DreamPrompt(
                            prompt.promptText(player),
                            prompt.clearPlayerChatOnRestart(player),
                            prompt.clearPlayerChatOnEnd(player),
                            prompt.onResponseCallback(),
                            prompt.onConversationRestartCallback(),
                            prompt.onEndConversationCallback()
                    ))
                    .withInitialSessionData(new HashMap<>(prompt.defaultData(player)))
                    .withModality(false)
                    .withLocalEcho(false)
                    .withEscapeSequence(null)
                    .addConversationAbandonedListener(abandonedEvent -> {
                        // Cleanup on any abandon path not caught by acceptInput()
                        if (abandonedEvent.getContext() != null
                                && abandonedEvent.getContext().getForWhom() instanceof Player p) {
                            DreamCore.Conversations.remove(p.getUniqueId());
                            // Distinguish between natural end and forced abandon
                            new PromptEndedEvent(p, "", abandonedEvent.getContext(),
                                    abandonedEvent.gracefulExit() ? PromptEndedEvent.EndReason.NORMAL
                                            : PromptEndedEvent.EndReason.ABANDONED);
                        }
                    });

            Conversation conversation = factory.buildConversation(player);
            conversation.begin();
            DreamCore.Conversations.put(player.getUniqueId(), conversation);
        });
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*  Builder for ad-hoc prompts (not using IDreamPrompt)                                                            */
    /* -------------------------------------------------------------------------------------------------------------- */

    /// <summary>Creates a new <see cref="Builder"/>.</summary>
    public static Builder builder() { return new Builder(); }

    /// <summary>Backwards-compat alias (kept for older call sites).</summary>
    public static Builder PulsePromptBuilder() { return builder(); }

    /// <summary>
    /// Builder for ad-hoc prompts not using <see cref="IDreamPrompt"/>.
    /// </summary>
    public static final class Builder {
        private final HashMap<Object, Object> defaultData = new HashMap<>();
        private Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponse = t -> {};
        private Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onRestart = t -> {};
        private Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEnd = t -> {};
        private String promptText = "|Enter text: |";
        private boolean clearOnStart = true;
        private boolean clearOnRestart = true;
        private boolean clearOnEnd = true;

        /// <summary>Adds default session data key/value.</summary>
        public Builder addDefaultData(Object key, Object value) { defaultData.put(key, value); return this; }
        /// <summary>Sets prompt text (MiniMessage supported).</summary>
        public Builder promptText(String text) { this.promptText = text; return this; }
        /// <summary>Sets response callback.</summary>
        public Builder onResponse(Consumer<DreamPromptTriplet<Player, String, ConversationContext>> c) { this.onResponse = c; return this; }
        /// <summary>Sets restart callback.</summary>
        public Builder onRestart(Consumer<DreamPromptTriplet<Player, String, ConversationContext>> c) { this.onRestart = c; return this; }
        /// <summary>Sets end callback.</summary>
        public Builder onEnd(Consumer<DreamPromptTriplet<Player, String, ConversationContext>> c) { this.onEnd = c; return this; }
        /// <summary>Clears chat when starting.</summary>
        public Builder clearOnStart(boolean v) { this.clearOnStart = v; return this; }
        /// <summary>Clears chat when repeating.</summary>
        public Builder clearOnRestart(boolean v) { this.clearOnRestart = v; return this; }
        /// <summary>Clears chat when ending.</summary>
        public Builder clearOnEnd(boolean v) { this.clearOnEnd = v; return this; }

        /// <summary>
        /// Starts the conversation for a player.
        /// </summary>
        /// <param name="player">Target player.</param>
        /// <param name="overrideExisting">If false and a conversation exists, this call is ignored.</param>
        /// <example>
        /// <code>
        /// DreamPrompt.builder()
        ///     .promptText("&aSay something:")
        ///     .onResponse(t -> t.player().sendMessage("You said: " + t.input()))
        ///     .startConversation(player, true);
        /// </code>
        /// </example>
        public void startConversation(@NotNull Player player, boolean overrideExisting) {
            Objects.requireNonNull(player, "player");

            Bukkit.getScheduler().runTask(DreamCore.DreamCore, () -> {
                var existing = DreamCore.Conversations.get(player.getUniqueId());
                if (existing != null && !overrideExisting) return;

                PromptStartedEvent startEvent = new PromptStartedEvent(player);
                if (startEvent.isCancelled()) return;

                if (existing != null) existing.abandon();

                if (clearOnStart) {
                    for (int i = 0; i < CLEAR_LINES; i++) player.sendMessage("");
                }

                ConversationFactory factory = new ConversationFactory(DreamCore.DreamCore)
                        .withFirstPrompt(new DreamPrompt(
                                promptText,
                                clearOnRestart,
                                clearOnEnd,
                                onResponse,
                                onRestart,
                                onEnd
                        ))
                        .withInitialSessionData(new HashMap<>(defaultData))
                        .withModality(false)
                        .withLocalEcho(false)
                        .withEscapeSequence(null)
                        .addConversationAbandonedListener(abandonedEvent -> {
                            if (abandonedEvent.getContext() != null
                                    && abandonedEvent.getContext().getForWhom() instanceof Player p) {
                                DreamCore.Conversations.remove(p.getUniqueId());
                                new PromptEndedEvent(p, "", abandonedEvent.getContext(),
                                        abandonedEvent.gracefulExit() ? PromptEndedEvent.EndReason.NORMAL
                                                : PromptEndedEvent.EndReason.ABANDONED);
                            }
                        });

                Conversation c = factory.buildConversation(player);
                c.begin();
                DreamCore.Conversations.put(player.getUniqueId(), c);
            });
        }

        /// <summary>
        /// Cancels a running conversation for a player (if any).
        /// </summary>
        /// <param name="player">Target player.</param>
        public void cancelConversation(@NotNull Player player) {
            Objects.requireNonNull(player, "player");
            var current = DreamCore.Conversations.remove(player.getUniqueId());
            if (current != null) current.abandon();
        }
    }
}