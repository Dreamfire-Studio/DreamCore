package com.dreamfirestudios.dreamcore.DreamVariable.JavaUtil;

import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.util.UUID;

@PulseAutoRegister
public final class UUIDTest extends DreamAbstractVariableTest<UUID> {

    public UUIDTest() {
        super(PersistentDataTypes.STRING, UUID.class, false, true);
    }

    @Override
    protected UUID parseImpl(String raw) { return UUID.fromString(raw.trim()); }

    @Override
    public Object serialize(Object value) {
        return value == null ? null : value.toString(); // UUID -> String
    }

    @Override
    public Object deserialize(Object storageValue) {
        return storageValue == null ? null : UUID.fromString(storageValue.toString()); // String -> UUID
    }

    @Override
    public UUID defaultValue() {
        return new UUID(0L, 0L);
    }
}
