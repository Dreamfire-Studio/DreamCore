package com.dreamfirestudios.dreamcore.DreamPersistentData;

import org.bukkit.persistence.PersistentDataType;

public enum PersistentDataTypes {
    BYTE(PersistentDataType.BYTE),
    SHORT(PersistentDataType.SHORT),
    INTEGER(PersistentDataType.INTEGER),
    LONG(PersistentDataType.LONG),
    FLOAT(PersistentDataType.FLOAT),
    DOUBLE(PersistentDataType.DOUBLE),
    BOOLEAN(PersistentDataType.BOOLEAN),
    STRING(PersistentDataType.STRING),
    BYTE_ARRAY(PersistentDataType.BYTE_ARRAY),
    INTEGER_ARRAY(PersistentDataType.INTEGER_ARRAY),
    LONG_ARRAY(PersistentDataType.LONG_ARRAY);

    public PersistentDataType persistentDataType;
    PersistentDataTypes(PersistentDataType persistentDataType){
        this.persistentDataType = persistentDataType;
    }
}
