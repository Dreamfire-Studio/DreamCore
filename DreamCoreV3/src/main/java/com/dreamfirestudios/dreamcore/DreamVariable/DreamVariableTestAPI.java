package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

public class DreamVariableTestAPI {
    public static boolean registerVarTest(Class<?> test_class, DreamVariableTest variableLogic, boolean override_if_found){
        if(DreamCore.DreamCore.DreamVariableTests.containsKey(test_class) && !override_if_found) return false;
        DreamCore.DreamCore.DreamVariableTests.put(test_class, variableLogic);
        return true;
    }

    public static DreamVariableTest returnTestFromType(Class<?> classType){
        return DreamCore.DreamCore.DreamVariableTests.getOrDefault(classType, null);
    }

    public static PersistentDataTypes ReturnTypeFromVariableTest(Class<?> classType){
        var pulseVariableTest = returnTestFromType(classType);
        return pulseVariableTest == null ? null : pulseVariableTest.PersistentDataType();
    }

    public static List<String> returnAsAllTypes(String text, boolean addVariableName, boolean isArrayType) {
        var all_types = new ArrayList<String>();
        for(var test_key : DreamCore.DreamCore.DreamVariableTests.keySet()){
            var test = DreamCore.DreamCore.DreamVariableTests.get(test_key);
            if(!test.IsType(text)) continue;
            if(all_types.isEmpty() && addVariableName) all_types.add(text);
            for(var type : test.ClassTypes()){
                if((type.isArray() && isArrayType) || (!type.isArray() && !isArrayType)){
                    if(!all_types.contains(type.getSimpleName())) all_types.add(type.getSimpleName());
                }
            }

        }
        return all_types;
    }
}
