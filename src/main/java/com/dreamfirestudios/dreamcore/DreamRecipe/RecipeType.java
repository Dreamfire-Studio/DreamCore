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