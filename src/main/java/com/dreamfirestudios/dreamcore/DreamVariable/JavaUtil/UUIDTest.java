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
