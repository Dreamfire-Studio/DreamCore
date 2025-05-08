package com.dreamfirestudios.dreamCore.DreamfireVariable.JavaLang;

import com.dreamfirestudios.dreamCore.DreamfirePersistentData.PersistentDataTypes;
import com.dreamfirestudios.dreamCore.DreamfireJava.PulseAutoRegister;
import com.dreamfirestudios.dreamCore.DreamfireVariable.DreamfireVariableTest;

import java.util.ArrayList;
import java.util.List;

@PulseAutoRegister
public class BoolTest implements DreamfireVariableTest {
    @Override
    public PersistentDataTypes PersistentDataType() { return PersistentDataTypes.BOOLEAN; }
    @Override
    public boolean IsType(Object variable) {
        try{
            boolean x = Boolean.parseBoolean(variable.toString());
            return true;
        }catch (NumberFormatException e){ return false; }
    }

    @Override
    public List<Class<?>> ClassTypes() {
        var classTypes = new ArrayList<Class<?>>();
        classTypes.add(boolean.class);
        classTypes.add(Boolean.class);
        classTypes.add(boolean[].class);
        classTypes.add(Boolean[].class);
        return classTypes;
    }

    @Override
    public Object SerializeData(Object serializedData) {
        return serializedData;
    }

    @Override
    public Object DeSerializeData(Object serializedData) {
        try {return Boolean.parseBoolean(serializedData.toString());}
        catch (NumberFormatException e) { return serializedData; }
    }

    @Override
    public Object ReturnDefaultValue() { return false; }

    @Override
    public List<String> TabData(List<String> baseTabList, String currentArgument) {
        return List.of("[Boolean]");
    }
}
