package com.dreamfirestudios.dreamCore.DreamfireSmartInvs;

import org.bukkit.plugin.java.JavaPlugin;

public class SmartInvsPlugin {

    private InventoryManager invManager;
    public InventoryManager manager() { return invManager; }

    public SmartInvsPlugin(JavaPlugin javaPlugin){
        invManager = new InventoryManager(javaPlugin);
        invManager.init();
    }

}
