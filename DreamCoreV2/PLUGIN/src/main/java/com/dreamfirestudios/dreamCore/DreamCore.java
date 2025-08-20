package com.dreamfirestudios.dreamCore;

import com.dreamfirestudios.dreamCore.DreamActionBar.DreamActionBar;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.UUID;

public class DreamCore extends JavaPlugin {

    public static DreamCore DreamCore;
    public static LuckPerms LuckPerms;

    public static final LinkedHashMap<UUID, DreamActionBar> DreamActionBars = new LinkedHashMap<>();

    @Override
    public void onEnable() {
        DreamCore = this;
        LuckPerms = LuckPermsProvider.get();
    }
}
