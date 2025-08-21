package com.dreamfirestudios.dreamcore.DreamJava;

import com.dreamfirestudios.dreamcore.DreamChat.DreamChat;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamVariableTest;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DreamfireClassAPI {
    private static final Map<Class<?>, BiConsumer<JavaPlugin, Object>> registrationActions = new HashMap<>();

    static {
        registrationActions.put(DreamVariableTest.class, (javaPlugin, instance) -> RegisterPulseVariableTest(javaPlugin, (DreamVariableTest) instance));
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
                        break;
                    }
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            javaPlugin.getLogger().severe("Failed to register classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void RegisterPulseVariableTest(JavaPlugin javaPlugin, DreamVariableTest dreamVariableTest){
        DreamChat.SendMessageToConsole(String.format("&8Registered DreamfireVariableTest: %s", dreamVariableTest.getClass().getSimpleName()), DreamMessageSettings.all());
        for(var classType : dreamVariableTest.ClassTypes()){
            DreamCore.DreamCore.DreamVariableTests.put(classType, dreamVariableTest);
        }
    }
}