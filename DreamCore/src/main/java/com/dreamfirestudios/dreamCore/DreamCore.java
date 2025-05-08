package com.dreamfirestudios.dreamCore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import com.dreamfirestudios.dreamCore.DreamfireBook.DreamfireBook;
import com.dreamfirestudios.dreamCore.DreamfireBlockMask.DreamfireBlockMask;
import com.dreamfirestudios.dreamCore.DreamfireBossBar.DreamfireBossBar;
import com.dreamfirestudios.dreamCore.DreamfireCam.DreamfireCamPath;
import com.dreamfirestudios.dreamCore.DreamfireEnchantment.IDreamfireEnchantment;
import com.dreamfirestudios.dreamCore.DreamfireEntityMask.DreamfireEntityMask;
import com.dreamfirestudios.dreamCore.DreamfireEvents.IDreamfireEvents;
import com.dreamfirestudios.dreamCore.DreamfireFakeBlock.DreamfireFakeBlock;
import com.dreamfirestudios.dreamCore.DreamfireHologram.DreamfireHologram;
import com.dreamfirestudios.dreamCore.DreamfireItems.IDreamfireItemStack;
import com.dreamfirestudios.dreamCore.DreamfireJava.DreamfireClassAPI;
import com.dreamfirestudios.dreamCore.DreamfireLocationLimiter.DreamfireLocationLimiter;
import com.dreamfirestudios.dreamCore.DreamfireLoop.IDreamfireLoop;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentBlockCallback;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentEntityCallback;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentItemStackCallback;
import com.dreamfirestudios.dreamCore.DreamfirePlaceholder.DreamfirePlaceholderManager;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfireRecipe.IDreamfireRecipe;
import com.dreamfirestudios.dreamCore.DreamfireScoreboard.DreamfireScoreboard;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.SmartInvsPlugin;
import com.dreamfirestudios.dreamCore.DreamfireStorage.DreamfireStorageManager;
import com.dreamfirestudios.dreamCore.DreamfireTeleport.DreamfireTeleport;
import com.dreamfirestudios.dreamCore.DreamfireTimer.DreamfireStopWatch;
import com.dreamfirestudios.dreamCore.DreamfireTimer.DreamfireTimer;
import com.dreamfirestudios.dreamCore.DreamfireVanish.DreamfireVanish;
import com.dreamfirestudios.dreamCore.DreamfireVariable.DreamfireVariableTest;
import com.dreamfirestudios.dreamCore.DreamfireWorld.DreamfireWorld;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.conversations.Conversation;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

//TODO get action bar by viewer (Player)
//TODO add support for custom blocks
public final class DreamCore extends JavaPlugin{
    private static DreamCore dreamfireCore;
    private LuckPerms luckPerms;
    private ProtocolManager protocolManager;
    private DreamfireStorageManager dreamfireStorageManager;
    private com.dreamfirestudios.dreamCore.DreamfireSmartInvs.SmartInvsPlugin smartInvsPlugin;
    private DreamfirePlaceholderManager dreamfirePlaceholderManager;


    public static DreamCore GetDreamfireCore(){return dreamfireCore;}
    public static LuckPerms GetLuckPerms(){return GetDreamfireCore().luckPerms;}
    public static ProtocolManager GetProtocolManager(){return GetDreamfireCore().protocolManager;}
    public static SmartInvsPlugin GetSmartInvsPlugin(){return GetDreamfireCore().smartInvsPlugin;}
    public static DreamfireStorageManager GetDreamfireStorageManager(){return GetDreamfireCore().dreamfireStorageManager;}
    public static DreamfirePlaceholderManager GetDreamfirePlaceholderManager(){return GetDreamfireCore().dreamfirePlaceholderManager;}


