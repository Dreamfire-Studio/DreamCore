package com.dreamfirestudios.dreamCore.DreamfireSmartInvs.opener;

import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.InventoryManager;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.SmartInventory;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class SpecialInventoryOpener implements InventoryOpener {

    private static final List<InventoryType> SUPPORTED = ImmutableList.of(
            InventoryType.FURNACE,
            InventoryType.WORKBENCH,
            InventoryType.DISPENSER,
            InventoryType.DROPPER,
            InventoryType.ENCHANTING,
            InventoryType.BREWING,
            InventoryType.ANVIL,
            InventoryType.BEACON,
            InventoryType.HOPPER
    );

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(SmartInventory inv, Player player) {
        InventoryManager manager = inv.getManager();
        Inventory handle = Bukkit.createInventory(player, inv.getType(), inv.getTitle());

        fill(handle, manager.getContents(player).get());

        player.openInventory(handle);
        return handle;
    }

    @Override
    public boolean supports(InventoryType type) {
        return SUPPORTED.contains(type);
    }

}
