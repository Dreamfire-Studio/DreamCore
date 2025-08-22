package com.dreamfirestudios.dreamcore.DreamSmartInvs.content;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;

public interface InventoryProvider {

    CompletableFuture<Void> init(Player player, InventoryContents contents);

    default void update(Player player, InventoryContents contents) {

    }

    default void closeinventory(Player player, InventoryContents contents, Inventory inventory) {

    }

}
