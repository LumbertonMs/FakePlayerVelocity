package com.fakeplayer;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Command handler for fake player management
 */
public class FakePlayerCommand implements SimpleCommand {
    
    private final FakePlayerManager fakePlayerManager;
    private final ProxyServer proxyServer;
    private final Logger logger;
    
    public FakePlayerCommand(FakePlayerManager fakePlayerManager, ProxyServer proxyServer, Logger logger) {
        this.fakePlayerManager = fakePlayerManager;
        this.proxyServer = proxyServer;
        this.logger = logger;
    }
    
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        
        if (args.length == 0) {
            sendHelp(source);
            return;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "create":
                handleCreate(source, args);
                break;
            case "delete":
            case "remove":
                handleDelete(source, args);
                break;
            case "connect":
                handleConnect(source, args);
                break;
            case "disconnect":
                handleDisconnect(source, args);
                break;
            case "list":
                handleList(source);
                break;
            case "info":
                handleInfo(source, args);
                break;
            case "help":
                sendHelp(source);
                break;
            default:
                source.sendMessage(Component.text("Unknown command. Use /fakeplay help", NamedTextColor.RED));
        }
    }
    
    private void handleCreate(CommandSource source, String[] args) {
        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /fakeplay create <name>", NamedTextColor.YELLOW));
            return;
        }
        
        String playerName = args[1];
        
        // Validate player name - must be 3-16 characters, alphanumeric and underscore
        if (!isValidUsername(playerName)) {
            source.sendMessage(Component.text("Invalid player name: " + playerName + " (must be 3-16 alphanumeric characters)", NamedTextColor.RED));
            return;
        }
        
        FakePlayer fakePlayer = fakePlayerManager.createFakePlayer(playerName);
        if (fakePlayer != null) {
            source.sendMessage(Component.text("Created fake player: ").color(NamedTextColor.GREEN)
                    .append(Component.text(playerName)));
            logger.info("{} created fake player {}", source, playerName);
        } else {
            source.sendMessage(Component.text("Could not create fake player (may already exist)", NamedTextColor.RED));
        }
    }
    
    private boolean isValidUsername(String name) {
        if (name.length() < 3 || name.length() > 16) {
            return false;
        }
        return name.matches("^[a-zA-Z0-9_]+$");
    }
    
    private void handleDelete(CommandSource source, String[] args) {
        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /fakeplay delete <name>", NamedTextColor.YELLOW));
            return;
        }
        
        String playerName = args[1];
        
        if (fakePlayerManager.removeFakePlayer(playerName)) {
            source.sendMessage(Component.text("Deleted fake player: ").color(NamedTextColor.GREEN)
                    .append(Component.text(playerName)));
            logger.info("{} deleted fake player {}", source, playerName);
        } else {
            source.sendMessage(Component.text("Fake player not found: " + playerName, NamedTextColor.RED));
        }
    }
    
    private void handleConnect(CommandSource source, String[] args) {
        if (args.length < 3) {
            source.sendMessage(Component.text("Usage: /fakeplay connect <player> <server>", NamedTextColor.YELLOW));
            return;
        }
        
        String playerName = args[1];
        String serverName = args[2];
        
        FakePlayer fakePlayer = fakePlayerManager.getFakePlayer(playerName);
        if (fakePlayer == null) {
            source.sendMessage(Component.text("Fake player not found: " + playerName, NamedTextColor.RED));
            return;
        }
        
        if (proxyServer.getServer(serverName).isEmpty()) {
            source.sendMessage(Component.text("Server not found: " + serverName, NamedTextColor.RED));
            return;
        }
        
        if (fakePlayerManager.connectToServer(playerName, serverName)) {
            source.sendMessage(Component.text("Connected ").color(NamedTextColor.GREEN)
                    .append(Component.text(playerName))
                    .append(Component.text(" to server "))
                    .append(Component.text(serverName)));
            logger.info("{} connected fake player {} to server {}", source, playerName, serverName);
        } else {
            source.sendMessage(Component.text("Could not connect player to server", NamedTextColor.RED));
        }
    }
    
    private void handleDisconnect(CommandSource source, String[] args) {
        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /fakeplay disconnect <player>", NamedTextColor.YELLOW));
            return;
        }
        
        String playerName = args[1];
        
        FakePlayer fakePlayer = fakePlayerManager.getFakePlayer(playerName);
        if (fakePlayer == null) {
            source.sendMessage(Component.text("Fake player not found: " + playerName, NamedTextColor.RED));
            return;
        }
        
        if (fakePlayerManager.disconnectFromServer(playerName)) {
            source.sendMessage(Component.text("Disconnected ").color(NamedTextColor.GREEN)
                    .append(Component.text(playerName))
                    .append(Component.text(" from server")));
            logger.info("{} disconnected fake player {}", source, playerName);
        } else {
            source.sendMessage(Component.text("Player is not connected to a server", NamedTextColor.RED));
        }
    }
    
    private void handleList(CommandSource source) {
        List<FakePlayer> fakePlayersList = new ArrayList<>(fakePlayerManager.getAllFakePlayers());
        
        if (fakePlayersList.isEmpty()) {
            source.sendMessage(Component.text("No fake players exist.", NamedTextColor.YELLOW));
            return;
        }
        
        source.sendMessage(Component.text("Fake Players List (" + fakePlayersList.size() + "):", NamedTextColor.GOLD));
        
        for (FakePlayer player : fakePlayersList) {
            String status = player.isConnected() ? 
                    "connected to " + player.getConnectedServer().getServerInfo().getName() : 
                    "not connected";
            
            Component playerComponent = Component.text("  • ")
                    .append(Component.text(player.getUsername()).color(NamedTextColor.AQUA))
                    .append(Component.text(" [" + status + "]"));
            
            source.sendMessage(playerComponent);
        }
    }
    
    private void handleInfo(CommandSource source, String[] args) {
        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /fakeplay info <player>", NamedTextColor.YELLOW));
            return;
        }
        
        String playerName = args[1];
        FakePlayer fakePlayer = fakePlayerManager.getFakePlayer(playerName);
        
        if (fakePlayer == null) {
            source.sendMessage(Component.text("Fake player not found: " + playerName, NamedTextColor.RED));
            return;
        }
        
        source.sendMessage(Component.text("Fake Player Info:", NamedTextColor.GOLD));
        source.sendMessage(Component.text("  Name: " + fakePlayer.getUsername()));
        source.sendMessage(Component.text("  UUID: " + fakePlayer.getUUID()));
        source.sendMessage(Component.text("  Online Time: " + fakePlayer.getOnlineTime() + "s"));
        
        if (fakePlayer.isConnected()) {
            source.sendMessage(Component.text("  Server: " + fakePlayer.getConnectedServer().getServerInfo().getName())
                    .color(NamedTextColor.GREEN));
        } else {
            source.sendMessage(Component.text("  Server: Not connected")
                    .color(NamedTextColor.RED));
        }
    }
    
    private void sendHelp(CommandSource source) {
        source.sendMessage(Component.text("FakePlayer Commands:", NamedTextColor.GOLD));
        source.sendMessage(Component.text("  /fakeplay create <name>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Create a fake player")));
        source.sendMessage(Component.text("  /fakeplay delete <name>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Delete a fake player")));
        source.sendMessage(Component.text("  /fakeplay connect <player> <server>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Connect fake player to server")));
        source.sendMessage(Component.text("  /fakeplay disconnect <player>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Disconnect fake player from server")));
        source.sendMessage(Component.text("  /fakeplay list").color(NamedTextColor.YELLOW)
                .append(Component.text(" - List all fake players")));
        source.sendMessage(Component.text("  /fakeplay info <player>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Get info about a fake player")));
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("fakeplayer.command");
    }
}
