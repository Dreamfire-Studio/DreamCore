package com.dreamfirestudios.dreamcore.DreamRecipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Define a custom recipe in a version-tolerant way.
 * Implementors only fill in what they need; the defaults are safe.
 *
 * Register with {@link DreamRecipeRegistry#register(JavaPlugin, IDreamRecipe)}.
 */
public interface IDreamRecipe {

    // ── Identity ─────────────────────────────────────────────────────────────────

    /** The concrete type of recipe to build. */
    @NotNull RecipeType recipeType();

    /** Resulting item when the recipe is crafted. Must be a valid non-AIR stack. */
    @NotNull ItemStack recipeResult();

    /** Human/readable name for debugging and logs. */
    default @NotNull String recipeName() { return getClass().getSimpleName(); }

    /** Key namespace (lowercase, unique). Default: pluginName_recipeName (lowercased). */
    default @NotNull String nameSpace(@NotNull JavaPlugin plugin) {
        return (plugin.getName() + "_" + recipeName()).toLowerCase(Locale.ROOT);
    }

    /** Convenience builder for the NamespacedKey for this recipe. */
    default @NotNull NamespacedKey key(@NotNull JavaPlugin plugin) {
        return new NamespacedKey(plugin, nameSpace(plugin));
    }

    // ── Common inputs (used by various recipe kinds) ─────────────────────────────

    /** For furnace-like recipes (furnace/blast/smoke/campfire/stonecut). */
    default @NotNull RecipeChoice recipeSource() {
        return new RecipeChoice.MaterialChoice(Collections.singletonList(Material.AIR));
    }

    /** For smithing/transform additions (e.g., NETHERITE_INGOT). */
    default @NotNull RecipeChoice recipeAddition() {
        return new RecipeChoice.MaterialChoice(Collections.singletonList(Material.AIR));
    }

    /** For 1.20+ smithing transform: smithing template (e.g., NETHERITE_UPGRADE_SMITHING_TEMPLATE). */
    default @NotNull RecipeChoice recipeTemplate() {
        return new RecipeChoice.MaterialChoice(Collections.singletonList(Material.AIR));
    }

    /** Shaped: map single chars (e.g. 'I', 'S') to material choices. */
    default @NotNull Map<Character, RecipeChoice> recipeMaterials() { return new HashMap<>(); }

    /** Shaped: up to 3 rows (each 1–3 chars). */
    default @NotNull List<String> recipeShape() { return new ArrayList<>(); }

    /** Shapeless: list of choices. */
    default @NotNull List<RecipeChoice> recipeListMaterials() { return new ArrayList<>(); }

    /** Villager trade params (if using MERCHANT). */
    default int recipeUses() { return 1; }
    default int recipeMaxUsers() { return 0; } // 0 = unlimited in Bukkit MerchantRecipe
    default boolean recipeExperienceReward() { return false; }
    default int recipeVillagerExperience() { return 0; }
    default float recipePriceMultiplier() { return 1f; }
    default int recipeDemand() { return 0; }
    default int recipeSpecialPrice() { return 0; }

    /** Smelt/Blast/Smoke/Campfire timings & XP. */
    default float recipeExperience() { return 0f; }
    default int recipeCookingTime() { return 200; }

    // ── Permissions (checked by DreamRecipeRegistry’s PrepareItemCraftEvent) ─────

    /** If true, crafting this recipe requires a permission. */
    default boolean requiresPermission() { return false; }

    /** Permission node to check. Ignored if {@link #requiresPermission()} is false. */
    default @NotNull String permissionNode() { return "dreamcore.recipe." + recipeName().toLowerCase(Locale.ROOT); }

    // ── Build the recipe (Paper/Bukkit). Do not register directly; use the registry. ─

    /**
     * Build a Bukkit Recipe object for registration.
     * Some types (e.g., Merchant) are not registered via Server#addRecipe; the registry will handle that.
     */
    default @Nullable Recipe buildRecipe(@NotNull JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        ItemStack result = recipeResult();
        if (result == null || result.getType() == Material.AIR) {
            throw new IllegalArgumentException(recipeName() + ": result cannot be null/AIR");
        }

        NamespacedKey k = key(plugin);

        switch (recipeType()) {
            case BlastingRecipe:
                return new BlastingRecipe(k, result, validateSource("Blasting"), recipeExperience(), recipeCookingTime());

            case CampfireRecipe:
                return new CampfireRecipe(k, result, validateSource("Campfire"), recipeExperience(), recipeCookingTime());

            case FurnaceRecipe:
                return new FurnaceRecipe(k, result, validateSource("Furnace"), recipeExperience(), recipeCookingTime());

            case SmokingRecipe:
                return new SmokingRecipe(k, result, validateSource("Smoking"), recipeExperience(), recipeCookingTime());

            case StonecuttingRecipe:
                return new StonecuttingRecipe(k, result, validateSource("Stonecut"));

            case ShapedRecipe:
                return buildShaped(k, result);

            case ShapelessRecipe:
                return buildShapeless(k, result);

            case SmithingRecipe:
                // Prefer 1.20+ SmithingTransformRecipe(template, base, addition)
                // Fallback to legacy SmithingRecipe if present.
                return buildSmithingVersionSafe(k, result);

            case MerchantRecipe:
                // Not registered via addRecipe(). The registry returns null here and stores the
                // MerchantRecipe separately for your villager-provisioning code.
                return null;
        }
        return null;
    }

    // ── Internal builders & validators (default methods so implementors inherit behavior) ─

    private @NotNull RecipeChoice validateSource(String label) {
        RecipeChoice src = recipeSource();
        if (isAirChoice(src)) {
            throw new IllegalArgumentException(recipeName() + ": " + label + " source cannot be AIR/empty");
        }
        return src;
    }

    private boolean isAirChoice(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.MaterialChoice mc) {
            for (Material m : mc.getChoices()) if (m != Material.AIR) return false;
            return true;
        }
        if (choice instanceof RecipeChoice.ExactChoice ec) {
            for (ItemStack s : ec.getChoices()) if (s != null && s.getType() != Material.AIR) return false;
            return true;
        }
        return false;
    }

    private @NotNull ShapedRecipe buildShaped(NamespacedKey key, ItemStack result) {
        List<String> shape = recipeShape();
        if (shape.isEmpty() || shape.size() > 3)
            throw new IllegalArgumentException(recipeName() + ": shaped recipe must have 1–3 rows");

        // Validate each row length 1–3
        for (String row : shape) {
            if (row == null || row.isEmpty() || row.length() > 3)
                throw new IllegalArgumentException(recipeName() + ": each shaped row length must be 1–3");
        }

        ShapedRecipe shaped = new ShapedRecipe(key, result);
        // Pad to 3 rows for Bukkit if you want strict grid alignment; optional. Here we pass exact.
        shaped.shape(shape.toArray(new String[0]));

        Map<Character, RecipeChoice> map = recipeMaterials();
        if (map.isEmpty())
            throw new IllegalArgumentException(recipeName() + ": shaped recipe needs materials map");

        for (Map.Entry<Character, RecipeChoice> e : map.entrySet()) {
            Character ch = e.getKey();
            RecipeChoice choice = e.getValue();
            if (ch == null) continue;
            if (choice == null || isAirChoice(choice))
                throw new IllegalArgumentException(recipeName() + ": shaped material for '" + ch + "' cannot be AIR/empty");
            shaped.setIngredient(ch, choice);
        }
        return shaped;
    }

    private @NotNull ShapelessRecipe buildShapeless(NamespacedKey key, ItemStack result) {
        List<RecipeChoice> choices = recipeListMaterials();
        if (choices.isEmpty())
            throw new IllegalArgumentException(recipeName() + ": shapeless recipe requires at least one ingredient");
        if (choices.size() > 9)
            throw new IllegalArgumentException(recipeName() + ": shapeless recipe can have at most 9 ingredients");

        ShapelessRecipe shapeless = new ShapelessRecipe(key, result);
        for (RecipeChoice c : choices) {
            if (c == null || isAirChoice(c))
                throw new IllegalArgumentException(recipeName() + ": shapeless ingredient cannot be AIR/empty");
            shapeless.addIngredient(c);
        }
        return shapeless;
    }

    private @NotNull Recipe buildSmithingVersionSafe(NamespacedKey key, ItemStack result) {
        RecipeChoice template = recipeTemplate();
        RecipeChoice base     = recipeSource();
        RecipeChoice add      = recipeAddition();

        if (isAirChoice(base) || isAirChoice(add))
            throw new IllegalArgumentException(recipeName() + ": smithing base/addition cannot be AIR/empty");

        // Try 1.20+ SmithingTransformRecipe(NamespacedKey, ItemStack, RecipeChoice, RecipeChoice, RecipeChoice)
        try {
            Class<?> clazz = Class.forName("org.bukkit.inventory.SmithingTransformRecipe");
            return (Recipe) clazz
                    .getConstructor(NamespacedKey.class, ItemStack.class, RecipeChoice.class, RecipeChoice.class, RecipeChoice.class)
                    .newInstance(key, result,
                            isAirChoice(template)
                                    ? new RecipeChoice.MaterialChoice(Collections.singletonList(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                                    : template,
                            base, add);
        } catch (ClassNotFoundException ignored) {
            // Fall back to legacy SmithingRecipe if server is older
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to construct SmithingTransformRecipe reflectively", e);
        }

        // Legacy org.bukkit.inventory.SmithingRecipe(NamespacedKey, ItemStack, RecipeChoice, RecipeChoice)
        try {
            Class<?> legacy = Class.forName("org.bukkit.inventory.SmithingRecipe");
            return (Recipe) legacy
                    .getConstructor(NamespacedKey.class, ItemStack.class, RecipeChoice.class, RecipeChoice.class)
                    .newInstance(key, result, base, add);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("SmithingRecipe class not available on this server.", e);
        }
    }
}