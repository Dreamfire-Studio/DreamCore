/*  Copyright (c) Dreamfire Studios
 *  DocFX-friendly XML docs
 */

package com.dreamfirestudios.dreamcore.DreamPersistentData;

import org.bukkit.persistence.PersistentDataType;

/// <summary>
/// Logical wrappers over Bukkit <see cref="PersistentDataType"/> constants,
/// used for grouping and dumping containers by type.
/// </summary>
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

    /// <summary>Backed Bukkit type.</summary>
    public PersistentDataType persistentDataType;

    /// <summary>Constructs the enum entry.</summary>
    /// <param name="persistentDataType">Backed Bukkit type.</param>
    PersistentDataTypes(PersistentDataType persistentDataType){
        this.persistentDataType = persistentDataType;
    }
}