    private final LinkedHashMap<UUID, DreamfireActionBar> dreamfireActionBarLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireBlockMask> blockMaskLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireBook> dreamfireBookLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, IDreamfireLoop> dreamfireLoopLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireBossBar> dreamfireBossBarLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireCamPath> dreamfireCamPathLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireEntityMask> dreamfireEntityMaskLinkedHashMap = new LinkedHashMap<>();
    public final LinkedHashMap<UUID, List<UUID>> viewerHideMatrixLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireHologram> dreamfireHologramLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireLocationLimiter> dreamfireLocationLimiterLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, ArrayList<DreamfirePlayerAction>> dreamfirePlayerActionLinkedHashMap  = new LinkedHashMap<>();
    private final LinkedHashMap<String, DreamfireFakeBlock> dreamfireFakeBlockLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Conversation> conversationLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, DreamfireScoreboard> dreamfireScoreboardLinkedHashMap = new LinkedHashMap<>();
    public final LinkedHashMap<Class<?>, DreamfireVariableTest> dreamfireVariableTestLinkedHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, DreamfireWorld> dreamfireWorldLinkedHashMap = new LinkedHashMap<>();
    public final ArrayList<IDreamfirePersistentBlockCallback> iDreamfirePersistentBlockCallbacks = new ArrayList<>();
    public final ArrayList<IDreamfirePersistentEntityCallback> iDreamfirePersistentEntityCallbacks = new ArrayList<>();
    public final ArrayList<IDreamfirePersistentItemStackCallback> iDreamfirePersistentItemStackCallbacks = new ArrayList<>();
    public final ArrayList<DreamfireTeleport> dreamfireTeleportArrayList = new ArrayList<>();
    public final ArrayList<DreamfireStopWatch> dreamfireStopWatchArrayList = new ArrayList<>();
    public final ArrayList<DreamfireTimer> dreamfireTimerArrayList = new ArrayList<>();
    public final ArrayList<IDreamfireEvents> iDreamfireEventsArrayList = new ArrayList<>();
    public final ArrayList<IDreamfireEnchantment> iDreamfireEnchantments = new ArrayList<>();
    public final ArrayList<IDreamfireItemStack> iDreamfireItemStacks = new ArrayList<>();



    public DreamfireActionBar AddDreamfireActionBar(UUID id, DreamfireActionBar dreamfireActionBar){ return dreamfireActionBarLinkedHashMap.put(id, dreamfireActionBar); }
    public DreamfireActionBar GetDreamfireActionBar(UUID id){ return dreamfireActionBarLinkedHashMap.getOrDefault(id, null); }
    public DreamfireActionBar DeleteDreamfireActionBar(UUID id){return dreamfireActionBarLinkedHashMap.remove(id);}
    public Collection<DreamfireActionBar> GetAllDreamfireActionBar(){return dreamfireActionBarLinkedHashMap.values();}














    public DreamfireBlockMask AddBlockMask(UUID id, DreamfireBlockMask dreamfireBlockMask){return blockMaskLinkedHashMap.put(id, dreamfireBlockMask);}
    public DreamfireBook AddBookBuilder(UUID id, DreamfireBook bookBuilder){return dreamfireBookLinkedHashMap.put(id, bookBuilder);}
    public IDreamfireLoop AddDreamfireLoop(String id, IDreamfireLoop IDreamfireLoop){return dreamfireLoopLinkedHashMap.put(id, IDreamfireLoop);}
    public DreamfireBossBar AddDreamfireBossBar(UUID id, DreamfireBossBar dreamfireBossBar){return dreamfireBossBarLinkedHashMap.put(id, dreamfireBossBar);}
    public DreamfireCamPath AddDreamfireCamPath(UUID id, DreamfireCamPath dreamfireCamPath){return dreamfireCamPathLinkedHashMap.put(id, dreamfireCamPath);}
    public DreamfireEntityMask AddDreamfireEntityMask(UUID id, DreamfireEntityMask dreamfireEntityMask){return dreamfireEntityMaskLinkedHashMap.put(id, dreamfireEntityMask);}
    public DreamfireHologram AddDreamfireHologram(UUID id, DreamfireHologram dreamfireHologram){return dreamfireHologramLinkedHashMap.put(id,dreamfireHologram);}
    public DreamfireLocationLimiter AddDreamfireLocationLimiter(UUID id, DreamfireLocationLimiter dreamfireLocationLimiter){return dreamfireLocationLimiterLinkedHashMap.put(id, dreamfireLocationLimiter);}
    public DreamfireFakeBlock AddDreamfireFakeBlock(String id, DreamfireFakeBlock dreamfireFakeBlock){return dreamfireFakeBlockLinkedHashMap.put(id, dreamfireFakeBlock);}
    public Conversation AddConversation(UUID id, Conversation conversation){return conversationLinkedHashMap.put(id, conversation);}
    public ArrayList<DreamfirePlayerAction> AddDreamfirePlayerActions(UUID id, ArrayList<DreamfirePlayerAction> dreamfirePlayerActions){return dreamfirePlayerActionLinkedHashMap.put(id, dreamfirePlayerActions);}
    public DreamfireScoreboard AddDreamfireScoreboard(String id, DreamfireScoreboard dreamfireScoreboard){return dreamfireScoreboardLinkedHashMap.put(id, dreamfireScoreboard);}
    public DreamfireWorld AddDreamfireWorld(UUID id, DreamfireWorld dreamfireWorld){return dreamfireWorldLinkedHashMap.put(id, dreamfireWorld);}

