package com.dreamfirestudios.dreamCore.DreamfireVariable.JavaLang;

import com.dreamfirestudios.dreamCore.DreamfirePersistentData.PersistentDataTypes;
import com.dreamfirestudios.dreamCore.DreamfireJava.PulseAutoRegister;
import com.dreamfirestudios.dreamCore.DreamfireVariable.DreamfireVariableTest;

import java.util.ArrayList;
import java.util.List;

@PulseAutoRegister
public class DoubleTest implements DreamfireVariableTest {
    @Override
    public PersistentDataTypes PersistentDataType() { return PersistentDataTypes.DOUBLE; }
    @Override
    public boolean IsType(Object variable) {
        try{
            double x = Double.parseDouble(variable.toString());
            return true;
        }catch (NumberFormatException e){ return false; }
    }

    @Override
    public List<Class<?>> ClassTypes() {
        var classTypes = new ArrayList<Class<?>>();
        classTypes.add(double.class);
        classTypes.add(Double.class);
        classTypes.add(double[].class);
        classTypes.add(Double[].class);
        return classTypes;
    }

    @Override
    public Object SerializeData(Object serializedData) {
        return serializedData;
    }

    @Override
    public Object DeSerializeData(Object serializedData) {
        try {return Double.parseDouble(serializedData.toString());}
        catch (NumberFormatException e) { return serializedData; }
    }

    @Override
    public Object ReturnDefaultValue() { return 0.0; }

    @Override
    public List<String> TabData(List<String> baseTabList, String currentArgument) {
        return List.of("[DOUBLE]");
    }
}
