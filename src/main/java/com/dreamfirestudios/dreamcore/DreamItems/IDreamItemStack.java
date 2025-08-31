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
package com.dreamfirestudios.dreamcore.DreamItems;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import java.util.*;

/// <summary>
/// Definition for a custom item. Override only what you need.
/// </summary>
/// <remarks>
/// Paper‑friendly: uses Adventure <see cref="Component"/> for names/lore, PDC for custom data,
/// and standard Bukkit attribute/enchantment APIs. If <see cref="id()"/> returns a value,
/// it will be written to the Item's PDC by <see cref="DreamItemStacks.build(Plugin, IDreamItemStack)"/>.
/// This enables stable, reliable matching via <see cref="DreamItemStacks.isSame(Plugin, org.bukkit.inventory.ItemStack, org.bukkit.inventory.ItemStack)"/>.
/// </remarks>
/// <example>
/// <code>
/// public final class HealingPotion implements IDreamItemStack {
///     public Optional&lt;String&gt; id() { return Optional.of("healing_potion_t1"); }
///     public Material type() { return Material.POTION; }
///     public Component displayName() { return Component.text("§dLesser Healing"); }
///     public int amount() { return 1; }
///     public Map&lt;Enchantment, Integer&gt; enchantments() { return Map.of(Enchantment.MENDING, 1); }
///     public void writePdc(Plugin plugin, PersistentDataContainer pdc) {
///         pdc.set(new NamespacedKey(plugin, "heal"), PersistentDataType.INTEGER, 6);
///     }
/// }
/// </code>
/// </example>
public interface IDreamItemStack {
    String id();

    /// <summary>
    /// Display name (Adventure). Return <c>null</c> to keep the vanilla name.
    /// </summary>
    /// <returns>Display name component or <c>null</c>.</returns>
    default Component displayName() { return Component.text(getClass().getSimpleName()); }

    /// <summary>
    /// Base material type for the item.
    /// </summary>
    /// <returns><see cref="Material"/> type.</returns>
    default Material type() { return Material.DIAMOND_PICKAXE; }

    /// <summary>
    /// Initial stack amount (clamped to ≥ 1).
    /// </summary>
    /// <returns>Stack size to construct with.</returns>
    default int amount() { return 1; }

    /// <summary>
    /// Optional custom model data.
    /// </summary>
    /// <returns>Optional integer value; empty if none.</returns>
    default OptionalInt customModelData() { return OptionalInt.empty(); }

    /// <summary>
    /// Whether the item is unbreakable.
    /// </summary>
    /// <returns><c>true</c> to mark as unbreakable.</returns>
    default boolean unbreakable() { return false; }

    /* ------------------ Visuals & extras ------------------ */

    /// <summary>
    /// Lore lines (Adventure). Empty list means no lore.
    /// </summary>
    /// <returns>Immutable list of components.</returns>
    default List<Component> lore() { return List.of(); }

    /// <summary>
    /// Item flags to apply (e.g., hide attributes).
    /// </summary>
    /// <returns>Immutable set of flags.</returns>
    default Set<ItemFlag> flags() { return Set.of(); }

    /// <summary>
    /// Enchantments to apply after meta is set.
    /// </summary>
    /// <returns>Map of <see cref="Enchantment"/> to levels (&gt; 0).</returns>
    default Map<Enchantment, Integer> enchantments() { return Map.of(); }

    /// <summary>
    /// Attribute modifiers. One attribute may have multiple modifiers.
    /// </summary>
    /// <returns>
    /// Map from <see cref="Attribute"/> to a collection of <see cref="AttributeModifier"/>.
    /// </returns>
    /// <remarks>
    /// This direction (attribute -&gt; modifiers) matches Bukkit API expectations.
    /// </remarks>
    default Map<Attribute, Collection<AttributeModifier>> attributeModifiers() { return Map.of(); }

    /// <summary>
    /// Hook to write any custom Persistent Data Container values.
    /// </summary>
    /// <param name="plugin">Owning plugin (for namespaced keys).</param>
    /// <param name="pdc">Mutable PDC for the item meta.</param>
    /// <remarks>
    /// Called after meta is prepared and before it is applied to the stack.
    /// Implementers can set additional data (e.g., stats or flags).
    /// </remarks>
    /// <example>
    /// <code>
    /// public void writePdc(Plugin plugin, PersistentDataContainer pdc) {
    ///     pdc.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.STRING, "epic");
    /// }
    /// </code>
    /// </example>
    default void writePdc(Plugin plugin, PersistentDataContainer pdc) { /* no-op */ }
}