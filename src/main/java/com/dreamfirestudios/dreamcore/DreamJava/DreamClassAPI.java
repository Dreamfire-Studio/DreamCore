package com.dreamfirestudios.dreamcore.DreamJava;

import com.comphenix.protocol.events.PacketAdapter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamChat;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamEnchantment.IDreamEnchantment;
import com.dreamfirestudios.dreamcore.DreamHologram.DreamHologram; // (import retained even if unused by compiler settings)
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

/// <summary>
/// Central auto‑registration hub for Dreamfire “components” discovered via <see cref="PulseAutoRegister"/>.
/// </summary>
/// <remarks>
/// This class scans your plugin JAR for annotated classes and registers them
/// according to their implemented interfaces (e.g., <see cref="IDreamLoop"/>, <see cref="Listener"/>, <see cref="PacketAdapter"/>).
/// <para>All registrations log to console via <see cref="DreamChat"/>.</para>
/// </remarks>
/// <example>
/// <code>
/// // In your JavaPlugin#onEnable():
/// DreamClassAPI.RegisterClasses(this);
/// </code>
/// </example>
public class DreamClassAPI {

    /// <summary>
    /// Mapping of “marker” interface → registration action.
    /// </summary>
    /// <remarks>
    /// Each action receives the plugin instance and a newly constructed object instance.
    /// The first matching entry (via <c>isAssignableFrom</c>) is used.
    /// </remarks>
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

