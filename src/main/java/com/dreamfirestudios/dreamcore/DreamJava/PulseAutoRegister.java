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