package com.dreamfirestudios.dreamcore.DreamfireStorage;

/**
 * <summary>
 * Immutable wrapper that holds a single stored value for use with
 * <see cref="DreamfireStorageManager"/>.
 * </summary>
 *
 * <typeparam name="T">Type of the stored data.</typeparam>
 * <remarks>
 * Records provide built-in immutability, equals/hashCode, and accessors.
 * </remarks>
 * <example>
 * <code>
 * var obj = new DreamfireStorageObject&lt;Integer&gt;(42);
 * Integer val = obj.storageData(); // 42
 * </code>
 * </example>
 */
public record DreamfireStorageObject<T>(T storageData) { }