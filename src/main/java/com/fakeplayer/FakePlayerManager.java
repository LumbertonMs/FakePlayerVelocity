package com.fakeplayer;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all fake players on the proxy
 */
public class FakePlayerManager {
    
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Map<UUID, FakePlayer> fakePlayersByUUID;
    private final Map<String, FakePlayer> fakePlayersByName;
    
    public FakePlayerManager(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.fakePlayersByUUID = new ConcurrentHashMap<>();
        this.fakePlayersByName = new ConcurrentHashMap<>();
    }
    
    /**
     * Creates a new fake player with a random UUID
     */
    public FakePlayer createFakePlayer(String username) {
        // Check if player with this name already exists
        if (fakePlayersByName.containsKey(username.toLowerCase())) {
            logger.warn("Fake player '{}' already exists", username);
            return null;
        }
        
        UUID uuid = UUID.randomUUID();
        FakePlayer fakePlayer = new FakePlayer(uuid, username);
        
        fakePlayersByUUID.put(uuid, fakePlayer);
        fakePlayersByName.put(username.toLowerCase(), fakePlayer);
        
        logger.info("Created fake player '{}' with UUID {}", username, uuid);
        return fakePlayer;
    }
    
    /**
     * Removes a fake player by name
     */
    public boolean removeFakePlayer(String username) {
        FakePlayer player = fakePlayersByName.remove(username.toLowerCase());
        if (player != null) {
            fakePlayersByUUID.remove(player.getUUID());
            logger.info("Removed fake player '{}'", username);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a fake player by UUID
     */
    public boolean removeFakePlayer(UUID uuid) {
        FakePlayer player = fakePlayersByUUID.remove(uuid);
        if (player != null) {
            fakePlayersByName.remove(player.getUsername().toLowerCase());
            logger.info("Removed fake player '{}'", player.getUsername());
            return true;
        }
        return false;
    }
    
    /**
     * Connects a fake player to a backend server
     */
    public boolean connectToServer(String playerName, String serverName) {
        FakePlayer player = fakePlayersByName.get(playerName.toLowerCase());
        if (player == null) {
            logger.warn("Fake player '{}' not found", playerName);
            return false;
        }
        
        Optional<RegisteredServer> optionalServer = proxyServer.getServer(serverName);
        if (optionalServer.isEmpty()) {
            logger.warn("Server '{}' not found", serverName);
            return false;
        }
        
        RegisteredServer serverInfo = optionalServer.get();
        player.setConnectedServer(serverInfo);
        logger.info("Connected fake player '{}' to server '{}'", playerName, serverName);
        return true;
    }
    
    /**
     * Disconnects a fake player from their current server
     */
    public boolean disconnectFromServer(String playerName) {
        FakePlayer player = fakePlayersByName.get(playerName.toLowerCase());
        if (player == null) {
            return false;
        }
        
        if (!player.isConnected()) {
            return false;
        }
        
        String previousServer = player.getConnectedServer().getServerInfo().getName();
        player.setConnectedServer(null);
        logger.info("Disconnected fake player '{}' from server '{}'", playerName, previousServer);
        return true;
    }
    
    /**
     * Gets a fake player by name
     */
    public FakePlayer getFakePlayer(String username) {
        return fakePlayersByName.get(username.toLowerCase());
    }
    
    /**
     * Gets a fake player by UUID
     */
    public FakePlayer getFakePlayer(UUID uuid) {
        return fakePlayersByUUID.get(uuid);
    }
    
    /**
     * Checks if a player name belongs to a fake player
     */
    public boolean isFakePlayer(String username) {
        return fakePlayersByName.containsKey(username.toLowerCase());
    }
    
    /**
     * Checks if a UUID belongs to a fake player
     */
    public boolean isFakePlayer(UUID uuid) {
        return fakePlayersByUUID.containsKey(uuid);
    }
    
    /**
     * Gets all fake players
     */
    public Collection<FakePlayer> getAllFakePlayers() {
        return new ArrayList<>(fakePlayersByUUID.values());
    }
    
    /**
     * Gets all fake players connected to a specific server
     */
    public List<FakePlayer> getFakePlayersOnServer(String serverName) {
        List<FakePlayer> result = new ArrayList<>();
        for (FakePlayer player : fakePlayersByUUID.values()) {
            if (player.isConnected() && player.getConnectedServer().getServerInfo().getName().equalsIgnoreCase(serverName)) {
                result.add(player);
            }
        }
        return result;
    }
    
    /**
     * Gets the total count of fake players
     */
    public int getTotalFakePlayers() {
        return fakePlayersByUUID.size();
    }
    
    /**
     * Gets the count of fake players connected to servers
     */
    public int getConnectedFakePlayers() {
        return (int) fakePlayersByUUID.values().stream()
                .filter(FakePlayer::isConnected)
                .count();
    }
}
