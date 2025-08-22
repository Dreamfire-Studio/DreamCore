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

//TODO support permissions - PrepareItemCraftEvent
public interface IDreamRecipe {
    //Default values used in all recipes
    RecipeType recipeType();
    ItemStack recipeResult();
    default String recipeName(){ return getClass().getSimpleName(); }
    default String nameSpace(){ return String.format("%s_%s", com.dreamfirestudios.dreamcore.DreamCore.class.getSimpleName(), recipeName()).toLowerCase(); }

    //Values used in one or more recipe types
    default HashMap<Character, RecipeChoice> recipeMaterials(){ return new HashMap<>(); }
    default List<RecipeChoice> recipeListMaterials(){ return new ArrayList<>(); }
    default List<String> recipeShape(){ return new ArrayList<>(); }
    default RecipeChoice recipeSource(){ return new RecipeChoice.ExactChoice(new ItemStack(Material.AIR)); }
    default RecipeChoice recipeAddition(){ return new RecipeChoice.ExactChoice(new ItemStack(Material.AIR)); }
    default float recipeExperience(){ return 10f; }
    default int recipeCookingTime(){ return 20; }
    default int recipeUses(){ return 1; }
    default int recipeMaxUsers(){ return 2; }
    default boolean recipeExperienceReward(){ return false; }
    default float recipePriceMultiplier(){ return 1f; }
    default int recipeDemand(){ return 1; }
    default int recipeSpecialPrice(){ return 1; }

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