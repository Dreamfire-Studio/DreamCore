package com.dreamfirestudios.dreamcore;

import com.dreamfirestudios.dreamcore.DreamActionBar.DreamActionBar;
import com.dreamfirestudios.dreamcore.DreamBook.DreamBook;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamVariableTest;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.UUID;

public class DreamCore extends JavaPlugin {

    public static DreamCore DreamCore;
    public static LuckPerms LuckPerms;
    private static com.dreamfirestudios.dreamcore.DreamfireStorage.DreamfireStorageManager DreamfireStorageManager;

    public static final LinkedHashMap<UUID, DreamActionBar> DreamActionBars = new LinkedHashMap<>();
    public static final LinkedHashMap<Class<?>, DreamVariableTest> DreamVariableTests = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamBook> DreamBooks = new LinkedHashMap<>();

    public static com.dreamfirestudios.dreamcore.DreamfireStorage.DreamfireStorageManager GetDreamfireStorageManager(){return DreamfireStorageManager;}

    @Override
    public void onEnable() {
        DreamCore = this;
        LuckPerms = LuckPermsProvider.get();
    }
}
