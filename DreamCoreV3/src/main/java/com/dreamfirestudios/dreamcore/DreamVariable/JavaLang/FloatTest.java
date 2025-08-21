package com.dreamfirestudios.dreamcore.DreamVariable.JavaLang;

import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

@PulseAutoRegister
public final class FloatTest extends DreamAbstractVariableTest<Float> {

    public FloatTest() {
        super(PersistentDataTypes.FLOAT, Float.class, true, true);
    }

    @Override
    protected Float parseImpl(String raw) { return Float.parseFloat(raw.trim()); }

    @Override
    public Float defaultValue() { return 0.0f; }
}