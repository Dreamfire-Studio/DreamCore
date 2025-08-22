package com.dreamfirestudios.dreamcore.DreamRecipe;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Registers recipes and enforces permission-gated crafting.
 *
 * Call {@link #init(JavaPlugin)} once in onEnable().
 * Then call {@link #register(JavaPlugin, IDreamRecipe)} for each recipe.
 */
public final class DreamRecipeRegistry implements Listener {

    private static final Map<String, IDreamRecipe> keyed = new HashMap<>();
    private static final List<MerchantRecipe> merchantRecipes = new ArrayList<>();

    private DreamRecipeRegistry() {}

    public static void init(@NotNull JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new DreamRecipeRegistry(), plugin);
    }

    /**
     * Registers the recipe with the server where applicable.
     * For MerchantRecipe, it is stored for you to use when configuring villagers.
     *
     * @return the built Recipe (if not Merchant) or null (Merchant).
     */
    public static Recipe register(@NotNull JavaPlugin plugin, @NotNull IDreamRecipe def) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(def, "recipe");

        Recipe built = def.buildRecipe(plugin);

        if (def.recipeType() == RecipeType.MerchantRecipe) {
            // Build a Bukkit MerchantRecipe object using the params.
            MerchantRecipe mr = new MerchantRecipe(def.recipeResult(),
                    def.recipeUses(),
                    def.recipeMaxUsers(),
                    def.recipeExperienceReward(),
                    def.recipeVillagerExperience(),
                    def.recipePriceMultiplier(),
                    def.recipeDemand(),
                    def.recipeSpecialPrice());
            merchantRecipes.add(mr);
            // Not added to Bukkit crafting registry.
            keyed.put(def.nameSpace(plugin), def);
            return null;
        }

        if (built != null) {
            // Record permission enforcement by key (if recipe is Keyed)
            if (built instanceof Keyed k) {
                keyed.put(k.getKey().getKey(), def); // store just the key part (namespace:key -> key), or store full string if you prefer
            } else {
                // Non-keyed recipes are rare; permission enforcement will not trigger by key
            }
            Bukkit.addRecipe(built);
        }
        return built;
    }

    /** All stored MerchantRecipes you can attach to villagers at runtime. */
    public static List<MerchantRecipe> getMerchantRecipes() {
        return Collections.unmodifiableList(merchantRecipes);
    }

    /**
     * Permission gate: before a craft result appears, if this recipe is gated
     * and the viewing player lacks permission, remove the result.
     */
    @EventHandler
    public void onPrepare(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        if (!(event.getRecipe() instanceof Keyed keyedRecipe)) return;

        String k = keyedRecipe.getKey().getKey(); // only the key part
        IDreamRecipe def = keyed.get(k);
        if (def == null || !def.requiresPermission()) return;

        var view = event.getView();
        var who = view.getPlayer();
        if (who == null) return;

        if (!who.hasPermission(def.permissionNode())) {
            // Block this craft preview/result
            event.getInventory().setResult(new org.bukkit.inventory.ItemStack(Material.AIR));
        }
    }
}