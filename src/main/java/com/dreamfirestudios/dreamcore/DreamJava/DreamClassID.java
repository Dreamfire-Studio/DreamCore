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