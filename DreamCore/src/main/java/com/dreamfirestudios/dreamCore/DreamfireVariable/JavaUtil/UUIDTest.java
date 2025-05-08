package com.dreamfirestudios.dreamCore.DreamfireVariable.JavaUtil;

import com.dreamfirestudios.dreamCore.DreamfirePersistentData.PersistentDataTypes;
import com.dreamfirestudios.dreamCore.DreamfireJava.PulseAutoRegister;
import com.dreamfirestudios.dreamCore.DreamfireVariable.DreamfireVariableTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PulseAutoRegister
public class UUIDTest implements DreamfireVariableTest {
    @Override
    public PersistentDataTypes PersistentDataType() { return PersistentDataTypes.STRING; }

    @Override
    public boolean IsType(Object variable) {
        try {
            var uuid = UUID.fromString(variable.toString());
            return true;
        } catch (Exception ex) { return false; }
    }

    @Override
    public List<Class<?>> ClassTypes() {
        var classTypes = new ArrayList<Class<?>>();
        classTypes.add(UUID.class);
        classTypes.add(UUID[].class);
        return classTypes;
    }

    @Override
    public Object SerializeData(Object serializedData) {
        return serializedData.toString();
    }

    @Override
    public Object DeSerializeData(Object serializedData) {
        try {return UUID.fromString(serializedData.toString());}
        catch (NumberFormatException e) { return serializedData; }
    }

    @Override
    public Object ReturnDefaultValue() { return UUID.randomUUID(); }

    @Override
    public List<String> TabData(List<String> baseTabList, String currentArgument) {
        return List.of("[UUID]");
    }
}
