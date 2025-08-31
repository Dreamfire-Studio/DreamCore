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
package com.dreamfirestudios.dreamcore.DreamJava;

import com.comphenix.protocol.events.PacketAdapter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamChat;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamEnchantment.IDreamEnchantment;
import com.dreamfirestudios.dreamcore.DreamHologram.DreamHologram; // retained even if unused by compiler settings
import com.dreamfirestudios.dreamcore.DreamItems.IDreamItemStack;
import com.dreamfirestudios.dreamcore.DreamKeyPressed.IDreamKeyPatternSpec;
import com.dreamfirestudios.dreamcore.DreamKeyPressed.IDreamKeyPressed;
import com.dreamfirestudios.dreamcore.DreamLoop.IDreamLoop;
import com.dreamfirestudios.dreamcore.DreamMessagingChannel.PluginMessageLibrary;
import com.dreamfirestudios.dreamcore.DreamPlaceholder.IDreamPlaceholder;
import com.dreamfirestudios.dreamcore.DreamRecipe.IDreamRecipe;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamVariableTest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/// <summary>
/// Central auto-registration hub for Dreamfire components discovered via <see cref="PulseAutoRegister"/>.
/// </summary>
/// <remarks>
/// Scans plugin classes and, for the first matching marker interface, performs the mapped registration action.
/// Logs each registration via <see cref="DreamChat"/>.
/// </remarks>
/// <example>
/// <code>
/// // In your JavaPlugin#onEnable():
/// DreamClassAPI.RegisterClasses(this);
/// </code>
/// </example>
public final class DreamClassAPI {

    private DreamClassAPI() { }

    /// <summary>
    /// Mapping of marker interface → registration action.
    /// </summary>
    private static final Map<Class<?>, BiConsumer<JavaPlugin, Object>> registrationActions = new HashMap<>();

    static {
        registrationActions.put(IDreamLoop.class, (plugin, instance) -> RegisterPulseLoop(plugin, (IDreamLoop) instance));
        registrationActions.put(PacketAdapter.class, (plugin, instance) -> RegisterPacketAdapter(plugin, (PacketAdapter) instance));
        registrationActions.put(IDreamEnchantment.class, (plugin, instance) -> RegisterDreamEnchantment(plugin, (IDreamEnchantment) instance));
        registrationActions.put(Listener.class, (plugin, instance) -> RegisterListener(plugin, (Listener) instance));
        registrationActions.put(IDreamPlaceholder.class, (plugin, instance) -> RegisterPulsePlaceholder(plugin, (IDreamPlaceholder) instance));
        registrationActions.put(IDreamItemStack.class, (plugin, instance) -> RegisterIDreamItemStack(plugin, (IDreamItemStack) instance));
        registrationActions.put(IDreamRecipe.class, (plugin, instance) -> RegisterPulseRecipe(plugin, (IDreamRecipe) instance));
        registrationActions.put(DreamVariableTest.class, (plugin, instance) -> RegisterPulseVariableTest(plugin, (DreamVariableTest) instance));
        registrationActions.put(DreamAbstractVariableTest.class, (plugin, instance) -> RegisterPulseVariableTest(plugin, (DreamAbstractVariableTest<?>) instance));
        registrationActions.put(PluginMessageLibrary.class, (plugin, instance) -> RegisterPluginMessageListener(plugin, (PluginMessageLibrary) instance));
        registrationActions.put(IDreamKeyPressed.class, (plugin, instance) -> RegisterDreamKeyPressed(plugin, (IDreamKeyPressed) instance));
        registrationActions.put(IDreamKeyPatternSpec.class, (plugin, instance) -> RegisterDreamKeyPattern(plugin, (IDreamKeyPatternSpec) instance));
    }

