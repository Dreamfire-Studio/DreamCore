/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dreamfirestudios.dreamcore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.dreamfirestudios.dreamcore.DreamActionBar.DreamActionBar;
import com.dreamfirestudios.dreamcore.DreamBlockMask.DreamBlockMask;
import com.dreamfirestudios.dreamcore.DreamBook.DreamBook;
import com.dreamfirestudios.dreamcore.DreamBossBar.DreamBossBar;
import com.dreamfirestudios.dreamcore.DreamCam.DreamCamPath;
import com.dreamfirestudios.dreamcore.DreamEnchantment.IDreamEnchantment;
import com.dreamfirestudios.dreamcore.DreamEntityMask.DreamEntityMask;
import com.dreamfirestudios.dreamcore.DreamEvent.DreamPlayerMoveEvent;
import com.dreamfirestudios.dreamcore.DreamFakeBlock.DreamFakeBlock;
import com.dreamfirestudios.dreamcore.DreamHologram.DreamHologram;
import com.dreamfirestudios.dreamcore.DreamItems.IDreamItemStack;
import com.dreamfirestudios.dreamcore.DreamJava.DreamClassAPI;
import com.dreamfirestudios.dreamcore.DreamKeyPressed.*;
import com.dreamfirestudios.dreamcore.DreamLocationLimiter.DreamLocationLimiter;
import com.dreamfirestudios.dreamcore.DreamLoop.IDreamLoop;
import com.dreamfirestudios.dreamcore.DreamPlaceholder.DreamPlaceholderManager;
import com.dreamfirestudios.dreamcore.DreamScoreboard.DreamScoreboard;
import com.dreamfirestudios.dreamcore.DreamStopwatch.DreamStopwatch;
import com.dreamfirestudios.dreamcore.DreamTeleport.DreamTeleport;
import com.dreamfirestudios.dreamcore.DreamVanish.DreamVanish;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamVariableTest;
import com.dreamfirestudios.dreamcore.DreamSmartInvs.SmartInvsPlugin;
import com.dreamfirestudios.dreamcore.DreamWorld.DreamWorld;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.conversations.Conversation;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class DreamCore extends JavaPlugin {

    public static DreamCore DreamCore;
    public static LuckPerms LuckPerms;
    public static ProtocolManager ProtocolManager;
    public static DreamPlaceholderManager DreamPlaceholderManager;
    public static com.dreamfirestudios.dreamcore.DreamfireStorage.DreamfireStorageManager DreamfireStorageManager;
    public static SmartInvsPlugin SmartInvsPlugin;
    public static DreamPlayerMoveMonitor DreamPlayerMoveMonitor;
    public static IDreamKeyManager IDreamKeyManager;

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
    public static final LinkedHashMap<UUID, IDreamLoop> IDreamLoops = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, Conversation> Conversations = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamScoreboard> DreamScoreboards = new LinkedHashMap<>();
    public static final LinkedHashMap<UUID, DreamWorld> DreamWorlds = new LinkedHashMap<>();
    public static final LinkedHashMap<String, IDreamItemStack> IDreamItemStacks = new LinkedHashMap<>();
    public static final ArrayList<IDreamKeyPressed> DreamKeyPressedListeners = new ArrayList<>();
    public static final ArrayList<IDreamKeyPatternSpec> DreamKeyPatternSpecs = new ArrayList<>();

    @Override
    public void onEnable() {
        DreamCore = this;
        LuckPerms = LuckPermsProvider.get();
        IDreamKeyManager = new DreamKeyManager();
        ProtocolManager = ProtocolLibrary.getProtocolManager();
        DreamPlaceholderManager = new DreamPlaceholderManager("dreamcore", "Dreamfire Studios", "1.0.0");
        SmartInvsPlugin = new SmartInvsPlugin(this);
        DreamPlayerMoveMonitor = new DreamPlayerMoveMonitor(this);
        getServer().getPluginManager().registerEvents(new DreamKeyBukkitAdapter(IDreamKeyManager), this);
        DreamClassAPI.RegisterClasses(this);
    }

    @Override
    public void onDisable() {
        for(var blockMask : DreamBlockMasks.values().stream().toList()) blockMask.stop();
        for(var dreamfireLoop : IDreamLoops.values().stream().toList()) dreamfireLoop.CancelLoop();
        for(var dreamfireCamPath : DreamCamPaths.values().stream().toList()) dreamfireCamPath.onDisable();
        for(var dreamfireEntityMask : DreamEntityMasks.values().stream().toList()) dreamfireEntityMask.stop();
        for(var dreamfireHologram : DreamHolograms.values().stream().toList()) dreamfireHologram.deleteHologram();
    }

    public void OneTickClasses(){
        DreamActionBars.values().removeIf(DreamActionBar::displayNextFrame);
        for(var dreamfireBossBar : DreamBossBars.values()) dreamfireBossBar.displayNextFrame();
        for(var dreamfireLocationLimiter : DreamLocationLimiters.values()) dreamfireLocationLimiter.tickLocationLimiter();
        for(var dreamfireFakeBlock : DreamFakeBlocks.values()) dreamfireFakeBlock.displayNextFrame();
        for(var dreamfireScoreBoard : DreamScoreboards.values()) dreamfireScoreBoard.displayNextFrame();
        for(var dreamfireWorld : DreamWorlds.values()) dreamfireWorld.TickWorld();
        IDreamKeyManager.tick();
    }

    public void TwentyTickClasses(){
        DreamVanish.updateVanishOnAllPlayers();
        for(var dreamfireBook : DreamBooks.values()) dreamfireBook.displayNextFrame();
        for(var blockMask : DreamBlockMasks.values()) blockMask.displayNextFrame();
        for(var dreamfireEntityMask : DreamEntityMasks.values()) dreamfireEntityMask.displayNextFrame();
        for(var dreamfireHologram : DreamHolograms.values()) dreamfireHologram.displayNextFrame();
        DreamPlayerMoveMonitor.tick();
    }


}
