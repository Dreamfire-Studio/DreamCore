package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Base class for variable “tests” that parse/validate values and serialize/deserialise to persistent storage.
/// </summary>
/// <typeparam name="T">Primary boxed value type (e.g., Integer, Double).</typeparam>
/// <remarks>
/// Supports boxed type, optional primitive type, and optional array forms of both.
/// </remarks>
/// <example>
/// <code>
/// public final class IntVarTest extends DreamAbstractVariableTest&lt;Integer&gt; {
///     public IntVarTest() { super(PersistentDataTypes.INTEGER, Integer.class); }
///     protected Integer parseImpl(String raw) { return Integer.parseInt(raw); }
///     public Integer defaultValue() { return 0; }
/// }
/// </code>
/// </example>
public abstract class DreamAbstractVariableTest<T> {

    private final PersistentDataTypes pdt;
    private final Class<T> valueType;
    private final List<Class<?>> supported;

    /// <summary>
    /// Creates a variable test with explicit flags for primitive and array support.
    /// </summary>
    /// <param name="pdt">Backed persistent data type.</param>
    /// <param name="valueType">Boxed value type.</param>
    /// <param name="includePrimitive">Include primitive type variant.</param>
    /// <param name="includeArray">Include array variants.</param>
    protected DreamAbstractVariableTest(PersistentDataTypes pdt, Class<T> valueType, boolean includePrimitive, boolean includeArray) {
        this.pdt = pdt;
        this.valueType = valueType;
        this.supported = new ArrayList<>();
        this.supported.add(valueType);

        Class<?> primitive = boxedToPrimitive(valueType);
        if (includePrimitive && primitive != null){
            supported.add(primitive);
        }

        if (includeArray) {
            supported.add(Array.newInstance(valueType, 0).getClass());
            if (primitive != null) supported.add(Array.newInstance(primitive, 0).getClass());
        }
    }

    /// <summary>
    /// Creates a variable test with primitive+array support enabled.
    /// </summary>
    protected DreamAbstractVariableTest(PersistentDataTypes pdt, Class<T> valueType) {
        this(pdt, valueType, true, true);
    }

    /// <summary>Persistent data type backing this test.</summary>
    public final PersistentDataTypes persistentType() { return pdt; }
    /// <summary>Primary boxed value type.</summary>
    public final Class<T> valueType() { return valueType; }
    /// <summary>All supported runtime types (boxed/primitive/arrays as configured).</summary>
    public final List<Class<?>> supportedTypes() { return List.copyOf(supported); }

    /// <summary>Parses a trimmed string into <typeparamref name="T"/>.</summary>
    protected abstract T parseImpl(String raw);
    /// <summary>Default value for this type.</summary>
    public abstract T defaultValue();

    /// <summary>
    /// Parses an input string (must be non-null).
    /// </summary>
    /// <param name="raw">Raw text.</param>
    /// <returns>Parsed value.</returns>
    public T parse(String raw) {
        if (raw == null) throw new IllegalArgumentException("raw cannot be null");
        return parseImpl(raw.trim());
    }

    /// <summary>
    /// Serializes an object (or array) into a storage-friendly representation.
    /// </summary>
    /// <param name="value">Value or array.</param>
    /// <returns>Serialized object or array (strings), or null.</returns>
    public Object serialize(Object value) {
        if (value == null) return null;
        if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            Object[] out = new Object[len];
            for (int i = 0; i < len; i++) out[i] = serialize(Array.get(value, i));
            return out;
        }
        return value.toString();
    }

    /// <summary>
    /// Deserializes storage into value (or array of values).
    /// </summary>
    /// <param name="storageValue">Stored object.</param>
    /// <returns>Deserialized value or array.</returns>
    public Object deserialize(Object storageValue) {
        if (storageValue == null) return null;
        if (storageValue.getClass().isArray()) {
            int len = Array.getLength(storageValue);
            Object arr = Array.newInstance(valueType, len);
            for (int i = 0; i < len; i++) {
                Array.set(arr, i, parse(String.valueOf(Array.get(storageValue, i))));
            }
            return arr;
        }
        return parse(String.valueOf(storageValue));
    }

    /// <summary>
    /// Checks if an object is compatible (direct type or parsable).
    /// </summary>
    public boolean isType(Object variable) {
        if (variable == null) return false;
        for (Class<?> c : supported) if (c.isInstance(variable)) return true;
        try { parse(String.valueOf(variable)); return true; } catch (RuntimeException ex) { return false; }
    }

    private static Class<?> boxedToPrimitive(Class<?> boxed) {
        if (boxed == Boolean.class) return boolean.class;
        if (boxed == Integer.class) return int.class;
        if (boxed == Long.class) return long.class;
        if (boxed == Double.class) return double.class;
        if (boxed == Float.class) return float.class;
        if (boxed == Short.class) return short.class;
        if (boxed == Byte.class) return byte.class;
        if (boxed == Character.class) return char.class;
        return null;
    }
}