    /// <summary>Registers all discovered classes (wrapped exceptions).</summary>
    public static void RegisterClasses(JavaPlugin javaPlugin){
        try { RegisterClassesRaw(javaPlugin); }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /// <summary>Registers all discovered classes (propagates reflection errors).</summary>
    public static void RegisterClassesRaw(JavaPlugin javaPlugin) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            for (var autoRegisterClass : DreamfireJavaAPI.getAutoRegisterClasses(javaPlugin)) {
                for (var entry : registrationActions.entrySet()) {
                    if (entry.getKey().isAssignableFrom(autoRegisterClass)) {
                        Object instance = autoRegisterClass.getConstructor().newInstance();
                        entry.getValue().accept(javaPlugin, instance);
                    }
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            javaPlugin.getLogger().severe("Failed to register classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================ REGISTRATIONS ============================

    // inside DreamClassAPI.java — ADD these two methods
    /**
     * <summary>Registers a key-pattern listener (lifecycle callbacks only).</summary>
     * <remarks>
     * Stored for later consumption by the key-input manager (not implemented yet).
     * </remarks>
     */
    public static void RegisterDreamKeyPressed(JavaPlugin javaPlugin, IDreamKeyPressed listener) {
        var list = com.dreamfirestudios.dreamcore.DreamCore.DreamKeyPressedListeners;
        if (!list.contains(listener)) {
            list.add(listener);
            DreamChat.SendMessageToConsole(String.format("&8Registered DreamKeyPressed listener: %s", listener.getClass().getSimpleName()), DreamMessageSettings.all());
        }
    }

    /**
     * <summary>Registers a key-pattern specification (definition only).</summary>
     * <remarks>
     * Manager can pair these with listeners or allow implementors to implement both interfaces in one class.
     * </remarks>
     */
    public static void RegisterDreamKeyPattern(JavaPlugin javaPlugin, IDreamKeyPatternSpec spec) {
        var list = com.dreamfirestudios.dreamcore.DreamCore.DreamKeyPatternSpecs;
        if (!list.contains(spec)) {
            list.add(spec);
            DreamChat.SendMessageToConsole(String.format("&8Registered DreamKeyPattern spec: %s", spec.getClass().getSimpleName()), DreamMessageSettings.all());
        }
    }

    public static void RegisterPulseLoop(JavaPlugin javaPlugin, IDreamLoop iDreamLoop){
        iDreamLoop.Start();
        int finalId = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, iDreamLoop::Loop, iDreamLoop.StartDelay(), iDreamLoop.LoopInterval());
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

    public static void RegisterDreamEnchantment(JavaPlugin javaPlugin, IDreamEnchantment enchant){
        DreamCore.IDreamEnchantments.add(enchant);
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamEnchantment: %s", enchant.getName()), DreamMessageSettings.all());
    }

    public static void RegisterPulsePlaceholder(JavaPlugin javaPlugin, IDreamPlaceholder placeholder){
        DreamCore.DreamPlaceholderManager.register(placeholder);
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamPlaceholder: %s", placeholder.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterPulseRecipe(JavaPlugin javaPlugin, IDreamRecipe recipe){
        Bukkit.addRecipe(recipe.ReturnRecipe(javaPlugin));
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamRecipe: %s", recipe.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    public static void RegisterIDreamItemStack(JavaPlugin javaPlugin, IDreamItemStack def){
        DreamCore.IDreamItemStacks.put(def.id(), def);
        DreamChat.SendMessageToConsole(String.format("&8Registered IDreamItemStack: %s", def.getClass().getSimpleName()), DreamMessageSettings.all());
    }

    /// <summary>
    /// Registers a legacy <see cref="DreamVariableTest"/> against all its declared supported types.
    /// </summary>
    public static void RegisterPulseVariableTest(JavaPlugin javaPlugin, DreamVariableTest legacyTest){
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamVariableTest (legacy): %s", legacyTest.getClass().getSimpleName()), DreamMessageSettings.all());
        for (var classType : legacyTest.ClassTypes()){
            DreamCore.DreamVariableTests.put(classType, legacyTest);
        }
    }

    /// <summary>
    /// Registers a new-style <see cref="DreamAbstractVariableTest"/> by adapting it to the legacy registry.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin.</param>
    /// <param name="test">New-style variable test.</param>
    /// <remarks>
    /// To avoid core changes, we wrap <paramref name="test"/> with a lightweight adapter that implements
    /// <see cref="DreamVariableTest"/> and delegates to the generic implementation. This keeps all existing
    /// consumers functional while you migrate code incrementally.
    /// </remarks>
    public static void RegisterPulseVariableTest(JavaPlugin javaPlugin, DreamAbstractVariableTest<?> test){
        DreamVariableTest adapter = new VariableTestAdapter(test);
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamAbstractVariableTest: %s &8→ adapter bound (%s)",
                test.getClass().getSimpleName(), test.persistentType()), DreamMessageSettings.all());
        for (Class<?> type : test.supportedTypes()){
            DreamCore.DreamVariableTests.put(type, adapter);
        }
    }

    public static void RegisterPluginMessageListener(JavaPlugin javaPlugin, PluginMessageLibrary pluginMessageListener) {
        var channelName = pluginMessageListener.getChannelName(javaPlugin);
        Bukkit.getMessenger().registerOutgoingPluginChannel(javaPlugin, channelName);
        Bukkit.getMessenger().registerIncomingPluginChannel(javaPlugin, channelName, pluginMessageListener);
        if (Bukkit.getMessenger().isOutgoingChannelRegistered(javaPlugin, channelName)) {
            DreamChat.SendMessageToConsole(String.format("&8Registered PluginMessageListener: %s on channel %s",
                    pluginMessageListener.getClass().getSimpleName(), channelName), DreamMessageSettings.all());
        } else {
            Bukkit.getConsoleSender().sendMessage("Failed to register outgoing channel: " + channelName);
        }
    }

    // ============================ ADAPTER ============================

    /// <summary>
    /// Bridges a <see cref="DreamAbstractVariableTest{T}"/> to the legacy <see cref="DreamVariableTest"/> interface.
    /// </summary>
    private static final class VariableTestAdapter implements DreamVariableTest {
        private final DreamAbstractVariableTest<?> impl;

        private VariableTestAdapter(DreamAbstractVariableTest<?> impl) {
            this.impl = impl;
        }

        @Override
        public com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes PersistentDataType() {
            return impl.persistentType();
        }

        @Override
        public boolean IsType(Object variable) {
            return impl.isType(variable);
        }

        @Override
        public List<Class<?>> ClassTypes() {
            return impl.supportedTypes();
        }

        @Override
        public Object SerializeData(Object serializedData) {
            return impl.serialize(serializedData);
        }

        @Override
        public Object DeSerializeData(Object serializedData) {
            return impl.deserialize(serializedData);
        }

        @Override
        public Object ReturnDefaultValue() {
            return impl.defaultValue();
        }
    }
}