package com.dreamfirestudios.dreamcore.DreamJava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// <summary>
/// Marker annotation that flags a class for automatic discovery and registration.
/// </summary>
/// <remarks>
/// Classes annotated with <see cref="PulseAutoRegister"/> are discovered by
/// <see cref="DreamfireJavaAPI"/> and registered by <see cref="DreamClassAPI"/> based on
/// the interfaces they implement (e.g., <see cref="org.bukkit.event.Listener"/>).
/// </remarks>
/// <example>
/// <code>
/// @PulseAutoRegister
/// public final class MyListener implements Listener {
///     @EventHandler
///     public void onJoin(PlayerJoinEvent e) { /* ... */ }
/// }
/// </code>
/// </example>
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PulseAutoRegister { }