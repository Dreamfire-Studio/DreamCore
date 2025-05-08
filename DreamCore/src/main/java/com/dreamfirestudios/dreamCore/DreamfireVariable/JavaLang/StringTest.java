package com.dreamfirestudios.dreamCore.DreamfireVariable.JavaLang;

import com.dreamfirestudios.dreamCore.DreamfirePersistentData.PersistentDataTypes;
import com.dreamfirestudios.dreamCore.DreamfireJava.PulseAutoRegister;
import com.dreamfirestudios.dreamCore.DreamfireVariable.DreamfireVariableTest;

import java.util.ArrayList;
import java.util.List;

@PulseAutoRegister
public class StringTest implements DreamfireVariableTest {
    @Override
    public PersistentDataTypes PersistentDataType() { return PersistentDataTypes.STRING; }
    @Override
    public boolean IsType(Object variable) {
        try {
            var x = variable.toString();
            return true;
        } catch (NumberFormatException e) { return false; }
    }

    @Override
    public List<Class<?>> ClassTypes() {
        var classTypes = new ArrayList<Class<?>>();
        classTypes.add(String.class);
        classTypes.add(String[].class);
        return classTypes;
    }

    @Override
    public Object SerializeData(Object serializedData) {
        return serializedData;
    }

    @Override
    public Object DeSerializeData(Object serializedData) {
        return serializedData.toString();
    }

    @Override
    public Object ReturnDefaultValue() { return ""; }

    @Override
    public List<String> TabData(List<String> baseTabList, String currentArgument) {
        return List.of("[STRING]");
    }
}
