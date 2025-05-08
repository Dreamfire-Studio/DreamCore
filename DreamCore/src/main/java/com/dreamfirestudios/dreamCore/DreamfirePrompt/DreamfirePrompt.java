package com.dreamfirestudios.dreamCore.DreamfirePrompt;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import com.dreamfirestudios.dreamCore.DreamfirePrompt.Event.PromptStartedEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

public class DreamfirePrompt extends StringPrompt {

    public static void IDreamfirePrompt(Player player, IDreamfirePrompt iDreamfirePrompt, boolean overrideExisting){
        Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {
            var currentConversation = DreamCore.GetDreamfireCore().GetConversation(player.getUniqueId());
            if (currentConversation != null && !overrideExisting) return;
            if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerDreamfirePrompt, player.getUniqueId())) return;
            if(new PromptStartedEvent(player).isCancelled()) return;
            if (currentConversation != null) currentConversation.abandon();
            if (iDreamfirePrompt.clearPlayerChatOnStart(player)) for(var i = 0; i < 100; i++) player.sendMessage("");
            var factory = new ConversationFactory(DreamCore.GetDreamfireCore());
            var conversation = factory.withInitialSessionData(iDreamfirePrompt.defaultData(player))
                    .withFirstPrompt(new DreamfirePrompt(
                            iDreamfirePrompt.promptText(player),
                            iDreamfirePrompt.clearPlayerChatOnRestart(player),
                            iDreamfirePrompt.clearPlayerChatOnEnd(player),
                            iDreamfirePrompt.onResponseCallback(),
                            iDreamfirePrompt.onConversationRestartCallback(),
                            iDreamfirePrompt.onEndConversationCallback())
                    )
                    .buildConversation(player);
            conversation.begin();
            DreamCore.GetDreamfireCore().AddConversation(player.getUniqueId(), conversation);
        });
    }

    public static final String EndKey = "DreamfirePrompt::END";

    private final String promptText;
    private final Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onResponseCallback;
    private final Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onConversationRestartCallback;
    private final Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onEndConversationCallback;
    private final boolean clearPlayerChatOnRestart;
    private final boolean clearPlayerChatOnEnd;


    public DreamfirePrompt(String promptText, boolean clearPlayerChatOnRestart, boolean clearPlayerChatOnEnd,
                           Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onResponseCallback,
                           Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onConversationRestartCallback,
                           Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onEndConversationCallback) {
        this.promptText = promptText;
        this.onResponseCallback = onResponseCallback;
        this.onConversationRestartCallback = onConversationRestartCallback;
        this.onEndConversationCallback = onEndConversationCallback;
        this.clearPlayerChatOnRestart = clearPlayerChatOnRestart;
        this.clearPlayerChatOnEnd = clearPlayerChatOnEnd;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return PlainTextComponentSerializer.plainText().serialize(DreamfireMessage.formatMessage(promptText, null));
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!input.isBlank() && context.getForWhom() instanceof Player player) {
            onResponseCallback.accept(new DreamfirePromptTriplet<>(player, input, context));

            boolean endConversation = (boolean) context.getAllSessionData().getOrDefault(EndKey, false);

            if (endConversation) {
                if (clearPlayerChatOnEnd) for(var i = 0; i < 100; i++) context.getForWhom().sendRawMessage("");
                onEndConversationCallback.accept(new DreamfirePromptTriplet<>(player, input, context));
                DreamCore.GetDreamfireCore().DeleteConversation(player.getUniqueId());
                return Prompt.END_OF_CONVERSATION;
            }

            if (clearPlayerChatOnRestart) for(var i = 0; i < 100; i++) context.getForWhom().sendRawMessage("");
            onConversationRestartCallback.accept(new DreamfirePromptTriplet<>(player, input, context));
        }
        return this;
    }

    public static Builder PulsePromptBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final HashMap<Object, Object> defaultData = new HashMap<>();
        private Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onResponseCallback = triplet -> {};
        private Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onConversationRestartCallback = triplet -> {};
        private Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onEndConversationCallback = triplet -> {};
        private String promptText = "|Enter text: |";
        private boolean clearPlayerChatOnStart = true;
        private boolean clearPlayerChatOnRestart = true;
        private boolean clearPlayerChatOnEnd = true;
        private boolean translateColorCodes = true;
        private boolean translateHexCodes = true;

        public Builder addDefaultData(String key, String value) {
            defaultData.put(key, value);
            return this;
        }

        public Builder promptText(String promptText) {
            this.promptText = promptText;
            return this;
        }

        public Builder onResponseCallback(Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> callback) {
            this.onResponseCallback = callback;
            return this;
        }

        public Builder onConversationRestartCallback(Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> callback) {
            this.onConversationRestartCallback = callback;
            return this;
        }

        public Builder onEndConversationCallback(Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> callback) {
            this.onEndConversationCallback = callback;
            return this;
        }

        public Builder clearPlayerChatOnStart(boolean value) {
            this.clearPlayerChatOnStart = value;
            return this;
        }

        public Builder clearPlayerChatOnRestart(boolean value) {
            this.clearPlayerChatOnRestart = value;
            return this;
        }

        public Builder clearPlayerChatOnEnd(boolean value) {
            this.clearPlayerChatOnEnd = value;
            return this;
        }

        public Builder translateColorCodes(boolean value) {
            this.translateColorCodes = value;
            return this;
        }

        public Builder translateHexCodes(boolean value) {
            this.translateHexCodes = value;
            return this;
        }

        public void startConversation(Player targetPlayer, boolean overrideExisting) {
            Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {
                var currentConversation = DreamCore.GetDreamfireCore().GetConversation(targetPlayer.getUniqueId());
                if (currentConversation != null && !overrideExisting) return;
                if (currentConversation != null) currentConversation.abandon();
                if (clearPlayerChatOnStart){
                    for(var i = 0; i < 100; i++) targetPlayer.sendMessage("");
                }
                var factory = new ConversationFactory(DreamCore.GetDreamfireCore());
                var conversation = factory.withInitialSessionData(defaultData).withFirstPrompt(new DreamfirePrompt(
                                promptText, clearPlayerChatOnRestart, clearPlayerChatOnEnd,
                                onResponseCallback, onConversationRestartCallback, onEndConversationCallback))
                        .buildConversation(targetPlayer);
                conversation.begin();
                DreamCore.GetDreamfireCore().AddConversation(targetPlayer.getUniqueId(), conversation);
            });
        }

        public void cancelConversation(Player player) {
            var currentConversation = DreamCore.GetDreamfireCore().DeleteConversation(player.getUniqueId());
            if (currentConversation != null) currentConversation.abandon();
        }
    }
}
