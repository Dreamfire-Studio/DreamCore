package com.dreamfirestudios.dreamCore.DreamfireMessagingChannel;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;


public abstract class PluginMessageLibrary implements PluginMessageListener{
    public abstract String getChannelName(JavaPlugin javaPlugin);
}