    /// <summary>
    /// Discovers and registers all annotated classes. Exceptions are wrapped as <see cref="RuntimeException"/>.
    /// </summary>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <remarks>Calls <see cref="RegisterClassesRaw(JavaPlugin)"/> internally and wraps reflection exceptions.</remarks>
    /// <example>
    /// <code>DreamClassAPI.RegisterClasses(this);</code>
    /// </example>
    public static void RegisterClasses(JavaPlugin javaPlugin){
        try { RegisterClassesRaw(javaPlugin); }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /// <summary>
    /// Discovers and registers annotated classes, propagating reflection errors.
    /// </summary>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <exception cref="NoSuchMethodException">No default constructor found.</exception>
    /// <exception cref="InvocationTargetException">Constructor threw an exception.</exception>
    /// <exception cref="InstantiationException">Class cannot be instantiated.</exception>
    /// <exception cref="IllegalAccessException">Constructor not accessible.</exception>
    /// <remarks>
    /// For each discovered class, the first matching entry in <see cref="#registrationActions"/> is applied.
    /// </remarks>
    /// <example>
    /// <code>DreamClassAPI.RegisterClassesRaw(plugin);</code>
    /// </example>
    public static void RegisterClassesRaw(JavaPlugin javaPlugin)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

    /// <summary>
    /// Registers an <see cref="IDreamLoop"/>: starts it, schedules repeating task, records ID.
    /// </summary>
    /// <param name="javaPlugin">Plugin for scheduler.</param>
    /// <param name="iDreamLoop">Loop implementation.</param>
    /// <remarks>
    /// Schedules using <c>scheduleSyncRepeatingTask</c> with the loop’s start delay and interval.
    /// </remarks>
    /// <example>
    /// <code>DreamClassAPI.RegisterPulseLoop(plugin, new MyLoop());</code>
    /// </example>
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

    /// <summary>
    /// Registers a ProtocolLib <see cref="PacketAdapter"/>.
    /// </summary>
    /// <param name="javaPlugin">Plugin instance (unused but consistent signature).</param>
    /// <param name="packetAdapter">Adapter to add to ProtocolManager.</param>
    /// <example>
    /// <code>DreamClassAPI.RegisterPacketAdapter(plugin, new MyAdapter(plugin));</code>
    /// </example>
    public static void RegisterPacketAdapter(JavaPlugin javaPlugin, PacketAdapter packetAdapter) {
        DreamCore.ProtocolManager.addPacketListener(packetAdapter);
        DreamChat.SendMessageToConsole(String.format("&8Registered PacketAdapter: %s", packetAdapter.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers a Bukkit <see cref="Listener"/>.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="listener">Listener instance.</param>
    /// <example>
    /// <code>DreamClassAPI.RegisterListener(plugin, new MyListener());</code>
    /// </example>
    public static void RegisterListener(JavaPlugin javaPlugin, Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, javaPlugin);
        DreamChat.SendMessageToConsole(String.format("&8Registered Listener: %s", listener.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers a custom enchantment with DreamCore’s registry.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="pulseEnchantment">Enchantment implementation.</param>
    /// <example>
    /// <code>DreamClassAPI.RegisterDreamEnchantment(plugin, new FrostbiteEnchant());</code>
    /// </example>
    public static void RegisterDreamEnchantment(JavaPlugin javaPlugin, IDreamEnchantment pulseEnchantment){
        DreamCore.IDreamEnchantments.add(pulseEnchantment);
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamEnchantment: %s", pulseEnchantment.getName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers an <see cref="IDreamPlaceholder"/> with the placeholder manager.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="iDreamPlaceholder">Placeholder provider.</param>
    /// <example>
    /// <code>DreamClassAPI.RegisterPulsePlaceholder(plugin, new ServerTPSPlaceholder());</code>
    /// </example>
    public static void RegisterPulsePlaceholder(JavaPlugin javaPlugin, IDreamPlaceholder iDreamPlaceholder){
        DreamCore.DreamPlaceholderManager.register(iDreamPlaceholder);
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamPlaceholder: %s", iDreamPlaceholder.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers a recipe via Bukkit.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="iDreamRecipe">Recipe definition.</param>
    /// <example>
    /// <code>DreamClassAPI.RegisterPulseRecipe(plugin, new SuperPickaxeRecipe());</code>
    /// </example>
    public static void RegisterPulseRecipe(JavaPlugin javaPlugin, IDreamRecipe iDreamRecipe){
        Bukkit.addRecipe(iDreamRecipe.ReturnRecipe(javaPlugin));
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamRecipe: %s", iDreamRecipe.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers a custom item definition for global access.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="iDreamItemStack">Item definition.</param>
    /// <example>
    /// <code>DreamClassAPI.RegisterIDreamItemStack(plugin, new TeleportCharm());</code>
    /// </example>
    public static void RegisterIDreamItemStack(JavaPlugin javaPlugin, IDreamItemStack iDreamItemStack){
        DreamCore.IDreamItemStacks.add(iDreamItemStack);
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamItemStack: %s", iDreamItemStack.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers a <see cref="DreamVariableTest"/> against its supported class types.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="dreamfireVariableTest">Variable test implementation.</param>
    /// <remarks>Each returned class type is mapped to the same tester instance in <see cref="DreamCore#DreamVariableTests"/>.</remarks>
    /// <example>
    /// <code>DreamClassAPI.RegisterPulseVariableTest(plugin, new BoolTest());</code>
    /// </example>
    public static void RegisterPulseVariableTest(JavaPlugin javaPlugin, DreamVariableTest dreamfireVariableTest){
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamVariableTest: %s", dreamfireVariableTest.getClass().getSimpleName()), DreamMessageSettings.all());
        for(var classType : dreamfireVariableTest.ClassTypes()){
            DreamCore.DreamVariableTests.put(classType, dreamfireVariableTest);
        }
    }

    /// <summary>
    /// Registers a plugin messaging listener on a named channel.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="pluginMessageListener">Listener providing channel name and handler.</param>
    /// <remarks>
    /// Registers both outgoing and incoming plugin channels. Logs success/failure to console.
    /// </remarks>
    /// <example>
    /// <code>DreamClassAPI.RegisterPluginMessageListener(plugin, new BungeeBridge());</code>
    /// </example>
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