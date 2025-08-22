package com.dreamfirestudios.dreamcore.DreamItemDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired immediately after an <see cref="ItemDisplay"/> is spawned and configured
/// by <see cref="DreamItemDisplay.ItemDisplayBuilder#spawn(org.bukkit.inventory.ItemStack)"/>.
/// </summary>
/// <remarks>
/// This event currently calls itself through the plugin manager inside its constructor,
/// matching the pattern shown. If you prefer consistency with your other events,
/// we can switch to a static <c>fire(...)</c> helper (no behavior changes).
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onItemDisplaySpawn(ItemDisplaySpawnEvent e) {
///     e.getItemDisplay().setPersistent(true);
/// }
/// </code>
/// </example>
@Getter
public class ItemDisplaySpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /// <summary>The newly spawned <see cref="ItemDisplay"/>.</summary>
    private final ItemDisplay itemDisplay;

    /// <summary>
    /// Constructs and immediately fires the event through the plugin manager.
    /// </summary>
    /// <param name="itemDisplay">The spawned <see cref="ItemDisplay"/>.</param>
    public ItemDisplaySpawnEvent(ItemDisplay itemDisplay){
        this.itemDisplay = itemDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Static accessor required by Bukkit to obtain the shared handler list.
    /// </summary>
    /// <returns>The shared handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <summary>
    /// Instance accessor required by Bukkit for listener registration.
    /// </summary>
    /// <returns>The shared handler list.</returns>
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}