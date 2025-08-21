package com.dreamfirestudios.dreamcore.DreamVariable.JavaLang;

import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import com.dreamfirestudios.dreamcore.DreamVariable.DreamAbstractVariableTest;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

@PulseAutoRegister
public final class BoolTest extends DreamAbstractVariableTest<Boolean> {

    public BoolTest() {
        super(PersistentDataTypes.BOOLEAN, Boolean.class, true, true);
    }

    @Override
    protected Boolean parseImpl(String raw) {
        String s = raw.trim().toLowerCase();
        return switch (s) {
            case "true", "t", "1", "yes", "y", "on" -> true;
            case "false", "f", "0", "no", "n", "off" -> false;
            default -> Boolean.parseBoolean(s);
        };
    }

    @Override
    public Boolean defaultValue() { return false; }
}
