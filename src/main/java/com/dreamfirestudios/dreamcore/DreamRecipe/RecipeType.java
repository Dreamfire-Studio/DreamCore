package com.dreamfirestudios.dreamcore.DreamRecipe;

/// <summary>
/// Supported recipe families for <see cref="IDreamRecipe"/>.
/// </summary>
/// <remarks>
/// These map to Bukkit recipe implementations used in <c>IDreamRecipe.ReturnRecipe</c>.
/// </remarks>
public enum RecipeType {
    /// <summary>Blast furnace recipe.</summary>
    BlastingRecipe,
    /// <summary>Campfire cooking recipe.</summary>
    CampfireRecipe,
    /// <summary>Standard furnace recipe.</summary>
    FurnaceRecipe,
    /// <summary>Villager merchant trade.</summary>
    MerchantRecipe,
    /// <summary>Shaped crafting recipe.</summary>
    ShapedRecipe,
    /// <summary>Shapeless crafting recipe.</summary>
    ShapelessRecipe,
    /// <summary>Smithing table recipe.</summary>
    SmithingRecipe,
    /// <summary>Smoker recipe.</summary>
    SmokingRecipe,
    /// <summary>Stonecutter recipe.</summary>
    StonecuttingRecipe
}