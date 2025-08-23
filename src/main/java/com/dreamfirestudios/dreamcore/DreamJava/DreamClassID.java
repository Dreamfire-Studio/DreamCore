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

import java.util.UUID;

/// <summary>
/// Base class that supplies a stable, randomly assigned class instance ID.
/// </summary>
/// <remarks>
/// Useful for indexing holograms, loops, and other runtime objects in registries.
/// The ID is created once in the constructor and never changes.
/// </remarks>
/// <example>
/// <code>
/// public final class MyHologram extends DreamClassID { /* ... */ }
/// UUID id = new MyHologram().getClassID();
/// </code>
/// </example>
public abstract class DreamClassID {

    /// <summary>Unique identifier for this instance.</summary>
    public final UUID ClassID;

    /// <summary>
    /// Constructs a new instance with a fresh random UUID.
    /// </summary>
    protected DreamClassID(){
        ClassID = UUID.randomUUID();
    }

    /// <summary>
    /// Gets the unique identifier for this instance.
    /// </summary>
    /// <returns>Immutable UUID assigned at construction.</returns>
    public UUID getClassID() {
        return ClassID;
    }
}