package com.dreamfirestudios.dreamCore.DreamfireLuckPerms;

public enum DreamfirePermissions {
    translateColorCodes("PulseCore.translateColorCodes"),
    translateHexCodes("PulseCore.translateHexCodes");

    public final String perm;

    DreamfirePermissions(String perm){
        this.perm = perm;
    }
}
