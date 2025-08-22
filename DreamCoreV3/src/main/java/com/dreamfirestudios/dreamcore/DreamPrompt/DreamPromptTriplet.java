package com.dreamfirestudios.dreamcore.DreamPrompt;

/** Simple carrier for (player, input, context). */
public record DreamPromptTriplet<Player, Input, Context>(Player player, Input input, Context context) { }