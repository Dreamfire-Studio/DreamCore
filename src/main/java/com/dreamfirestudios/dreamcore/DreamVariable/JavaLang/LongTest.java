package com.dreamfirestudios.dreamcore.DreamVariable.JavaLang;

import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

@PulseAutoRegister
public final class LongTest extends DreamAbstractVariableTest<Long> {

    public LongTest() {
        super(PersistentDataTypes.LONG, Long.class, true, true);
    }

    @Override
    protected Long parseImpl(String raw) { return Long.parseLong(raw.trim()); }

    @Override
    public Long defaultValue() { return 0L; }
}
