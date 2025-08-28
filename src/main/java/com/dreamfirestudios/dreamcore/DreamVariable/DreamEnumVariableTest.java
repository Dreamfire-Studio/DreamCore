package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

public final class DreamEnumVariableTest<T extends Enum<T>> extends DreamAbstractVariableTest<T> {

    private final Class<T> enumClass;

    public DreamEnumVariableTest(Class<T> enumClass) {
        super(PersistentDataTypes.STRING, enumClass, false, true);
        this.enumClass = enumClass;
    }

    @Override
    protected T parseImpl(String raw) {
        return Enum.valueOf(enumClass, raw.trim().toUpperCase());
    }

    @Override
    public T defaultValue() {
        return enumClass.getEnumConstants()[0];
    }
}
