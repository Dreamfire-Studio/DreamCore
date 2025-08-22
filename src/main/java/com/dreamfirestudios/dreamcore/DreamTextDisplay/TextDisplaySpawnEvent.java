package com.dreamfirestudios.dreamcore.DreamTextDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a <see cref="TextDisplay"/> is spawned by <see cref="DreamTextDisplay"/>.
/// </summary>
/// <remarks>
/// Use to apply extra initialization such as metadata or PDC tags.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onSpawn(TextDisplaySpawnEvent e) {
///     e.getTextDisplay().text(Component.text("Overridden!"));
/// }
/// </code>
/// </example>
@Getter
public class TextDisplaySpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    /// <summary>The spawned TextDisplay instance.</summary>
    private final TextDisplay textDisplay;

    /// <summary>Constructs and dispatches the event.</summary>
    public TextDisplaySpawnEvent(TextDisplay textDisplay){
        this.textDisplay = textDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    public @NotNull HandlerList getHandlers() { return handlers; }
}