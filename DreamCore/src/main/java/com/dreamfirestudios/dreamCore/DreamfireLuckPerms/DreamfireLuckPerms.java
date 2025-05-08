package com.dreamfirestudios.dreamCore.DreamfireLuckPerms;

import com.dreamfirestudios.dreamCore.DreamCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DreamfireLuckPerms {
    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) return group;
        }
        return null;
    }

    public static User getUser(Player player){
        return DreamCore.GetLuckPerms().getPlayerAdapter(Player.class).getUser(player);
    }

    public static User getUser(UUID playerUUID){
        return DreamCore.GetLuckPerms().getUserManager().getUser(playerUUID);
    }

    public static CompletableFuture<Void> userFutureAsync(UUID playerUUID, Consumer<User> action){
        var userManager = DreamCore.GetLuckPerms().getUserManager();
        var userFuture = userManager.loadUser(playerUUID);
        return userFuture.thenAcceptAsync(action);
    }

    public static User userFutureBlock(UUID playerUUID){
        var userManager = DreamCore.GetLuckPerms().getUserManager();
        var userFuture = userManager.loadUser(playerUUID);
        return userFuture.join();
    }

    public static CompletableFuture<Boolean> isInGroup(UUID who, String groupName) {
        var userManager = DreamCore.GetLuckPerms().getUserManager();
        return userManager.loadUser(who).thenApplyAsync(user -> {
            Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
            return inheritedGroups.stream().anyMatch(g -> g.getName().equals(groupName));
        });
    }

    public static Group getGroup(String groupName){
        return DreamCore.GetLuckPerms().getGroupManager().getGroup(groupName);
    }

    public static void addPermission(User user, String permission){
        user.data().add(Node.builder(permission).build());
        DreamCore.GetLuckPerms().getUserManager().saveUser(user);
    }

    public static void addPermission(UUID playerUUID, String permission){
        DreamCore.GetLuckPerms().getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(Node.builder(permission).build());
        });
    }

    public static void addPermission(User user, DreamfirePermissions dreamfirePermissions){
        user.data().add(Node.builder(dreamfirePermissions.perm).build());
        DreamCore.GetLuckPerms().getUserManager().saveUser(user);
    }

    public static void addPermission(UUID playerUUID, DreamfirePermissions dreamfirePermissions){
        DreamCore.GetLuckPerms().getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(Node.builder(dreamfirePermissions.perm).build());
        });
    }

    public static void addPermission(User user, Node node){
        user.data().add(node);
        DreamCore.GetLuckPerms().getUserManager().saveUser(user);
    }

    public static void addPermission(UUID playerUUID, Node node){
        DreamCore.GetLuckPerms().getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(node);
        });
    }

    public static boolean hasPermission(User user, String permission){
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public static boolean hasPermission(User user, DreamfirePermissions dreamfirePermissions){
        return user.getCachedData().getPermissionData().checkPermission(dreamfirePermissions.perm).asBoolean();
    }
}