    public DreamfireBlockMask GetBlockMask(UUID id){return blockMaskLinkedHashMap.getOrDefault(id, null);}
    public DreamfireBook GetBookBuilder(UUID id){return dreamfireBookLinkedHashMap.get(id);}
    public IDreamfireLoop GetDreamfireLoop(String id){return dreamfireLoopLinkedHashMap.getOrDefault(id, null);}
    public DreamfireBossBar GetDreamfireBossBar(UUID id){return dreamfireBossBarLinkedHashMap.getOrDefault(id, null);}
    public DreamfireCamPath GetDreamfireCamPath(UUID id){return dreamfireCamPathLinkedHashMap.getOrDefault(id, null);}
    public DreamfireEntityMask  GetDreamfireEntityMask(UUID id){return dreamfireEntityMaskLinkedHashMap.getOrDefault(id, null);}
    public DreamfireHologram GetDreamfireHologram(UUID id){return dreamfireHologramLinkedHashMap.getOrDefault(id, null);}
    public DreamfireLocationLimiter GetDreamfireLocationLimiter(UUID id){return dreamfireLocationLimiterLinkedHashMap.getOrDefault(id, null);}
    public DreamfireFakeBlock GetDreamfireFakeBlock(String id){return dreamfireFakeBlockLinkedHashMap.getOrDefault(id, null);}
    public Conversation GetConversation(UUID id){return conversationLinkedHashMap.getOrDefault(id, null);}
    public ArrayList<DreamfirePlayerAction> GetDreamfirePlayerActions(UUID id){return dreamfirePlayerActionLinkedHashMap.getOrDefault(id, new ArrayList<>());}
    public DreamfireScoreboard GetDreamfireScoreboard(String id){return dreamfireScoreboardLinkedHashMap.getOrDefault(id, null);}
    public DreamfireWorld GetDreamfireWorld(UUID id){return dreamfireWorldLinkedHashMap.getOrDefault(id, null);}
    public Collection<DreamfireFakeBlock> GetDreamfireFakeBlocks(){return dreamfireFakeBlockLinkedHashMap.values();}

    public DreamfireBlockMask DeleteBlockMask(UUID id){return blockMaskLinkedHashMap.remove(id);}
    public DreamfireBook DeleteBookBuilder(UUID id){return dreamfireBookLinkedHashMap.remove(id);}
    public IDreamfireLoop DeleteDreamfireLoop(String id){return dreamfireLoopLinkedHashMap.remove(id);}
    public DreamfireBossBar DeleteDreamfireBossBar(UUID id){return dreamfireBossBarLinkedHashMap.remove(id);}
    public DreamfireCamPath DeleteDreamfireCamPath(UUID id){return dreamfireCamPathLinkedHashMap.remove(id);}
    public DreamfireEntityMask DeleteDreamfireEntityMask(UUID id){return dreamfireEntityMaskLinkedHashMap.remove(id);}
    public DreamfireHologram DeleteDreamfireHologram(UUID id){return dreamfireHologramLinkedHashMap.remove(id);}
    public DreamfireLocationLimiter DeleteDreamfireLocationLimiter(UUID id){return dreamfireLocationLimiterLinkedHashMap.remove(id);}
    public DreamfireFakeBlock DeleteDreamfireFakeBlock(String id){return dreamfireFakeBlockLinkedHashMap.remove(id);}
    public Conversation DeleteConversation(UUID id){return conversationLinkedHashMap.remove(id);}
    public ArrayList<DreamfirePlayerAction> DeleteDreamfirePlayerActions(UUID id){return dreamfirePlayerActionLinkedHashMap.remove(id);}
    public DreamfireScoreboard DeleteDreamfireScoreboard(String id){return dreamfireScoreboardLinkedHashMap.remove(id);}
    public DreamfireWorld DeleteDreamfireWorld(UUID uuid){return dreamfireWorldLinkedHashMap.remove(uuid);}

