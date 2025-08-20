package com.dreamfirestudios.dreamCore.DreamLuckPerms;

import com.dreamfirestudios.dreamCore.DreamCore;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DreamLuckPerms {
    public record GroupResult<T>(boolean success, T enumValue, String rawValue) {}

    public static <T extends Enum<T>> boolean isPlayerInGroup(Player player, T group) {
        return player.hasPermission("group." + group);
    }

    public static <T extends Enum<T>> GroupResult<T> tryGetPlayerGroup(Player player, Class<T> enumClass) {
        for (T constant : enumClass.getEnumConstants()) {
            String groupName = constant.name().toLowerCase();
            if (player.hasPermission("group." + groupName)) {
                return new GroupResult<>(true, constant, groupName);
            }
        }
        return new GroupResult<>(false, null, null);
    }

    public static User getUser(Player player){
        return DreamCore.LuckPerms.getPlayerAdapter(Player.class).getUser(player);
    }

    public static User getUser(UUID playerUUID){
        return DreamCore.LuckPerms.getUserManager().getUser(playerUUID);
    }

    public static <T extends Enum<T>> Group getGroup(T groupName){
        return DreamCore.LuckPerms.getGroupManager().getGroup(groupName.toString());
    }

    public static <T extends Enum<T>> void addPermission(User user, T permission){
        user.data().add(Node.builder(permission.toString()).build());
        DreamCore.LuckPerms.getUserManager().saveUser(user);
    }

    public static <T extends Enum<T>> void addPermission(UUID playerUUID, T permission){
        DreamCore.LuckPerms.getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(Node.builder(permission.toString()).build());
        });
    }

    public static void addPermission(User user, Node node){
        user.data().add(node);
        DreamCore.LuckPerms.getUserManager().saveUser(user);
    }

    public static void addPermission(UUID playerUUID, Node node){
        DreamCore.LuckPerms.getUserManager().modifyUser(playerUUID, user -> {
            user.data().add(node);
        });
    }

    public static <T extends Enum<T>> boolean hasPermission(User user, T permission){
        return user.getCachedData().getPermissionData().checkPermission(permission.toString()).asBoolean();
    }
}
