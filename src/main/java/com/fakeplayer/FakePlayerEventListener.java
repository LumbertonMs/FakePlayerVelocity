package com.fakeplayer;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import org.slf4j.Logger;

/**
 * Listens to player events to manage fake player integration
 */
public class FakePlayerEventListener {
    
    private final FakePlayerManager fakePlayerManager;
    private final Logger logger;
    
    public FakePlayerEventListener(FakePlayerManager fakePlayerManager, Logger logger) {
        this.fakePlayerManager = fakePlayerManager;
        this.logger = logger;
    }
    
    /**
     * Handles initial server connection for real players
     * Logs information about connections for debugging
     */
    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        String playerName = event.getPlayer().getUsername();
        
        // Log real player connections
        logger.debug("Real player '{}' choosing initial server", playerName);
    }
    
    /**
     * Handles post-connection events for real players
     */
    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        String playerName = event.getPlayer().getUsername();
        String serverName = event.getPlayer().getCurrentServer()
                .map(conn -> conn.getServerInfo().getName())
                .orElse("unknown");
        
        // Log real player server connections
        logger.debug("Real player '{}' connected to server '{}'", playerName, serverName);
    }
}
