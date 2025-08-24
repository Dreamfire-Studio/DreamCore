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
 *   <li>Permission checks in {@link #hasPermission(User, String)} use cached data; ensure caches are up-to-date after mutations.</li>
 * </ul>
 * </remarks>
 *
 * <example>
 * <code>
 * // Example: add a permission by node string
 * UUID uuid = player.getUniqueId();
 * DreamLuckPerms.addPermission(uuid, "myplugin.feature.use");
 *
 * // Example: check a user's cached permission status
 * User user = DreamLuckPerms.getUser(player);
 * boolean allowed = DreamLuckPerms.hasPermission(user, "myplugin.feature.use");
 * </code>
 * </example>
 */
public final class DreamLuckPerms {

    private DreamLuckPerms() { /* utility class */ }

    /**
     * <summary>
     * Result object for group queries, carrying success flag and the matched group name.
     * </summary>
     *
     * <remarks>
     * When {@code success} is {@code false}, {@code groupName} will be {@code null}.
     * </remarks>
     */
    public record GroupResult(boolean success, String groupName) {}

    /**
     * <summary>
     * Checks if a player is in a given group by testing the conventional {@code group.&lt;name&gt;} permission.
     * </summary>
     *
     * <param name="player">The Bukkit player to test.</param>
     * <param name="groupName">The group name (used verbatim in the node: {@code group.&lt;groupName&gt;}).</param>
     * <returns>True if the player has {@code group.&lt;groupName&gt;} permission, otherwise false.</returns>
     *
     * <remarks>
     * This uses Bukkit's {@link Player#hasPermission(String)} which may be backed by LuckPerms.
     * The tested node is derived as {@code "group." + groupName} with no case normalization.
     * </remarks>
     *
     * <example>
     * <code>
     * boolean isAdmin = DreamLuckPerms.isPlayerInGroup(player, "admin");
     * </code>
     * </example>
     */
    public static boolean isPlayerInGroup(Player player, String groupName) {
        return player.hasPermission("group." + groupName);
    }

    /**
     * <summary>
     * Attempts to determine which group (from provided candidates) a player belongs to
     * by scanning {@code group.&lt;name&gt;} permissions.
     * </summary>
     *
     * <param name="player">Player whose groups should be examined.</param>
     * <param name="candidateGroupNames">Candidate group names to test, in priority order.</param>
     * <returns>
     * A {@link GroupResult} whose {@code success} is true when any candidate matches;
     * the matching group name is included.
     * </returns>
     *
     * <remarks>
     * Each candidate is used verbatim as {@code "group." + candidate}. The first match is returned.
     * </remarks>
     *
     * <example>
     * <code>
     * var result = DreamLuckPerms.tryGetPlayerGroup(player, "admin", "moderator", "member");
     * if (result.success()) {
     *     String group = result.groupName();
     *     // handle group-specific logic
     * }
     * </code>
     * </example>
     */
    public static GroupResult tryGetPlayerGroup(Player player, String... candidateGroupNames) {
        if (candidateGroupNames != null) {
            for (String name : candidateGroupNames) {
                if (name != null && player.hasPermission("group." + name)) {
                    return new GroupResult(true, name);
                }
            }
        }
        return new GroupResult(false, null);
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
     * Gets a LuckPerms {@link Group} by name.
     * </summary>
     *
     * <param name="groupName">The LuckPerms group name.</param>
     * <returns>The {@link Group} or {@code null} if not found.</returns>
     */
    public static Group getGroup(String groupName) {
        return DreamCore.LuckPerms.getGroupManager().getGroup(groupName);
    }

    /**
     * <summary>
     * Adds a string-based permission node to the provided {@link User} and saves it.
     * </summary>
     *
     * <param name="user">The LuckPerms user to modify.</param>
     * <param name="permissionNode">Permission node key (e.g., {@code "myplugin.feature.use"}).</param>
     *
     * <remarks>
     * Performs an immediate {@code saveUser(user)} after adding the node.
     * </remarks>
     */
    public static void addPermission(User user, String permissionNode) {
        user.data().add(Node.builder(permissionNode).build());
        DreamCore.LuckPerms.getUserManager().saveUser(user);
    }

    /**
     * <summary>
     * Adds a string-based permission node to a user referenced by UUID.
     * </summary>
     *
     * <param name="playerUUID">Target user's UUID.</param>
     * <param name="permissionNode">Permission node key.</param>
     *
     * <remarks>
     * Uses {@code modifyUser} which handles load-modify-save safely, even if the user is not currently loaded.
     * </remarks>
     */
    public static void addPermission(UUID playerUUID, String permissionNode) {
        DreamCore.LuckPerms.getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(Node.builder(permissionNode).build());
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
     * Checks whether a {@link User} has a given string-based permission according to cached data.
     * </summary>
     *
     * <param name="user">The LuckPerms user to check.</param>
     * <param name="permissionNode">Permission node key.</param>
     * <returns>True if the cached permission data evaluates the node to allowed, else false.</returns>
     *
     * <remarks>
     * Uses {@code user.getCachedData().getPermissionData().checkPermission(...).asBoolean()}.
     * After modifying permissions, ensure caches are updated or re-queried for accurate results.
     * </remarks>
     */
    public static boolean hasPermission(User user, String permissionNode) {
        return user.getCachedData().getPermissionData().checkPermission(permissionNode).asBoolean();
    }

    /**
     * <summary>
     * Alias of {@link #hasPermission(User, String)} for backwards compatibility.
     * </summary>
     */
    public static boolean hasStringPermission(User user, String permission) {
        return hasPermission(user, permission);
    }
}