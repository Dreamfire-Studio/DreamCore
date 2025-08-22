package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired when a {@link DreamFakeBlock}'s material is updated and pushed to all observers.
 * </summary>
 *
 * <remarks>
 * Dispatched synchronously from the constructor via the Bukkit PluginManager.
 * </remarks>
 * <example>
 * <![CDATA[
 * @EventHandler
 * public void onUpdated(FakeBlockUpdatedEvent e) {
 *     // react to material changes
 * }
 * ]]>
 * </example>
 */
@Getter
public class FakeBlockUpdatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamFakeBlock fakeBlock;
    private final Material newMaterial;

    /**
     * <summary>Constructs and immediately dispatches the event.</summary>
     * <param name="fakeBlock">The fake block being updated.</param>
     * <param name="newMaterial">The new material applied.</param>
     */
    public FakeBlockUpdatedEvent(DreamFakeBlock fakeBlock, Material newMaterial) {
        this.fakeBlock = fakeBlock;
        this.newMaterial = newMaterial;
        Bukkit.getPluginManager().callEvent(this);
    }

    /** <returns>The static handler list required by Bukkit.</returns> */
    public static HandlerList getHandlerList() { return handlers; }

    /** <returns>The handler list instance.</returns> */
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}