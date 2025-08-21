package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

public interface DreamVariableTest {

    PersistentDataTypes PersistentDataType();
    boolean IsType(Object variable);
    List<Class<?>> ClassTypes();
    Object SerializeData(Object serializedData);
    Object DeSerializeData(Object serializedData);
    Object ReturnDefaultValue();
    default List<String> TabData(List<String> baseTabList, String currentArgument) {
        return new ArrayList<>();
    }

    default List<Object> SerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for (var x : convert) data.add(SerializeData(x));
        return data;
    }

    default List<Object> DeSerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for (var x : convert) data.add(DeSerializeData(x));
        return data;
    }
}
