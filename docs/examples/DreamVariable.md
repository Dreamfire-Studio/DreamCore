# DreamVariable — Beginner-Friendly Guide (Parsing, Validation, and Persistence)

> **What is this?**
> The **DreamVariable** system turns strings (e.g., command args, config text) into **typed values**, validates them, and provides **serialize/deserialize** logic for persistent storage. It comes with built-in tests for common Java types and supports arrays and primitives out-of-the-box.

---

## Big Picture

* **Tests** extend `DreamAbstractVariableTest<T>` and implement `parseImpl(...)` + `defaultValue()`.
* Each test declares a backing **PersistentDataTypes** entry for storage integration.
* The base class auto-adds **primitive** and **array** variants (configurable).
* A **legacy** `DreamVariableTest` interface exists for old code; new code should prefer the abstract base.
* Use `DreamVariableTestAPI` to **register** and **lookup** tests.

---

## Quick Start — Create Your Own Type

```java
public final class IntVarTest extends DreamAbstractVariableTest<Integer> {
    public IntVarTest() { super(PersistentDataTypes.INTEGER, Integer.class, true, true); }
    @Override protected Integer parseImpl(String raw) { return Integer.parseInt(raw.trim()); }
    @Override public Integer defaultValue() { return 0; }
}
```

Register (if not using `@PulseAutoRegister`):

```java
DreamVariableTestAPI.registerVarTest(Integer.class, /* legacy adapter or wrapper */, true);
```

> If you annotate with `@PulseAutoRegister`, your test is discovered and registered automatically by DreamCore.

---

## Built-in Tests (Java Types)

### `BoolTest` (`Boolean`, `boolean`, arrays)

* Backed by `PersistentDataTypes.BOOLEAN`.
* Accepts: `true/t/1/yes/y/on` and `false/f/0/no/n/off` (case-insensitive). Falls back to `Boolean.parseBoolean` for anything else.
* `defaultValue()` → `false`.

### `DoubleTest` (`Double`, `double`, arrays)

* Backed by `PersistentDataTypes.DOUBLE`.
* Parses via `Double.parseDouble(raw.trim())`.
* `defaultValue()` → `0.0d`.

### `FloatTest` (`Float`, `float`, arrays)

* Backed by `PersistentDataTypes.FLOAT`.
* Parses via `Float.parseFloat(raw.trim())`.
* `defaultValue()` → `0.0f`.

### `IntegerTest` (`Integer`, `int`, arrays)

* Backed by `PersistentDataTypes.INTEGER`.
* Parses via `Integer.parseInt(raw.trim())`.
* `defaultValue()` → `0`.

### `LongTest` (`Long`, `long`, arrays)

* Backed by `PersistentDataTypes.LONG`.
* Parses via `Long.parseLong(raw.trim())`.
* `defaultValue()` → `0L`.

### `UUIDTest` (`UUID`, arrays disabled for primitive reason; serializes as string)

* Backed by `PersistentDataTypes.STRING`.
* `parseImpl` uses `UUID.fromString`.
* `serialize(Object value)` → `value.toString()`.
* `deserialize(Object storage)` → `UUID.fromString(storage.toString())`.
* `defaultValue()` → `new UUID(0L, 0L)`.

---

## Core Base Class: `DreamAbstractVariableTest<T>`

**Constructor Variants**

```java
protected DreamAbstractVariableTest(PersistentDataTypes pdt, Class<T> valueType, boolean includePrimitive, boolean includeArray)
protected DreamAbstractVariableTest(PersistentDataTypes pdt, Class<T> valueType) // defaults includePrimitive=true, includeArray=true
```

**What it does for you**

* Records backing `PersistentDataTypes` for storage.
* Tracks `valueType` and a `supportedTypes` list including:

    * Boxed type (e.g., `Integer.class`).
    * Optional primitive (e.g., `int.class`).
    * Optional arrays of both (e.g., `Integer[]`, `int[]`).

**Methods to Implement**

* `protected abstract T parseImpl(String raw)` — trim/parse **one** value.
* `public abstract T defaultValue()` — fallback for empty/unset.

**Provided Methods**

* `public final PersistentDataTypes persistentType()` — storage type.
* `public final Class<T> valueType()` — boxed class.
* `public final List<Class<?>> supportedTypes()` — all supported runtime types.
* `public T parse(String raw)` — null-check + trim → `parseImpl`.
* `public Object serialize(Object value)` — if array → element-wise serialize; else `toString()`.
* `public Object deserialize(Object storage)` — if array → element-wise parse to boxed array; else parse single string.
* `public boolean isType(Object variable)` — accepts instances of supported types OR anything parsable via `parse(String.valueOf(variable))`.

**Array Handling**

* Deserialization allocates an **array of boxed type** and fills by parsing each element from `storageValue`.
* Serialization returns Object\[] for arrays (stringified per element by default).

