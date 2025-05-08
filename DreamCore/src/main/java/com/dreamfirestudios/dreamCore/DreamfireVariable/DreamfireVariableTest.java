package com.dreamfirestudios.dreamCore.DreamfireVariable;

import com.dreamfirestudios.dreamCore.DreamfirePersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

public interface DreamfireVariableTest {
    PersistentDataTypes PersistentDataType();
    boolean IsType(Object variable);
    List<Class<?>> ClassTypes();
    Object SerializeData(Object serializedData);
    Object DeSerializeData(Object serializedData);
    Object ReturnDefaultValue();
    List<String> TabData(List<String> baseTabList, String currentArgument);
    default List<Object> SerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for(var x : convert) data.add(SerializeData(x));
        return data;
    }
    default List<Object> DeSerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for(var x : convert) data.add(DeSerializeData(x));
        return data;
    }
}