    @Override
    public void onEnable() {
        dreamfireCore = this;
        luckPerms = LuckPermsProvider.get();
        protocolManager = ProtocolLibrary.getProtocolManager();
        dreamfireStorageManager = new DreamfireStorageManager();
        smartInvsPlugin = new SmartInvsPlugin(this);
        dreamfirePlaceholderManager = new DreamfirePlaceholderManager();
        DreamfireClassAPI.RegisterClasses(this);
    }

    @Override
    public void onDisable() {
        for(var blockMask : blockMaskLinkedHashMap.values().stream().toList()) blockMask.stop();
        for(var dreamfireLoop : dreamfireLoopLinkedHashMap.values().stream().toList()) dreamfireLoop.CancelLoop();
        for(var dreamfireCamPath : dreamfireCamPathLinkedHashMap.values().stream().toList()) dreamfireCamPath.OnDisable();
        for(var dreamfireEntityMask : dreamfireEntityMaskLinkedHashMap.values().stream().toList()) dreamfireEntityMask.stop();
        for(var dreamfireHologram : dreamfireHologramLinkedHashMap.values().stream().toList()) dreamfireHologram.deleteHologram();
    }

    public void OneTickClasses(){
        dreamfireActionBarLinkedHashMap.values().removeIf(DreamfireActionBar::displayNextFrame);
        for(var dreamfireBossBar : dreamfireBossBarLinkedHashMap.values()) dreamfireBossBar.displayNextFrame();
        for(var dreamfireLocationLimiter : dreamfireLocationLimiterLinkedHashMap.values()) dreamfireLocationLimiter.tickLocationLimiter();
        for(var dreamfireFakeBlock : dreamfireFakeBlockLinkedHashMap.values()) dreamfireFakeBlock.DisplayNextFrame();
        for(var dreamfireScoreBoard : dreamfireScoreboardLinkedHashMap.values()) dreamfireScoreBoard.DisplayNextFrame();
        for(var dreamfireWorld : dreamfireWorldLinkedHashMap.values()) dreamfireWorld.TickWorld();
        dreamfireTeleportArrayList.removeIf(DreamfireTeleport::HandleOnLoop);
        dreamfireStopWatchArrayList.removeIf(DreamfireStopWatch::HandleOnLoop);
        dreamfireTimerArrayList.removeIf(DreamfireTimer::HandleOnLoop);
    }

    public void TwentyTickClasses(){
        DreamfireVanish.updateVanishOnAllPlayers();
        for(var dreamfireBook : dreamfireBookLinkedHashMap.values()) dreamfireBook.displayNextFrame();
        for(var blockMask : blockMaskLinkedHashMap.values()) blockMask.displayNextFrame();
        for(var dreamfireEntityMask : dreamfireEntityMaskLinkedHashMap.values()) dreamfireEntityMask.displayNextFrame();
        for(var dreamfireHologram : dreamfireHologramLinkedHashMap.values()) dreamfireHologram.displayNextFrame();
    }

    public void PlayerQuit(PlayerQuitEvent playerQuitEvent){
        var player = playerQuitEvent.getPlayer();
        for(var dreamfireBook : dreamfireBookLinkedHashMap.values()) dreamfireBook.playerQuit(player);
        for(var dreamfireCamPath : dreamfireCamPathLinkedHashMap.values()) dreamfireCamPath.OnPlayerLeave(player);
    }
}