---

## Legacy Interface: `DreamVariableTest`

The older interface remains for compatibility. It exposes similar operations with different names:

* `PersistentDataType()` → storage type.
* `IsType(Object variable)` → compatibility check.
* `ClassTypes()` → runtime supported classes.
* `SerializeData(Object)` / `DeSerializeData(Object)` → storage transforms.
* `ReturnDefaultValue()` → default.
* `TabData(List<String> baseTabList, String currentArgument)` → command tab suggestions; default empty.
* Batch helpers for `List<Object>` are included.

> New code should prefer subclassing `DreamAbstractVariableTest<T>`. If you need to interop, write a tiny adapter from the new test into the legacy interface.

---

## Registration & Lookup: `DreamVariableTestAPI`

### `registerVarTest(Class<?> test_class, DreamVariableTest variableLogic, boolean override_if_found) : boolean`

Registers a test for a given runtime class key. Returns `true` if newly registered or overridden, `false` otherwise.

### `returnTestFromType(Class<?> classType) : DreamVariableTest`

Looks up a registered test; `null` if not found.

### `ReturnTypeFromVariableTest(Class<?> classType) : PersistentDataTypes`

Gets the backing storage type for the given class via its registered test.

### `returnAsAllTypes(String text, boolean addVariableName, boolean isArrayType) : List<String>`

Given a raw string, returns display names of **all types** whose tests claim they can parse it.

* If `addVariableName` and at least one type matches, the **original text** is included first.
* Filters by `isArrayType` so you can focus on array or non-array forms.

**Example — Building Tab Suggestions**

```java
String arg = "10";
List<String> candidates = DreamVariableTestAPI.returnAsAllTypes(arg, true, false);
// might return ["10", "Integer", "Long", "Float", "Double", "Boolean"]
```

---

## Usage Patterns

### Parse Command Arguments

```java
DreamAbstractVariableTest<Integer> intTest = new IntegerTest();
int value = intTest.parse(userInput);
```

### Serialize to Storage

```java
Object storageValue = intTest.serialize(42); // "42"
```

### Deserialize from Storage

```java
Object value = intTest.deserialize("42"); // Integer(42)
```

### Arrays from Config

```java
DreamAbstractVariableTest<Double> dbl = new DoubleTest();
Object arr = dbl.deserialize(new Object[]{"1.5", "2.0", "3.25"}); // Double[]{1.5, 2.0, 3.25}
```

### Type Hints for UX

```java
List<String> hints = DreamVariableTestAPI.returnAsAllTypes("true", true, false);
// ["true", "Boolean"]
```

---

## Edge Cases & Best Practices

* **Null input**: `parse(null)` throws `IllegalArgumentException` — validate earlier.
* **Locale**: Numeric parsers use Java defaults; prefer plain ASCII digits/period. If you expect commas, add a custom parser.
* **Range validation**: Base tests don’t enforce ranges; if needed, subclass and clamp/validate.
* **Arrays**: `deserialize(Object storage)` expects an array to already be an **Object\[]-like** structure (e.g., from config deserialization). If you’re starting with a CSV string, split first then pass the pieces.
* **Precision**: For money/precise math, consider a `BigDecimalTest` rather than `double/float`.
* **Performance**: `isType` tries parsing as a fallback; if calling frequently, cache results.

---

## Troubleshooting

* **NumberFormatException**: The input string can’t be parsed; show a friendly message and offer examples.
* **UUID format**: Must be canonical (8-4-4-4-12). Wrap parse in try/catch to return a custom error.
* **Array shape**: If your config loader yields `List<?>` instead of arrays, convert to `Object[]` or update `deserialize` to handle `List` (see Suggestions).

---

## Suggestions / Future Enhancements

* **Strict/Lenient Modes**: Let tests declare a strict mode (e.g., `BoolTest` only accepts a known set; disable fallback `parseBoolean`).
* **Locale-Aware Numbers**: Optional `NumberFormat`/`DecimalFormat` support or an explicit `Locale` parameter.
* **Range-Constrained Tests**: e.g., `IntRangeTest(min, max)` with helpful error messages.
* **List Support**: Teach `deserialize` and `serialize` to handle `List<?>` in addition to arrays.
* **BigDecimalTest**: For precise decimal arithmetic (finance, stats).
* **Hex/Binary Parsers**: `IntFromRadixTest` that accepts prefixes like `0xFF`, `0b1010`, or a provided radix.
* **Unified Adapter**: A small adapter to expose `DreamAbstractVariableTest<T>` through the legacy `DreamVariableTest` interface automatically.
* **Registry by Name**: Add string keys (e.g., "int", "double") to look up tests for scripting/config DSLs.
* **Error Reporting Contract**: Return a structured result (success + message) instead of throwing, for better UX.
* **Caching Layer**: Memoize successful `isType` checks for hot paths (tab completion spam).