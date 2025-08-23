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
package com.dreamfirestudios.dreamcore.DreamVariable;

import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Legacy interface for variable tests (kept for compatibility).
/// Prefer extending <see cref="DreamAbstractVariableTest{T}"/>.
/// </summary>
public interface DreamVariableTest {

    /// <summary>Backed persistent data type.</summary>
    PersistentDataTypes PersistentDataType();

    /// <summary>Checks if value is compatible / parsable.</summary>
    boolean IsType(Object variable);

    /// <summary>Supported runtime types.</summary>
    List<Class<?>> ClassTypes();

    /// <summary>Serialize a value.</summary>
    Object SerializeData(Object serializedData);

    /// <summary>Deserialize a stored value.</summary>
    Object DeSerializeData(Object serializedData);

    /// <summary>Default value.</summary>
    Object ReturnDefaultValue();

    /// <summary>Optional tab-suggestions for command UX.</summary>
    default List<String> TabData(List<String> baseTabList, String currentArgument) {
        return new ArrayList<>();
    }

    /// <summary>Batch serialize list of values.</summary>
    default List<Object> SerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for (var x : convert) data.add(SerializeData(x));
        return data;
    }

    /// <summary>Batch deserialize list of values.</summary>
    default List<Object> DeSerializeData(List<Object> convert){
        var data = new ArrayList<>();
        for (var x : convert) data.add(DeSerializeData(x));
        return data;
    }
}