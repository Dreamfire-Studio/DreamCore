package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;
import org.bukkit.Bukkit;

public final class DreamEnumVariableTest<T extends Enum<T>> extends DreamAbstractVariableTest<T> {

    private final Class<T> enumClass;

    public DreamEnumVariableTest(Class<T> enumClass) {
        super(PersistentDataTypes.STRING, enumClass, false, true);
        this.enumClass = enumClass;
    }

    @Override
    protected T parseImpl(String raw) {
        if (raw == null || raw.isBlank()) {
            return defaultValue();
        }

        String trimmed = raw.trim();

        try {
            return Enum.valueOf(enumClass, trimmed);
        } catch (IllegalArgumentException ignored) {}

        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(trimmed)) {
                return constant;
            }
        }
        T def = defaultValue();
        Bukkit.getLogger().warning("[DreamCore] Invalid enum value '" + raw + "' for " + enumClass.getSimpleName() + ", defaulting to " + def.name());
        return def;
    }

    @Override
    public T defaultValue() {
        return enumClass.getEnumConstants()[0];
    }
}
