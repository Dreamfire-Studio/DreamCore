package com.dreamfirestudios.dreamCore.DreamfireVariable;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

public class DreamfireVariable {
    public static boolean registerVarTest(Class<?> test_class, DreamfireVariableTest variableLogic, boolean override_if_found){
        if(DreamCore.GetDreamfireCore().dreamfireVariableTestLinkedHashMap.containsKey(test_class) && !override_if_found) return false;
        DreamCore.GetDreamfireCore().dreamfireVariableTestLinkedHashMap.put(test_class, variableLogic);
        return true;
    }

    public static DreamfireVariableTest returnTestFromType(Class<?> classType){
        return DreamCore.GetDreamfireCore().dreamfireVariableTestLinkedHashMap.getOrDefault(classType, null);
    }

    public static PersistentDataTypes ReturnTypeFromVariableTest(Class<?> classType){
        var pulseVariableTest = returnTestFromType(classType);
        return pulseVariableTest == null ? null : pulseVariableTest.PersistentDataType();
    }

    public static List<String> returnAsAllTypes(String text, boolean addVariableName, boolean isArrayType) {
        var all_types = new ArrayList<String>();
        for(var test_key : DreamCore.GetDreamfireCore().dreamfireVariableTestLinkedHashMap.keySet()){
            var test = DreamCore.GetDreamfireCore().dreamfireVariableTestLinkedHashMap.get(test_key);
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
