package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class DreamAbstractVariableTest<T> {

    private final PersistentDataTypes pdt;
    private final Class<T> valueType;
    private final List<Class<?>> supported;

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

    protected DreamAbstractVariableTest(PersistentDataTypes pdt, Class<T> valueType) {
        this(pdt, valueType, true, true);
    }

    public final PersistentDataTypes persistentType() { return pdt; }
    public final Class<T> valueType() { return valueType; }
    public final List<Class<?>> supportedTypes() { return List.copyOf(supported); }
    protected abstract T parseImpl(String raw);

    public abstract T defaultValue();

    public T parse(String raw) {
        if (raw == null) throw new IllegalArgumentException("raw cannot be null");
        return parseImpl(raw.trim());
    }

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
