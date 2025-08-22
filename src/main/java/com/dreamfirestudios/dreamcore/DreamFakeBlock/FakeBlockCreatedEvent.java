package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired when a new {@link DreamFakeBlock} is created and registered.
 * </summary>
 *
 * <remarks>
 * Dispatched synchronously from the constructor via the Bukkit PluginManager.
 * </remarks>
 * <example>
 * <![CDATA[
 * @EventHandler
 * public void onCreated(FakeBlockCreatedEvent e) {
 *     getLogger().info("FakeBlock created at " + e.getFakeBlock().getLocation()
 *         + " with id " + e.getId());
 * }
 * ]]>
 * </example>
 */
@Getter
public class FakeBlockCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamFakeBlock fakeBlock;
    private final String id;

    /**
     * <summary>Constructs and immediately dispatches the event.</summary>
     * <param name="fakeBlock">The created fake block instance.</param>
     * <param name="id">The registry ID used for this fake block.</param>
     */
    public FakeBlockCreatedEvent(DreamFakeBlock fakeBlock, String id) {
        this.fakeBlock = fakeBlock;
        this.id = id;
        Bukkit.getPluginManager().callEvent(this);
    }

    /** <returns>The static handler list required by Bukkit.</returns> */
    public static HandlerList getHandlerList() { return handlers; }

    /** <returns>The handler list instance.</returns> */
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}