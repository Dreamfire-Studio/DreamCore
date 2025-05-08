package com.dreamfirestudios.dreamCore.DreamfireJava;

import com.comphenix.protocol.events.PacketAdapter;
import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireChat;
import com.dreamfirestudios.dreamCore.DreamfireEnchantment.IDreamfireEnchantment;
import com.dreamfirestudios.dreamCore.DreamfireEvents.IDreamfireEvents;
import com.dreamfirestudios.dreamCore.DreamfireHologram.DreamfireHologram;
import com.dreamfirestudios.dreamCore.DreamfireHologram.IDreamfireHologram;
import com.dreamfirestudios.dreamCore.DreamfireItems.IDreamfireItemStack;
import com.dreamfirestudios.dreamCore.DreamfireLoop.IDreamfireLoop;
import com.dreamfirestudios.dreamCore.DreamfireMessagingChannel.PluginMessageLibrary;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentBlockCallback;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentEntityCallback;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentItemStackCallback;
import com.dreamfirestudios.dreamCore.DreamfirePlaceholder.IDreamfirePlaceholder;
import com.dreamfirestudios.dreamCore.DreamfireRecipe.IDreamfireRecipe;
import com.dreamfirestudios.dreamCore.DreamfireVariable.DreamfireVariableTest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DreamfireClassAPI {
    private static final Map<Class<?>, BiConsumer<JavaPlugin, Object>> registrationActions = new HashMap<>();

    static {
        registrationActions.put(IDreamfireLoop.class, (javaPlugin, instance) -> RegisterPulseLoop(javaPlugin, (IDreamfireLoop) instance));
        registrationActions.put(PacketAdapter.class, (javaPlugin, instance) -> RegisterPacketAdapter(javaPlugin, (PacketAdapter) instance));
        registrationActions.put(IDreamfireEnchantment.class, (javaPlugin, instance) -> RegisterDreamfireEnchantment(javaPlugin, (IDreamfireEnchantment) instance));
        registrationActions.put(Listener.class, (javaPlugin, instance) -> RegisterListener(javaPlugin, (Listener) instance));
        registrationActions.put(IDreamfireHologram.class, (javaPlugin, instance) -> RegisterIDreamfireHologram(javaPlugin, (IDreamfireHologram) instance));
        registrationActions.put(IDreamfirePlaceholder.class, (javaPlugin, instance) -> RegisterPulsePlaceholder(javaPlugin, (IDreamfirePlaceholder) instance));
        registrationActions.put(IDreamfireEvents.class, (javaPlugin, instance) -> RegisterIDreamfireEvents(javaPlugin, (IDreamfireEvents) instance));
        registrationActions.put(IDreamfireItemStack.class, (javaPlugin, instance) -> RegisterIDreamfireItemStack(javaPlugin, (IDreamfireItemStack) instance));
        registrationActions.put(IDreamfireRecipe.class, (javaPlugin, instance) -> RegisterPulseRecipe(javaPlugin, (IDreamfireRecipe) instance));
        registrationActions.put(DreamfireVariableTest.class, (javaPlugin, instance) -> RegisterPulseVariableTest(javaPlugin, (DreamfireVariableTest) instance));
        registrationActions.put(PluginMessageLibrary.class, (javaPlugin, instance) -> RegisterPluginMessageListener(javaPlugin, (PluginMessageLibrary) instance));
        registrationActions.put(IDreamfirePersistentBlockCallback.class, (javaPlugin, instance) -> RegisterDreamfirePersistentBlockCallback(javaPlugin, (IDreamfirePersistentBlockCallback) instance));
        registrationActions.put(IDreamfirePersistentItemStackCallback.class, (javaPlugin, instance) -> RegisterDreamfirePersistentItemStackCallback(javaPlugin, (IDreamfirePersistentItemStackCallback) instance));
        registrationActions.put(IDreamfirePersistentEntityCallback.class, (javaPlugin, instance) -> RegisterDreamfirePersistentEntityCallback(javaPlugin, (IDreamfirePersistentEntityCallback) instance));
    }

    public static void RegisterClasses(JavaPlugin javaPlugin){
        try { RegisterClassesRaw(javaPlugin); }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) { throw new RuntimeException(e);}
    }

    public static void RegisterClassesRaw(JavaPlugin javaPlugin) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            for (var autoRegisterClass : DreamfireJavaAPI.getAutoRegisterClasses(javaPlugin)) {
                for (var entry : registrationActions.entrySet()) {
                    if (entry.getKey().isAssignableFrom(autoRegisterClass)) {
                        Object instance = autoRegisterClass.getConstructor().newInstance();
                        entry.getValue().accept(javaPlugin, instance);
                        break; // Stop checking after the first successful match
                    }
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            javaPlugin.getLogger().severe("Failed to register classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void RegisterPulseLoop(JavaPlugin javaPlugin, IDreamfireLoop iDreamfireLoop){
        iDreamfireLoop.Start();
        int finalId = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
            @Override
            public void run() {
                iDreamfireLoop.Loop();
            }
        }, iDreamfireLoop.StartDelay(), iDreamfireLoop.LoopInterval());
        iDreamfireLoop.PassID(finalId);
        DreamCore.GetDreamfireCore().AddDreamfireLoop(iDreamfireLoop.ReturnID(), iDreamfireLoop);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered Loop: %s", iDreamfireLoop.ReturnID()));
    }

    public static void RegisterPacketAdapter(JavaPlugin javaPlugin, PacketAdapter packetAdapter) {
        DreamCore.GetProtocolManager().addPacketListener(packetAdapter);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered PacketAdapter: %s", packetAdapter.getClass().getSimpleName()));
    }

    public static void RegisterListener(JavaPlugin javaPlugin, Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, javaPlugin);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered Listener: %s", listener.getClass().getSimpleName()));
    }

    public static void RegisterDreamfireEnchantment(JavaPlugin javaPlugin, IDreamfireEnchantment pulseEnchantment){
        DreamCore.GetDreamfireCore().iDreamfireEnchantments.add(pulseEnchantment);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered DreamfireEnchantment: %s", pulseEnchantment.getName()));
    }

    public static void RegisterDreamfirePersistentBlockCallback(JavaPlugin javaPlugin, IDreamfirePersistentBlockCallback iDreamfirePersistentBlockCallback){
        DreamCore.GetDreamfireCore().iDreamfirePersistentBlockCallbacks.add(iDreamfirePersistentBlockCallback);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered DreamfirePersistentBlockCallback: %s", iDreamfirePersistentBlockCallback.getClass().getSimpleName()));
    }

    public static void RegisterDreamfirePersistentItemStackCallback(JavaPlugin javaPlugin, IDreamfirePersistentItemStackCallback iDreamfirePersistentItemStackCallback){
        DreamCore.GetDreamfireCore().iDreamfirePersistentItemStackCallbacks.add(iDreamfirePersistentItemStackCallback);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered DreamfirePersistentBlockCallback: %s", iDreamfirePersistentItemStackCallback.getClass().getSimpleName()));
    }

    public static void RegisterDreamfirePersistentEntityCallback(JavaPlugin javaPlugin, IDreamfirePersistentEntityCallback iDreamfirePersistentEntityCallback){
        DreamCore.GetDreamfireCore().iDreamfirePersistentEntityCallbacks.add(iDreamfirePersistentEntityCallback);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered DreamfirePersistentBlockCallback: %s", iDreamfirePersistentEntityCallback.getClass().getSimpleName()));
    }

    public static void RegisterIDreamfireHologram(JavaPlugin javaPlugin, IDreamfireHologram iDreamfireHologram){
        new DreamfireHologram.HologramBuilder().create(iDreamfireHologram);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered IDreamfireHologram: %s", iDreamfireHologram.getClass().getSimpleName()));
    }

    public static void RegisterPulsePlaceholder(JavaPlugin javaPlugin, IDreamfirePlaceholder iDreamfirePlaceholder){
        DreamCore.GetDreamfirePlaceholderManager().RegisterInterface(iDreamfirePlaceholder);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered IDreamfirePlaceholder: %s", iDreamfirePlaceholder.getClass().getSimpleName()));
    }

    public static void RegisterPulseRecipe(JavaPlugin javaPlugin, IDreamfireRecipe iDreamfireRecipe){
        Bukkit.addRecipe(iDreamfireRecipe.ReturnRecipe(javaPlugin));
        DreamfireChat.SendMessageToConsole(String.format("&8Registered IDreamfireRecipe: %s", iDreamfireRecipe.getClass().getSimpleName()));
    }

    public static void RegisterIDreamfireEvents(JavaPlugin javaPlugin, IDreamfireEvents iDreamfireEvents){
        DreamCore.GetDreamfireCore().iDreamfireEventsArrayList.add(iDreamfireEvents);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered IDreamfireEvents: %s", iDreamfireEvents.getClass().getSimpleName()));
    }

    public static void RegisterIDreamfireItemStack(JavaPlugin javaPlugin, IDreamfireItemStack iDreamfireItemStack){
        DreamCore.GetDreamfireCore().iDreamfireItemStacks.add(iDreamfireItemStack);
        DreamfireChat.SendMessageToConsole(String.format("&8Registered IDreamfireItemStack: %s", iDreamfireItemStack.getClass().getSimpleName()));
    }

    public static void RegisterPulseVariableTest(JavaPlugin javaPlugin, DreamfireVariableTest dreamfireVariableTest){
        DreamfireChat.SendMessageToConsole(String.format("&8Registered DreamfireVariableTest: %s", dreamfireVariableTest.getClass().getSimpleName()));
        for(var classType : dreamfireVariableTest.ClassTypes()){
            DreamCore.GetDreamfireCore().dreamfireVariableTestLinkedHashMap.put(classType, dreamfireVariableTest);
        }
    }

    public static void RegisterPluginMessageListener(JavaPlugin javaPlugin, PluginMessageLibrary pluginMessageListener) {
        var channelName = pluginMessageListener.getChannelName(javaPlugin);
        Bukkit.getMessenger().registerOutgoingPluginChannel(javaPlugin, channelName);
        Bukkit.getMessenger().registerIncomingPluginChannel(javaPlugin, channelName, pluginMessageListener);
        if (Bukkit.getMessenger().isOutgoingChannelRegistered(javaPlugin, channelName)) {
            DreamfireChat.SendMessageToConsole(String.format("&8Registered PluginMessageListener: %s on channel %s", pluginMessageListener.getClass().getSimpleName(), channelName));
        } else {
            Bukkit.getConsoleSender().sendMessage("Failed to register outgoing channel: " + channelName);
        }
    }
}
