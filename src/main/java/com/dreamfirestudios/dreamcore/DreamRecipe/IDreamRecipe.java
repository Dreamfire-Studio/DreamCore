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
package com.dreamfirestudios.dreamcore.DreamRecipe;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/// <summary>
/// Contract for defining DreamCore recipes with sensible defaults and a factory to build Bukkit recipe instances.
/// </summary>
/// <remarks>
/// Supports all standard recipe families (shaped, shapeless, furnace-like, smithing, merchant, etc.).
/// Implementers fill in only the values they need for the chosen <see cref="recipeType()"/>.
/// </remarks>
/// <example>
/// <code>
/// public final class MySwordRecipe implements IDreamRecipe {
///     public RecipeType recipeType() { return RecipeType.ShapedRecipe; }
///     public ItemStack recipeResult() { return new ItemStack(Material.DIAMOND_SWORD); }
///     public List&lt;String&gt; recipeShape() { return List.of(" D ", " D ", " S "); }
///     public HashMap&lt;Character, RecipeChoice&gt; recipeMaterials() {
///         var m = new HashMap&lt;Character, RecipeChoice&gt;();
///         m.put('D', new RecipeChoice.MaterialChoice(Material.DIAMOND));
///         m.put('S', new RecipeChoice.MaterialChoice(Material.STICK));
///         return m;
///     }
/// }
/// </code>
/// </example>
public interface IDreamRecipe {
    // Default values used in all recipes

    /// <summary>Type/family of this recipe.</summary>
    RecipeType recipeType();

    /// <summary>Resulting item produced by the recipe.</summary>
    ItemStack recipeResult();

    /// <summary>
    /// Optional display/registry name; defaults to the class simple name.
    /// </summary>
    /// <returns>Recipe name.</returns>
    default String recipeName(){ return getClass().getSimpleName(); }

    /// <summary>
    /// Namespaced key local part (lowercase) used for registration.
    /// </summary>
    /// <returns>Namespace string suitable for <see cref="NamespacedKey"/>.</returns>
    default String nameSpace(){ return String.format("%s_%s", com.dreamfirestudios.dreamcore.DreamCore.class.getSimpleName(), recipeName()).toLowerCase(); }

    // Values used in one or more recipe types

    /// <summary>
    /// Character-to-ingredient mapping used by shaped recipes.
    /// </summary>
    /// <returns>Map of character symbols to <see cref="RecipeChoice"/>.</returns>
    default HashMap<Character, RecipeChoice> recipeMaterials(){ return new HashMap<>(); }

    /// <summary>
    /// Ingredient list used by shapeless recipes.
    /// </summary>
    /// <returns>List of <see cref="RecipeChoice"/> used as inputs.</returns>
    default List<RecipeChoice> recipeListMaterials(){ return new ArrayList<>(); }

    /// <summary>
    /// Three-row shape used by shaped recipes, e.g., <c>["ABC","DEF","GHI"]</c>.
    /// </summary>
    /// <returns>Exactly three strings (Shape rows).</returns>
    default List<String> recipeShape(){ return new ArrayList<>(); }

    /// <summary>Primary source/base item (furnace/smithing/cutting/etc.).</summary>
    default RecipeChoice recipeSource(){ return new RecipeChoice.ExactChoice(new ItemStack(Material.AIR)); }

    /// <summary>Additional item for smithing recipes.</summary>
    default RecipeChoice recipeAddition(){ return new RecipeChoice.ExactChoice(new ItemStack(Material.AIR)); }

    /// <summary>Experience yielded by furnace-like recipes.</summary>
    default float recipeExperience(){ return 10f; }

    /// <summary>Cooking time (ticks) for furnace-like recipes.</summary>
    default int recipeCookingTime(){ return 20; }

    /// <summary>Allowed uses for merchant recipes.</summary>
    default int recipeUses(){ return 1; }

    /// <summary>Maximum users for merchant recipes (villager demand system).</summary>
    default int recipeMaxUsers(){ return 2; }

    /// <summary>Whether a merchant trade rewards experience.</summary>
    default boolean recipeExperienceReward(){ return false; }

    /// <summary>Price multiplier for merchant trades.</summary>
    default float recipePriceMultiplier(){ return 1f; }

    /// <summary>Demand parameter for merchant trade economics.</summary>
    default int recipeDemand(){ return 1; }

    /// <summary>Special price value for merchant trades.</summary>
    default int recipeSpecialPrice(){ return 1; }

    /// <summary>
    /// Builds the appropriate Bukkit <see cref="Recipe"/> instance from the configured properties.
    /// </summary>
    /// <param name="javaPlugin">Owning plugin used to form the <see cref="NamespacedKey"/>.</param>
    /// <returns>Concrete <see cref="Recipe"/> or <c>null</c> if type was unrecognized.</returns>
    /// <remarks>
    /// <para>Note: Some Bukkit constructors used here are marked deprecated across versions; they are still commonly used and
    /// are intentionally suppressed.</para>
    /// </remarks>
    /// <example>
    /// <code>
    /// Recipe r = myRecipe.ReturnRecipe(plugin);
    /// if (r != null) Bukkit.addRecipe(r);
    /// </code>
    /// </example>
    @SuppressWarnings("deprecation")
    default Recipe ReturnRecipe(JavaPlugin javaPlugin){

        if(recipeType() == RecipeType.BlastingRecipe){
            return new BlastingRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult(), recipeSource(), recipeExperience(), recipeCookingTime());
        }else if(recipeType() == RecipeType.CampfireRecipe){
            return new CampfireRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult(), recipeSource(), recipeExperience(), recipeCookingTime());
        }else if(recipeType() == RecipeType.FurnaceRecipe){
            return new FurnaceRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult(), recipeSource(), recipeExperience(), recipeCookingTime());
        }else if(recipeType() == RecipeType.MerchantRecipe){
            return new MerchantRecipe(recipeResult(), recipeUses(), recipeMaxUsers(), recipeExperienceReward(), (int) recipeExperience(), recipePriceMultiplier(), recipeDemand(), recipeSpecialPrice());
        }else if(recipeType() == RecipeType.ShapedRecipe){
            var shapedRecipe = new ShapedRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult());
            shapedRecipe.shape(recipeShape().get(0), recipeShape().get(1), recipeShape().get(2));
            for(var c : recipeMaterials().keySet()) shapedRecipe.setIngredient(c, recipeMaterials().get(c));
            return shapedRecipe;
        }else if(recipeType() == RecipeType.ShapelessRecipe){
            var shapelessRecipe = new ShapelessRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult());
            for(var recipeChoice : recipeListMaterials()) shapelessRecipe.addIngredient(recipeChoice);
            return shapelessRecipe;
        }else if(recipeType() == RecipeType.SmithingRecipe){
            return new SmithingRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult(), recipeSource(), recipeAddition());
        }else if(recipeType() == RecipeType.SmokingRecipe){
            return new SmokingRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult(), recipeSource(), recipeExperience(), recipeCookingTime());
        }else if(recipeType() == RecipeType.StonecuttingRecipe){
            return new StonecuttingRecipe(new NamespacedKey(javaPlugin, nameSpace()), recipeResult(), recipeSource());
        }
        return null;
    }
}