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

/**
 * Single-step string prompt with:
 * <ul>
 *   <li>Start/response/restart/end events</li>
 *   <li>Optional chat-clearing on start/restart/end</li>
 *   <li>Safe cleanup via ConversationAbandonedListener</li>
 *   <li>Convenience static launcher for IDreamPrompt flows</li>
 * </ul>
 *
 * <p>To end a running prompt from your callbacks, put {@link #END_KEY} = Boolean.TRUE into the session data.</p>
 */
public class DreamPrompt extends StringPrompt {

    /** SessionData key that signals the prompt to end on next input cycle. */
    public static final String END_KEY = "DreamfirePrompt::END";

    private static final int CLEAR_LINES = 100;

    private final String promptText;
    private final boolean clearOnRestart;
    private final boolean clearOnEnd;

    private final Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponse;
    private final Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onRestart;
    private final Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEnd;

    /**
     * Create a prompt instance (normally constructed through the Builder or IDreamPrompt wrapper).
     */
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

    /**
     * Formats the visible prompt line (MiniMessage -> Component -> plain text).
     */
    @Override
    public String getPromptText(ConversationContext context) {
        return PlainTextComponentSerializer.plainText()
                .serialize(DreamMessageFormatter.format(promptText, DreamMessageSettings.all()));
    }

    /**
     * Handles user input for this step. If {@link #END_KEY} is true in session data,
     * the conversation ends after calling the end callback.
     */
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

    private static Object getSessionFlag(Map<Object, Object> data, String key) {
        return data == null ? null : data.get(key);
    }

    private static void clearChat(ConversationContext ctx) {
        for (int i = 0; i < CLEAR_LINES; i++) {
            ctx.getForWhom().sendRawMessage("");
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*  Static convenience entrypoint for IDreamPrompt flows                                                          */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Launches a conversation for the given player using an {@link IDreamPrompt} provider.
     *
     * @param overrideExisting if false and a conversation exists for that player, this call is ignored
     */
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

    public static Builder builder() { return new Builder(); }

    /** Backwards-compat alias (kept for older call sites) */
    public static Builder PulsePromptBuilder() { return builder(); }

    public static final class Builder {
        private final HashMap<Object, Object> defaultData = new HashMap<>();
        private Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponse = t -> {};
        private Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onRestart = t -> {};
        private Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEnd = t -> {};
        private String promptText = "|Enter text: |";
        private boolean clearOnStart = true;
        private boolean clearOnRestart = true;
        private boolean clearOnEnd = true;

        public Builder addDefaultData(Object key, Object value) { defaultData.put(key, value); return this; }
        public Builder promptText(String text) { this.promptText = text; return this; }
        public Builder onResponse(Consumer<DreamPromptTriplet<Player, String, ConversationContext>> c) { this.onResponse = c; return this; }
        public Builder onRestart(Consumer<DreamPromptTriplet<Player, String, ConversationContext>> c) { this.onRestart = c; return this; }
        public Builder onEnd(Consumer<DreamPromptTriplet<Player, String, ConversationContext>> c) { this.onEnd = c; return this; }
        public Builder clearOnStart(boolean v) { this.clearOnStart = v; return this; }
        public Builder clearOnRestart(boolean v) { this.clearOnRestart = v; return this; }
        public Builder clearOnEnd(boolean v) { this.clearOnEnd = v; return this; }

        /**
         * Start the conversation for a player.
         * @param overrideExisting if false and a conversation exists for that player, this call is ignored
         */
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

        public void cancelConversation(@NotNull Player player) {
            Objects.requireNonNull(player, "player");
            var current = DreamCore.Conversations.remove(player.getUniqueId());
            if (current != null) current.abandon();
        }
    }
}