package com.dreamfirestudios.dreamcore.DreamArmorStand;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when an <see cref="ArmorStand"/> has its glowing state changed.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// It provides the armor stand reference and whether the glowing effect is being enabled or disabled.
/// </remarks>
@Getter
public class ArmorStandGlowEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The armor stand whose glowing state changed.
    /// </summary>
    private final ArmorStand armorStand;

    /// <summary>
    /// Whether the armor stand is glowing (<c>true</c>) or not (<c>false</c>).
    /// </summary>
    private final boolean glowing;

    /// <summary>
    /// Creates a new <see cref="ArmorStandGlowEvent"/>.
    /// </summary>
    /// <param name="armorStand">The armor stand affected.</param>
    /// <param name="glowing">The new glowing state of the armor stand.</param>
    public ArmorStandGlowEvent(ArmorStand armorStand, boolean glowing) {
        this.armorStand = armorStand;
        this.glowing = glowing;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the handler list for this event.
    /// </summary>
    /// <returns>The static handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <inheritdoc />
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
