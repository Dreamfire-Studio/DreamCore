package com.dreamfirestudios.dreamCore.DreamfirePrompt;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

public interface IDreamfirePrompt {
    String promptText(Player player);
    default HashMap<Object, Object> defaultData(Player player){return new HashMap<>();};
    default boolean clearPlayerChatOnStart(Player player){return true;}
    default boolean clearPlayerChatOnRestart(Player player){return true;}
    default boolean clearPlayerChatOnEnd(Player player){return true;}
    Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onResponseCallback();
    default Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onConversationRestartCallback(){
        return playerStringConversationContextDreamfirePromptTriplet -> {

        };
    }
    Consumer<DreamfirePromptTriplet<Player, String, ConversationContext>> onEndConversationCallback();
}
