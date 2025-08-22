package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired when a player is added as an observer to a {@link DreamFakeBlock}.
 * </summary>
 *
 * <remarks>
 * Dispatched synchronously from the constructor via the Bukkit PluginManager.
 * </remarks>
 */
@Getter
public class FakeBlockObserverAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamFakeBlock fakeBlock;
    private final Player player;

    /**
     * <summary>Constructs and immediately dispatches the event.</summary>
     * <param name="fakeBlock">The fake block being observed.</param>
     * <param name="player">The player added as an observer.</param>
     */
    public FakeBlockObserverAddedEvent(DreamFakeBlock fakeBlock, Player player) {
        this.fakeBlock = fakeBlock;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /** <returns>The static handler list required by Bukkit.</returns> */
    public static HandlerList getHandlerList() { return handlers; }

    /** <returns>The handler list instance.</returns> */
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}