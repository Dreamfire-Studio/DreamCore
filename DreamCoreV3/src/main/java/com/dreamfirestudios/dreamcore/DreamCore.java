package com.dreamfirestudios.dreamcore;

import com.dreamfirestudios.dreamcore.DreamActionBar.DreamActionBar;
import com.dreamfirestudios.dreamcore.DreamBlockMask.DreamBlockMask;
import com.dreamfirestudios.dreamcore.DreamBook.DreamBook;
import com.dreamfirestudios.dreamcore.DreamBossBar.DreamBossBar;
import com.dreamfirestudios.dreamcore.DreamCam.DreamCamPath;
import com.dreamfirestudios.dreamcore.DreamEnchantment.IDreamEnchantment;
import com.dreamfirestudios.dreamcore.DreamEntityMask.DreamEntityMask;
import com.dreamfirestudios.dreamcore.DreamFakeBlock.DreamFakeBlock;
import com.dreamfirestudios.dreamcore.DreamHologram.DreamHologram;
import com.dreamfirestudios.dreamcore.DreamLocationLimiter.DreamLocationLimiter;
import com.dreamfirestudios.dreamcore.DreamLoop.IDreamLoop;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamVariableTest;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class DreamCore extends JavaPlugin {

    public static DreamCore DreamCore;
    public static LuckPerms LuckPerms;
    private static com.dreamfirestudios.dreamcore.DreamfireStorage.DreamfireStorageManager DreamfireStorageManager;

    public static final LinkedHashMap<UUID, DreamActionBar> DreamActionBars = new LinkedHashMap<>();
    public static final LinkedHashMap<Class<?>, DreamVariableTest> DreamVariableTests = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamBook> DreamBooks = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamBossBar> DreamBossBars = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamBlockMask> DreamBlockMasks = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamCamPath> DreamCamPaths = new LinkedHashMap<>();
    public static final ArrayList<IDreamEnchantment> IDreamEnchantments = new ArrayList<>();
    public static final LinkedHashMap<UUID, List<UUID>> DreamVanishs = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamEntityMask> DreamEntityMasks = new LinkedHashMap<>();
    public static final LinkedHashMap<String, DreamFakeBlock> DreamFakeBlocks = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamHologram> DreamHolograms = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamLocationLimiter> DreamLocationLimiters = new LinkedHashMap<>();
    public static final LinkedHashMap<String, IDreamLoop> IDreamLoops = new LinkedHashMap<>();

    public static com.dreamfirestudios.dreamcore.DreamfireStorage.DreamfireStorageManager GetDreamfireStorageManager(){return DreamfireStorageManager;}

    @Override
    public void onEnable() {
        DreamCore = this;
        LuckPerms = LuckPermsProvider.get();
    }

    public void OneTickClasses(){

    }

    public void TwentyTickClasses(){

    }
}
