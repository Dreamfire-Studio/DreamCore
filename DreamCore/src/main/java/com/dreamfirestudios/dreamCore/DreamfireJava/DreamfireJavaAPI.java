package com.dreamfirestudios.dreamCore.DreamfireJava;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DreamfireJavaAPI {
    public static List<Class<?>> getAutoRegisterClasses(JavaPlugin javaPlugin) {
        try {
            return getAutoRegisterClassesRaw(javaPlugin);
        } catch (URISyntaxException | IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error while retrieving auto-register classes", e);
        }
    }

    public static List<Class<?>> getAutoRegisterClassesRaw(JavaPlugin javaPlugin) throws URISyntaxException, IOException, ClassNotFoundException {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        for (Class<?> clazz : getAllClassesFromPlugin(javaPlugin)) {
            if (clazz.isAnnotationPresent(PulseAutoRegister.class)) {
                annotatedClasses.add(clazz);
            }
        }
        return annotatedClasses;
    }

    public static List<Class<?>> getAllClassesFromPlugin(JavaPlugin javaPlugin)
            throws URISyntaxException, IOException, ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();
        for (String className : getAllClassNamesFromPlugin(javaPlugin)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    private static List<String> getAllClassNamesFromPlugin(JavaPlugin javaPlugin) throws URISyntaxException, IOException {
        File pluginFile = new File(javaPlugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        List<String> classNames = new ArrayList<>();
        try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(pluginFile))) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (isValidClassEntry(entry, javaPlugin)) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }

    private static boolean isValidClassEntry(ZipEntry entry, JavaPlugin javaPlugin) {
        if (entry.isDirectory() || !entry.getName().endsWith(".class") || entry.getName().contains("$")) {
            return false;
        }
        String className = entry.getName().replace('/', '.').replace(".class", "");
        return className.startsWith(javaPlugin.getClass().getPackageName());
    }
}
