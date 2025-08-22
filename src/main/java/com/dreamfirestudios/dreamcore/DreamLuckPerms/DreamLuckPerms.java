/* ============================================================================
 * [COPYRIGHT HEADER PLACEHOLDER]
 * Replace this block with the ChaosGalaxyTCG / Dreamfire V2 standard header.
 * Example:
 * Copyright (c) Dreamfire Studios.
 * Licensed under the <Your License>. See LICENSE in the project root.
 * ============================================================================
 */
package com.dreamfirestudios.dreamcore.DreamLuckPerms;

import com.dreamfirestudios.dreamcore.DreamCore;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * <summary>
 * Convenience utilities for interacting with LuckPerms within DreamCore.
 * </summary>
 *
 * <remarks>
 * All methods assume LuckPerms is present and initialized via {@link DreamCore#LuckPerms}.
 * Unless otherwise noted, these helpers operate on the main thread and follow LuckPerms'
 * recommended <i>modify &amp; save</i> pattern for user mutations.
 * <br/><br/>
 * <b>Caching notes</b>:
 * <ul>
 *   <li>{@link #getUser(Player)} uses the LuckPerms player adapter and will return a non-null {@link User} for online players.</li>
 *   <li>{@link #getUser(UUID)} may return {@code null} if the user is not loaded; use the UserManager to load if needed.</li>
 *   <li>Permission checks in {@link #hasPermission(User, Enum)} use cached data; ensure caches are up-to-date after mutations.</li>
 * </ul>
 * </remarks>
 *
 * <example>
 * <code>
 * // Example: add the "MY_PERMISSION" enum constant to a user by UUID
 * enum MyPerms { MY_PERMISSION }
 *
 * UUID uuid = player.getUniqueId();
 * DreamLuckPerms.addPermission(uuid, MyPerms.MY_PERMISSION);
 *
 * // Example: check a user's cached permission status
 * User user = DreamLuckPerms.getUser(player);
 * boolean allowed = DreamLuckPerms.hasPermission(user, MyPerms.MY_PERMISSION);
 * </code>
 * </example>
 */
public final class DreamLuckPerms {

    private DreamLuckPerms() { /* utility class */ }

    /**
     * <summary>
     * Simple result object for group queries, carrying success flag, parsed enum, and raw string.
     * </summary>
     *
     * <typeparam name="T">Enum type representing known groups.</typeparam>
     * <remarks>
     * When {@code success} is {@code false}, {@code enumValue} and {@code rawValue} will be {@code null}.
     * </remarks>
     */
    public record GroupResult<T>(boolean success, T enumValue, String rawValue) {}

    /**
     * <summary>
     * Checks if a player is in a given group by testing the conventional {@code group.&lt;name&gt;} permission.
     * </summary>
     *
     * <typeparam name="T">Enum type used to represent the group name.</typeparam>
     * <param name="player">The Bukkit player to test.</param>
     * <param name="group">The enum constant whose {@code toString()} name is used.</param>
     * <returns>True if the player has {@code group.&lt;enumName&gt;} permission, otherwise false.</returns>
     *
     * <remarks>
     * This uses Bukkit's {@link Player#hasPermission(String)} which may be backed by LuckPerms.
     * The tested node is derived as {@code "group." + group}. If your enum name casing differs from
     * actual group identifiers, prefer {@link #tryGetPlayerGroup(Player, Class)} or a direct LuckPerms query.
     * </remarks>
     *
     * <example>
     * <code>
     * enum Ranks { ADMIN, MOD, MEMBER }
     * boolean isAdmin = DreamLuckPerms.isPlayerInGroup(player, Ranks.ADMIN);
     * </code>
     * </example>
     */
    public static <T extends Enum<T>> boolean isPlayerInGroup(Player player, T group) {
        return player.hasPermission("group." + group);
    }

    /**
     * <summary>
     * Attempts to determine which enum-based group a player belongs to by scanning {@code group.&lt;name&gt;} permissions.
     * </summary>
     *
     * <typeparam name="T">Enum type to scan.</typeparam>
     * <param name="player">Player whose groups should be examined.</param>
     * <param name="enumClass">The enum class containing all candidate group constants.</param>
     * <returns>
     * A {@link GroupResult} whose {@code success} is true when any constant matches;
     * the matching enum value and raw group name (lowercase) are included.
     * </returns>
     *
     * <remarks>
     * This method converts each enum constant to a lowercase string and checks
     * {@code player.hasPermission("group." + lowercaseConstant)}. It returns the first match.
     * If multiple group permissions are present, the earliest enum constant wins.
     * </remarks>
     *
     * <example>
     * <code>
     * enum Rank { ADMIN, MODERATOR, MEMBER }
     * var result = DreamLuckPerms.tryGetPlayerGroup(player, Rank.class);
     * if (result.success()) {
     *     Rank rank = result.enumValue();
     *     // handle rank-specific logic
     * }
     * </code>
     * </example>
     */
    public static <T extends Enum<T>> GroupResult<T> tryGetPlayerGroup(Player player, Class<T> enumClass) {
        for (T constant : enumClass.getEnumConstants()) {
            String groupName = constant.name().toLowerCase();
            if (player.hasPermission("group." + groupName)) {
                return new GroupResult<>(true, constant, groupName);
            }
        }
        return new GroupResult<>(false, null, null);
    }

    /**
     * <summary>
     * Retrieves the LuckPerms {@link User} object for an online player.
     * </summary>
     *
     * <param name="player">The Bukkit player (must be online).</param>
     * <returns>The LuckPerms {@link User} instance for the player; never null for online players.</returns>
     *
     * <remarks>
     * Uses {@code LuckPerms.getPlayerAdapter(Player.class).getUser(player)} which should be non-null
     * for online players managed by LuckPerms.
     * </remarks>
     */
    public static User getUser(Player player) {
        return DreamCore.LuckPerms.getPlayerAdapter(Player.class).getUser(player);
    }

    /**
     * <summary>
     * Retrieves the LuckPerms {@link User} by UUID if loaded.
     * </summary>
     *
     * <param name="playerUUID">The target player's UUID.</param>
     * <returns>
     * The {@link User} instance, or {@code null} if not currently loaded by LuckPerms.
     * </returns>
     *
     * <remarks>
     * Use the LuckPerms {@code UserManager#loadUser(UUID)} (async) if you need to ensure the user is loaded.
     * </remarks>
     */
    public static User getUser(UUID playerUUID) {
        return DreamCore.LuckPerms.getUserManager().getUser(playerUUID);
    }

    /**
     * <summary>
     * Gets a LuckPerms {@link Group} by enum name.
     * </summary>
     *
     * <typeparam name="T">Enum type whose {@code toString()} maps to a LuckPerms group name.</typeparam>
     * <param name="groupName">Enum constant naming the target group.</param>
     * <returns>The {@link Group} or {@code null} if not found.</returns>
     *
     * <remarks>
     * This simply calls {@code getGroupManager().getGroup(enum.toString())}. If your
     * group identifiers do not match {@code toString()}, provide a mapping layer.
     * </remarks>
     */
    public static <T extends Enum<T>> Group getGroup(T groupName) {
        return DreamCore.LuckPerms.getGroupManager().getGroup(groupName.toString());
    }

    /**
     * <summary>
     * Adds a string-based permission (from enum) to the provided {@link User} and saves it.
     * </summary>
     *
     * <typeparam name="T">Enum type representing a permission node.</typeparam>
     * <param name="user">The LuckPerms user to modify.</param>
     * <param name="permission">Enum constant whose {@code toString()} is the node key.</param>
     *
     * <remarks>
     * This method performs an immediate {@code saveUser(user)} after adding the node.
     * </remarks>
     *
     * <example>
     * <code>
     * enum MyPerms { FEATURE_USE }
     * User user = DreamLuckPerms.getUser(player);
     * DreamLuckPerms.addPermission(user, MyPerms.FEATURE_USE);
     * </code>
     * </example>
     */
    public static <T extends Enum<T>> void addPermission(User user, T permission) {
        user.data().add(Node.builder(permission.toString()).build());
        DreamCore.LuckPerms.getUserManager().saveUser(user);
    }

    /**
     * <summary>
     * Adds a string-based permission (from enum) to a user referenced by UUID.
     * </summary>
     *
     * <typeparam name="T">Enum type representing a permission node.</typeparam>
     * <param name="playerUUID">Target user's UUID.</param>
     * <param name="permission">Enum constant whose {@code toString()} is the node key.</param>
     *
     * <remarks>
     * Uses {@code modifyUser} which handles load-modify-save safely, even if the user is not currently loaded.
     * </remarks>
     */
    public static <T extends Enum<T>> void addPermission(UUID playerUUID, T permission) {
        DreamCore.LuckPerms.getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(Node.builder(permission.toString()).build());
        });
    }

    /**
     * <summary>
     * Adds a concrete LuckPerms {@link Node} to a {@link User} and saves it.
     * </summary>
     *
     * <param name="user">The user to grant the node to.</param>
     * <param name="node">The LuckPerms node to add.</param>
     *
     * <remarks>
     * This calls {@code saveUser(user)} after mutation. For bulk operations, consider batching changes
     * before a single save to reduce I/O.
     * </remarks>
     */
    public static void addPermission(User user, Node node) {
        user.data().add(node);
        DreamCore.LuckPerms.getUserManager().saveUser(user);
    }

    /**
     * <summary>
     * Adds a concrete LuckPerms {@link Node} to a user by UUID via {@code modifyUser}.
     * </summary>
     *
     * <param name="playerUUID">Target user's UUID.</param>
     * <param name="node">The LuckPerms node to add.</param>
     *
     * <remarks>
     * {@code modifyUser} will load the user if necessary, apply the mutation, and persist the change.
     * </remarks>
     */
    public static void addPermission(UUID playerUUID, Node node) {
        DreamCore.LuckPerms.getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(node);
        });
    }

    /**
     * <summary>
     * Checks whether a {@link User} has a given enum-based permission according to cached data.
     * </summary>
     *
     * <typeparam name="T">Enum type representing the permission node.</typeparam>
     * <param name="user">The LuckPerms user to check.</param>
     * <param name="permission">Enum constant whose {@code toString()} is the node key.</param>
     * <returns>True if the cached permission data evaluates the node to allowed, else false.</returns>
     *
     * <remarks>
     * Uses {@code user.getCachedData().getPermissionData().checkPermission(...).asBoolean()}.
     * After modifying permissions, ensure caches are updated or re-queried for accurate results.
     * </remarks>
     */
    public static <T extends Enum<T>> boolean hasPermission(User user, T permission) {
        return user.getCachedData().getPermissionData().checkPermission(permission.toString()).asBoolean();
    }
}