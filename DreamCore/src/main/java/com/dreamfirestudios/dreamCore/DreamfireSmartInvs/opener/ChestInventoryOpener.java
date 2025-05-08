package com.dreamfirestudios.dreamCore.DreamfireSmartInvs.opener;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.InventoryManager;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.SmartInventory;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ChestInventoryOpener implements InventoryOpener {

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(SmartInventory inv, Player player) {
        Preconditions.checkArgument(inv.getColumns() == 9,
                "The column count for the chest inventory must be 9, found: %s.", inv.getColumns());
        Preconditions.checkArgument(inv.getRows() >= 1 && inv.getRows() <= 6,
                "The row count for the chest inventory must be between 1 and 6, found: %s", inv.getRows());

        InventoryManager manager = inv.getManager();
        Inventory handle = Bukkit.createInventory(player, inv.getRows() * inv.getColumns(), inv.getTitle());

        fill(handle, manager.getContents(player).get());
        Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {
            player.openInventory(handle);
        });

        return handle;
    }

    @Override
    public boolean supports(InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST;
    }

}
