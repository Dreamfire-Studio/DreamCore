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

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamPersistentData.PersistentDataTypes;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Registration/lookup utilities for variable tests.
/// </summary>
/// <remarks>
/// Backed by <see cref="DreamCore.DreamVariableTests"/> (Class → logic).
/// </remarks>
public class DreamVariableTestAPI {

    /// <summary>
    /// Registers a variable test for a class type.
    /// </summary>
    /// <param name="test_class">Key class (e.g., Integer.class).</param>
    /// <param name="variableLogic">Test logic.</param>
    /// <param name="override_if_found">Override existing mapping if true.</param>
    /// <returns>True if newly registered; false if already existed and not overridden.</returns>
    public static boolean registerVarTest(Class<?> test_class, DreamVariableTest variableLogic, boolean override_if_found){
        if (DreamCore.DreamCore.DreamVariableTests.containsKey(test_class) && !override_if_found) return false;
        DreamCore.DreamCore.DreamVariableTests.put(test_class, variableLogic);
        return true;
    }

    /// <summary>
    /// Looks up a test by class type.
    /// </summary>
    /// <returns>Test instance or null.</returns>
    public static DreamVariableTest returnTestFromType(Class<?> classType){
        return DreamCore.DreamCore.DreamVariableTests.getOrDefault(classType, null);
    }

    /// <summary>
    /// Returns the persistent data type for a class (or null).
    /// </summary>
    public static PersistentDataTypes ReturnTypeFromVariableTest(Class<?> classType){
        var pulseVariableTest = returnTestFromType(classType);
        return pulseVariableTest == null ? null : pulseVariableTest.PersistentDataType();
    }

    /// <summary>
    /// Produces a list of type names this text could represent, optionally prefixed with the text itself.
    /// </summary>
    /// <param name="text">Raw string to test.</param>
    /// <param name="addVariableName">If true, include the string itself as first entry when at least one type matches.</param>
    /// <param name="isArrayType">Filter to array or non-array types.</param>
    /// <returns>List of display names.</returns>
    public static List<String> returnAsAllTypes(String text, boolean addVariableName, boolean isArrayType) {
        var all_types = new ArrayList<String>();
        for (var test_key : DreamCore.DreamVariableTests.keySet()) {
            var test = DreamCore.DreamVariableTests.get(test_key);
            if (!test.IsType(text)) continue;
            if (all_types.isEmpty() && addVariableName) all_types.add(text);
            for (var type : test.ClassTypes()) {
                if ((type.isArray() && isArrayType) || (!type.isArray() && !isArrayType)) {
                    if (!all_types.contains(type.getSimpleName())) all_types.add(type.getSimpleName());
                }
            }
        }
        return all_types;
    }
}