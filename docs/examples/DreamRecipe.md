# DreamRecipe — Custom Crafting and Recipe System

The **DreamRecipe** system provides a unified way to declare and register recipes in your plugin. It abstracts Bukkit’s recipe classes (shaped, shapeless, furnace, smithing, merchant trades, etc.) into a single interface so you can define them consistently.

---

## Why Use DreamRecipe?

* ✅ Consistent recipe definitions (one interface for all types).
* ✅ Handles **shaped**, **shapeless**, **furnace-like**, **smithing**, and **merchant trades**.
* ✅ Generates Bukkit `Recipe` objects automatically.
* ✅ Provides sensible defaults, so you only implement what you need.

---

## Core Interfaces & Enums

### `IDreamRecipe`

This is the main contract for defining recipes.

You implement it to define:

* `recipeType()` → which family of recipe (see `RecipeType`).
* `recipeResult()` → the `ItemStack` that is produced.
* Optionally: shape, materials, experience, merchant trade options, etc.

### `RecipeType`

Enum values mapping to Bukkit recipe families:

* `BlastingRecipe`
* `CampfireRecipe`
* `FurnaceRecipe`
* `MerchantRecipe`
* `ShapedRecipe`
* `ShapelessRecipe`
* `SmithingRecipe`
* `SmokingRecipe`
* `StonecuttingRecipe`

---

## Functions in `IDreamRecipe`

### `recipeType()`

Defines the recipe family.

```java
@Override
public RecipeType recipeType() { return RecipeType.ShapedRecipe; }
```

### `recipeResult()`

Defines the output item.

```java
@Override
public ItemStack recipeResult() { return new ItemStack(Material.DIAMOND_SWORD); }
```

### `recipeName()`

Defaults to the class name. Override for custom naming.

```java
@Override
public String recipeName() { return "my_diamond_sword"; }
```

### `nameSpace()`

Generates the `NamespacedKey` local part. Defaults to `dreamcore_classname`. Used internally for registration.

### `recipeMaterials()` (for shaped)

Maps characters in the shape to `RecipeChoice` materials.

```java
@Override
public HashMap<Character, RecipeChoice> recipeMaterials() {
    var m = new HashMap<Character, RecipeChoice>();
    m.put('D', new RecipeChoice.MaterialChoice(Material.DIAMOND));
    m.put('S', new RecipeChoice.MaterialChoice(Material.STICK));
    return m;
}
```

### `recipeListMaterials()` (for shapeless)

A list of ingredients when order doesn’t matter.

### `recipeShape()` (for shaped)

The crafting table layout.

```java
@Override
public List<String> recipeShape() { return List.of(" D ", " D ", " S "); }
```

### `recipeSource()` & `recipeAddition()`

Used for smithing/furnace/smelting type recipes.

### `recipeExperience()` & `recipeCookingTime()`

For furnace-like recipes.

### `recipeUses()`, `recipeMaxUsers()`, `recipePriceMultiplier()`, etc.

For merchant recipes. Control how many times villagers can trade the item, whether it rewards XP, etc.

### `ReturnRecipe(JavaPlugin plugin)`

Factory method that builds the actual Bukkit `Recipe` based on the configured values.

```java
Recipe r = myRecipe.ReturnRecipe(plugin);
if (r != null) Bukkit.addRecipe(r);
```

---

## Example: Diamond Sword Recipe

```java
public final class MySwordRecipe implements IDreamRecipe {
    @Override
    public RecipeType recipeType() { return RecipeType.ShapedRecipe; }

    @Override
    public ItemStack recipeResult() { return new ItemStack(Material.DIAMOND_SWORD); }

    @Override
    public List<String> recipeShape() {
        return List.of(" D ", " D ", " S ");
    }

    @Override
    public HashMap<Character, RecipeChoice> recipeMaterials() {
        var m = new HashMap<Character, RecipeChoice>();
        m.put('D', new RecipeChoice.MaterialChoice(Material.DIAMOND));
        m.put('S', new RecipeChoice.MaterialChoice(Material.STICK));
        return m;
    }
}

// Registration
dreamCoreRecipe = new MySwordRecipe();
Recipe recipe = dreamCoreRecipe.ReturnRecipe(plugin);
if (recipe != null) Bukkit.addRecipe(recipe);
```

---

## Suggestions for Future Improvements

* 🔄 **Bulk registration API**: auto-register all recipes in a package.
* 🎨 **Custom result metadata**: support display names, lore, attributes.
* ⏱ **Recipe conditions**: allow recipes to be enabled/disabled at runtime (permissions, config flags).
* 📦 **Persistent storage**: integrate with configs so recipes can be edited by server owners.
* 🧩 **Recipe events**: add events for when recipes are crafted or discovered.

---