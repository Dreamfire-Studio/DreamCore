package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired after all observers are removed from a {@link DreamFakeBlock}.
 * </summary>
 *
 * <remarks>
 * Indicates the block is no longer presented to any player.
 * Dispatched synchronously from the constructor via the Bukkit PluginManager.
 * </remarks>
 */
@Getter
public class FakeBlockClearedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamFakeBlock fakeBlock;

    /**
     * <summary>Constructs and immediately dispatches the event.</summary>
     * <param name="fakeBlock">The fake block that was cleared.</param>
     */
    public FakeBlockClearedEvent(DreamFakeBlock fakeBlock) {
        this.fakeBlock = fakeBlock;
        Bukkit.getPluginManager().callEvent(this);
    }

    /** <returns>The static handler list required by Bukkit.</returns> */
    public static HandlerList getHandlerList() { return handlers; }

    /** <returns>The handler list instance.</returns> */
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}