package com.fakeplayer;

import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Represents a fake player on the proxy
 */
public class FakePlayer {
    
    private final java.util.UUID uuid;
    private final String username;
    private RegisteredServer connectedServer;
    private long createdAt;
    
    public FakePlayer(java.util.UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.createdAt = System.currentTimeMillis();
    }
    
    public java.util.UUID getUUID() {
        return uuid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public RegisteredServer getConnectedServer() {
        return connectedServer;
    }
    
    public void setConnectedServer(RegisteredServer serverInfo) {
        this.connectedServer = serverInfo;
    }
    
    public boolean isConnected() {
        return connectedServer != null;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public int getOnlineTime() {
        return (int) ((System.currentTimeMillis() - createdAt) / 1000);
    }
    
    @Override
    public String toString() {
        return String.format("FakePlayer{name=%s, uuid=%s, server=%s}", 
                username, uuid, connectedServer != null ? connectedServer.getServerInfo().getName() : "none");
    }
}
