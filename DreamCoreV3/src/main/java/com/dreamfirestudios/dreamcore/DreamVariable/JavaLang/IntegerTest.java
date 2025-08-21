package com.dreamfirestudios.dreamcore.DreamVariable.JavaLang;

import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

@PulseAutoRegister
public final class IntegerTest extends DreamAbstractVariableTest<Integer> {

    public IntegerTest() {
        super(PersistentDataTypes.INTEGER, Integer.class, true, true);
    }

    @Override
    protected Integer parseImpl(String raw) { return Integer.parseInt(raw.trim()); }

    @Override
    public Integer defaultValue() { return 0; }
}
