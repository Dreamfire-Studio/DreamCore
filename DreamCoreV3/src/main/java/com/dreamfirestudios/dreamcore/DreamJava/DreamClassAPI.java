package com.dreamfirestudios.dreamcore.DreamJava;

import com.comphenix.protocol.events.PacketAdapter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamChat;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamEnchantment.IDreamEnchantment;
import com.dreamfirestudios.dreamcore.DreamHologram.DreamHologram;
import com.dreamfirestudios.dreamcore.DreamItems.IDreamItemStack;
import com.dreamfirestudios.dreamcore.DreamLoop.IDreamLoop;
import com.dreamfirestudios.dreamcore.DreamMessagingChannel.PluginMessageLibrary;
import com.dreamfirestudios.dreamcore.DreamPlaceholder.IDreamPlaceholder;
import com.dreamfirestudios.dreamcore.DreamRecipe.IDreamRecipe;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamVariableTest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DreamClassAPI {
    private static final Map<Class<?>, BiConsumer<JavaPlugin, Object>> registrationActions = new HashMap<>();

    static {
        registrationActions.put(IDreamLoop.class, (javaPlugin, instance) -> RegisterPulseLoop(javaPlugin, (IDreamLoop) instance));
        registrationActions.put(PacketAdapter.class, (javaPlugin, instance) -> RegisterPacketAdapter(javaPlugin, (PacketAdapter) instance));
        registrationActions.put(IDreamEnchantment.class, (javaPlugin, instance) -> RegisterDreamEnchantment(javaPlugin, (IDreamEnchantment) instance));
        registrationActions.put(Listener.class, (javaPlugin, instance) -> RegisterListener(javaPlugin, (Listener) instance));
        registrationActions.put(IDreamPlaceholder.class, (javaPlugin, instance) -> RegisterPulsePlaceholder(javaPlugin, (IDreamPlaceholder) instance));
        registrationActions.put(IDreamItemStack.class, (javaPlugin, instance) -> RegisterIDreamItemStack(javaPlugin, (IDreamItemStack) instance));
        registrationActions.put(IDreamRecipe.class, (javaPlugin, instance) -> RegisterPulseRecipe(javaPlugin, (IDreamRecipe) instance));
        registrationActions.put(DreamVariableTest.class, (javaPlugin, instance) -> RegisterPulseVariableTest(javaPlugin, (DreamVariableTest) instance));
        registrationActions.put(PluginMessageLibrary.class, (javaPlugin, instance) -> RegisterPluginMessageListener(javaPlugin, (PluginMessageLibrary) instance));
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

    public static void RegisterPulseLoop(JavaPlugin javaPlugin, IDreamLoop iDreamLoop){
        iDreamLoop.Start();
        int finalId = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
            @Override
            public void run() {
                iDreamLoop.Loop();
            }
        }, iDreamLoop.StartDelay(), iDreamLoop.LoopInterval());
        iDreamLoop.PassID(finalId);
        DreamCore.IDreamLoops.put(iDreamLoop.ReturnID(), iDreamLoop);
        DreamChat.SendMessageToConsole(String.format("&8Registered Loop: %s", iDreamLoop.ReturnID()), DreamMessageSettings.all());
    }

    public static void RegisterPacketAdapter(JavaPlugin javaPlugin, PacketAdapter packetAdapter) {
        DreamCore.ProtocolManager.addPacketListener(packetAdapter);
        DreamChat.SendMessageToConsole(String.format("&8Registered PacketAdapter: %s", packetAdapter.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterListener(JavaPlugin javaPlugin, Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, javaPlugin);
        DreamChat.SendMessageToConsole(String.format("&8Registered Listener: %s", listener.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterDreamEnchantment(JavaPlugin javaPlugin, IDreamEnchantment pulseEnchantment){
        DreamCore.IDreamEnchantments.add(pulseEnchantment);
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamEnchantment: %s", pulseEnchantment.getName()), DreamMessageSettings.all());
    }

    public static void RegisterPulsePlaceholder(JavaPlugin javaPlugin, IDreamPlaceholder iDreamPlaceholder){
        DreamCore.DreamPlaceholderManager.register(iDreamPlaceholder);
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamPlaceholder: %s", iDreamPlaceholder.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterPulseRecipe(JavaPlugin javaPlugin, IDreamRecipe iDreamRecipe){
        Bukkit.addRecipe(iDreamRecipe.ReturnRecipe(javaPlugin));
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamRecipe: %s", iDreamRecipe.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterIDreamItemStack(JavaPlugin javaPlugin, IDreamItemStack iDreamItemStack){
        DreamCore.IDreamItemStacks.add(iDreamItemStack);
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamItemStack: %s", iDreamItemStack.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterPulseVariableTest(JavaPlugin javaPlugin, DreamVariableTest dreamfireVariableTest){
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamVariableTest: %s", dreamfireVariableTest.getClass().getSimpleName()), DreamMessageSettings.all());
        for(var classType : dreamfireVariableTest.ClassTypes()){
            DreamCore.DreamVariableTests.put(classType, dreamfireVariableTest);
        }
    }

    public static void RegisterPluginMessageListener(JavaPlugin javaPlugin, PluginMessageLibrary pluginMessageListener) {
        var channelName = pluginMessageListener.getChannelName(javaPlugin);
        Bukkit.getMessenger().registerOutgoingPluginChannel(javaPlugin, channelName);
        Bukkit.getMessenger().registerIncomingPluginChannel(javaPlugin, channelName, pluginMessageListener);
        if (Bukkit.getMessenger().isOutgoingChannelRegistered(javaPlugin, channelName)) {
            DreamChat.SendMessageToConsole(String.format("&8Registered PluginMessageListener: %s on channel %s", pluginMessageListener.getClass().getSimpleName(), channelName), DreamMessageSettings.all());
        } else {
            Bukkit.getConsoleSender().sendMessage("Failed to register outgoing channel: " + channelName);
        }
    }
}