/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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