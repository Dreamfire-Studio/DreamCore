package com.dreamfirestudios.dreamCore.DreamfireProtocolLib.Listeners.Client;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireJava.PulseAutoRegister;
import org.bukkit.Bukkit;

@PulseAutoRegister
public class PlayClientWorldEvent extends PacketAdapter {
    public PlayClientWorldEvent() {
        super(
                new AdapterParameteters()
                .plugin(DreamCore.GetDreamfireCore())
                .listenerPriority(ListenerPriority.NORMAL)
                .types(PacketType.Play.Server.WORLD_EVENT)
        );
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        var packet = event.getPacket();
        int eventID = packet.getIntegers().read(0);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        var packet = event.getPacket();
        int eventID = packet.getIntegers().read(0);
    }
}