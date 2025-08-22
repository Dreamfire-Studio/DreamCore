package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Legacy interface for variable tests (kept for compatibility).
/// Prefer extending <see cref="DreamAbstractVariableTest{T}"/>.
/// </summary>
public interface DreamVariableTest {

    /// <summary>Backed persistent data type.</summary>
    PersistentDataTypes PersistentDataType();

    /// <summary>Checks if value is compatible / parsable.</summary>
    boolean IsType(Object variable);

    /// <summary>Supported runtime types.</summary>
    List<Class<?>> ClassTypes();

    /// <summary>Serialize a value.</summary>
    Object SerializeData(Object serializedData);

    /// <summary>Deserialize a stored value.</summary>
    Object DeSerializeData(Object serializedData);

    /// <summary>Default value.</summary>
    Object ReturnDefaultValue();

    /// <summary>Optional tab-suggestions for command UX.</summary>
    default List<String> TabData(List<String> baseTabList, String currentArgument) {
        return new ArrayList<>();
    }

    /// <summary>Batch serialize list of values.</summary>
    default List<Object> SerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for (var x : convert) data.add(SerializeData(x));
        return data;
    }

    /// <summary>Batch deserialize list of values.</summary>
    default List<Object> DeSerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for (var x : convert) data.add(DeSerializeData(x));
        return data;
    }
}