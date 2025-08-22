package com.dreamfirestudios.dreamcore.DreamJava;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/// <summary>
/// Reflection utilities for scanning a plugin JAR to discover annotated classes.
/// </summary>
/// <remarks>
/// This helper enumerates classes within the calling plugin’s package and returns those
/// annotated with <see cref="PulseAutoRegister"/>. It is used by <see cref="DreamClassAPI"/>.
/// </remarks>
/// <example>
/// <code>
/// List&lt;Class&lt;?&gt;&gt; classes = DreamfireJavaAPI.getAutoRegisterClasses(plugin);
/// </code>
/// </example>
public class DreamfireJavaAPI {

    /// <summary>
    /// Retrieves auto‑register classes, wrapping checked exceptions.
    /// </summary>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <returns>List of annotated classes.</returns>
    /// <exception cref="RuntimeException">If scanning or loading fails.</exception>
    public static List<Class<?>> getAutoRegisterClasses(JavaPlugin javaPlugin) {
        try {
            return getAutoRegisterClassesRaw(javaPlugin);
        } catch (URISyntaxException | IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error while retrieving auto-register classes", e);
        }
    }

    /// <summary>
    /// Retrieves classes annotated with <see cref="PulseAutoRegister"/> (propagates exceptions).
    /// </summary>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <returns>List of annotated classes.</returns>
    /// <exception cref="URISyntaxException">If plugin location URI is invalid.</exception>
    /// <exception cref="IOException">If JAR cannot be read.</exception>
    /// <exception cref="ClassNotFoundException">If a class cannot be loaded.</exception>
    public static List<Class<?>> getAutoRegisterClassesRaw(JavaPlugin javaPlugin)
            throws URISyntaxException, IOException, ClassNotFoundException {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        for (Class<?> clazz : getAllClassesFromPlugin(javaPlugin)) {
            if (clazz.isAnnotationPresent(PulseAutoRegister.class)) {
                annotatedClasses.add(clazz);
            }
        }
        return annotatedClasses;
    }

    /// <summary>
    /// Loads all classes defined under the plugin’s base package from the plugin JAR.
    /// </summary>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <returns>Loaded classes.</returns>
    /// <exception cref="URISyntaxException">If plugin location URI is invalid.</exception>
    /// <exception cref="IOException">If JAR cannot be read.</exception>
    /// <exception cref="ClassNotFoundException">If a class cannot be loaded.</exception>
    public static List<Class<?>> getAllClassesFromPlugin(JavaPlugin javaPlugin)
            throws URISyntaxException, IOException, ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();
        for (String className : getAllClassNamesFromPlugin(javaPlugin)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    /// <summary>
    /// Reads fully qualified class names from the plugin JAR under the plugin package.
    /// </summary>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <returns>List of class names.</returns>
    /// <exception cref="URISyntaxException">If plugin location URI is invalid.</exception>
    /// <exception cref="IOException">If JAR cannot be read.</exception>
    /// <remarks>
    /// Filters out inner classes (names containing <c>$</c>) and directories.
    /// </remarks>
    private static List<String> getAllClassNamesFromPlugin(JavaPlugin javaPlugin)
            throws URISyntaxException, IOException {
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

    /// <summary>
    /// Checks if a JAR entry represents a top‑level class within the plugin’s package.
    /// </summary>
    /// <param name="entry">ZIP entry.</param>
    /// <param name="javaPlugin">Calling plugin.</param>
    /// <returns><c>true</c> if the entry is a valid class for scanning.</returns>
    private static boolean isValidClassEntry(ZipEntry entry, JavaPlugin javaPlugin) {
        if (entry.isDirectory() || !entry.getName().endsWith(".class") || entry.getName().contains("$")) {
            return false;
        }
        String className = entry.getName().replace('/', '.').replace(".class", "");
        return className.startsWith(javaPlugin.getClass().getPackageName());
    }
}