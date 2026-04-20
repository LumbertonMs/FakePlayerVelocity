package com.fakeplayer;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(
        id = "fakeplayer",
        name = "FakePlayerVelocity",
        version = "1.0.0",
        authors = {"Development Team"}
)
public class FakePlayerPlugin {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private FakePlayerManager fakePlayerManager;

    @Inject
    public FakePlayerPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("FakePlayerVelocity plugin initializing...");
        
        this.fakePlayerManager = new FakePlayerManager(proxyServer, logger);
        
        // Register command
        proxyServer.getCommandManager().register("fakeplay", 
                new FakePlayerCommand(fakePlayerManager, proxyServer, logger));
        
        // Register event listener
        proxyServer.getEventManager().register(this, new FakePlayerEventListener(fakePlayerManager, logger));
        
        logger.info("FakePlayerVelocity plugin initialized successfully!");
    }

    public FakePlayerManager getFakePlayerManager() {
        return fakePlayerManager;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
