package com.dreamfirestudios.dreamcore.DreamVariable.JavaLang;

import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

@PulseAutoRegister
public final class DoubleTest extends DreamAbstractVariableTest<Double> {

    public DoubleTest() {
        super(PersistentDataTypes.DOUBLE, Double.class, true, true);
    }

    @Override
    protected Double parseImpl(String raw) { return Double.parseDouble(raw.trim()); }

    @Override
    public Double defaultValue() { return 0.0d; }
}
