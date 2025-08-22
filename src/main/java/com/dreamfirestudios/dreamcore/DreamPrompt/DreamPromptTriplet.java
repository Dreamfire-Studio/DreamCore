package com.dreamfirestudios.dreamcore.DreamPrompt;

/// <summary>
/// Simple carrier for (player, input, context) used by prompt callbacks.
/// </summary>
/// <typeparam name="Player">Player type (Bukkit).</typeparam>
/// <typeparam name="Input">Input type (usually <see cref="String"/>).</typeparam>
/// <typeparam name="Context">Conversation context type.</typeparam>
/// <example>
/// <code>
/// Consumer&lt;DreamPromptTriplet&lt;Player,String,ConversationContext&gt;&gt; c = t -&gt; {
///     t.player().sendMessage("You said: " + t.input());
/// };
/// </code>
/// </example>
public record DreamPromptTriplet<Player, Input, Context>(Player player, Input input, Context context) { }