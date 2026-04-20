# FakePlayerVelocity

A Velocity proxy plugin that allows you to create and manage fake players that appear on your servers and in `/glist` commands. Perfect for testing, load balancing demonstrations, or player count inflation.

## Features

- ✅ Create fake players with randomized UUIDs
- ✅ Connect fake players to backend servers
- ✅ Manage player lifecycles (create, delete, connect, disconnect)
- ✅ Fake players appear in player lists and `/glist` per-server
- ✅ Real-time player count integration
- ✅ Command-based management system
- ✅ Concurrent-safe player management

## Building

### Prerequisites
- Java 21 or higher
- Maven 3.9+

### Build Command

```bash
mvn clean package
```

The compiled plugin will be in `target/fake-player-velocity-1.0.0.jar`

## Installation

1. Build the plugin using the command above
2. Copy the JAR to your Velocity proxy's `plugins/` directory
3. Restart your Velocity proxy
4. The plugin will automatically initialize

## Usage

### Commands

All commands require the `fakeplayer.command` permission.

#### Create a Fake Player
```
/fakeplay create <username>
```
Creates a new fake player with a random UUID.

**Example:** `/fakeplay create FakePlayer1`

#### Connect to Server
```
/fakeplay connect <player> <server>
```
Connects a fake player to a backend server.

**Example:** `/fakeplay connect FakePlayer1 survival`

#### Disconnect from Server
```
/fakeplay disconnect <player>
```
Disconnects a fake player from their current server.

**Example:** `/fakeplay disconnect FakePlayer1`

#### List Fake Players
```
/fakeplay list
```
Shows all created fake players and their connection status.

#### Get Player Info
```
/fakeplay info <player>
```
Displays detailed information about a specific fake player.

**Example:** `/fakeplay info FakePlayer1`

Output includes:
- Username
- UUID
- Online time (in seconds)
- Current server connection

#### Delete a Fake Player
```
/fakeplay delete <player>
```
Removes a fake player from the proxy.

**Example:** `/fakeplay delete FakePlayer1`

#### Command Help
```
/fakeplay help
```
Shows all available commands and their usage.

## How It Works

### Player Registration
When you create a fake player, the plugin:
1. Generates a random UUID
2. Stores the player in memory with a concurrent map
3. Makes the player queryable by name and UUID

### Server Connection
When you connect a fake player to a server:
1. Verifies the server exists in your Velocity configuration
2. Associates the fake player with that server
3. The player appears in server-specific lists

### Integration with glist
The fake players integrate with per-server `/glist` commands because:
- They're stored in Velocity's memory alongside real player data
- Backend servers query Velocity's player list through the proxy
- Fake players respond to player list queries just like real players

### File Structure

```
src/
├── main/
│   ├── java/com/fakeplayer/
│   │   ├── FakePlayerPlugin.java          # Main plugin class
│   │   ├── FakePlayerManager.java         # Manages fake players
│   │   ├── FakePlayer.java                # Fake player data model
│   │   ├── FakePlayerCommand.java         # Command handler
│   │   └── FakePlayerEventListener.java   # Event listener
│   └── resources/
│       └── velocity-plugin.json           # Plugin manifest
pom.xml                                    # Maven configuration
```

## Configuration

The plugin is currently managed entirely through commands. Future versions may include a configuration file for:
- Default fake player limits
- Auto-respawn settings
- Persistent storage

## Permissions

Users need the `fakeplayer.command` permission to use any fake player commands.

### Permission Suggestion for LuckPerms
```
/lp group default permission set fakeplayer.command true
```

## Examples

### Create 5 Fake Players and Connect Them

```
/fakeplay create Bot1
/fakeplay create Bot2
/fakeplay create Bot3
/fakeplay create Bot4
/fakeplay create Bot5

/fakeplay connect Bot1 survival
/fakeplay connect Bot2 survival
/fakeplay connect Bot3 creative
/fakeplay connect Bot4 creative
/fakeplay connect Bot5 survival
```

Now `/glist` on both servers will show these fake players!

### Monitor Fake Players

```
/fakeplay list

# Output:
# Fake Players List (5):
#   • Bot1 [connected to survival]
#   • Bot2 [connected to survival]
#   • Bot3 [connected to creative]
#   • Bot4 [connected to creative]
#   • Bot5 [connected to survival]
```

### Check Individual Player Stats

```
/fakeplay info Bot1

# Output:
# Fake Player Info:
#   Name: Bot1
#   UUID: a1b2c3d4-e5f6-7890-abcd-ef1234567890
#   Online Time: 3600s
#   Server: survival
```

## Technical Details

### Thread Safety
- All fake player storage uses `ConcurrentHashMap`
- Safe for concurrent command execution and queries

### Memory Management
- Fake players are stored in memory only
- Data persists during server runtime
- Players are cleared on proxy restart

### UUID Generation
- Each fake player gets a random UUID
- UUIDs are guaranteed to be unique
- Format: Standard Java UUID

## Troubleshooting

### Fake Players Not Showing in /glist
1. Verify the fake player is created: `/fakeplay list`
2. Check the player is connected to the server: `/fakeplay connect <name> <server>`
3. Ensure backend servers are configured to query Velocity's player list

### Command Not Working
1. Check you have the `fakeplayer.command` permission
2. Verify the Velocity proxy has restarted after plugin installation
3. Check server logs for any error messages

### Players Disappearing After Restart
This is expected behavior - fake players are stored in memory only. To maintain persistent fake players, recommend implementing a configuration file-based system in future updates.

## Development

### Adding New Features

To add new commands, extend `FakePlayerCommand.java`:

```java
private void handleNewFeature(CommandSource source, String[] args) {
    // Your implementation here
    FakePlayer player = fakePlayerManager.getFakePlayer(args[1]);
    if (player != null) {
        source.sendMessage(Component.text("Success", NamedTextColor.GREEN));
    }
}
```

Then add a case in the `execute()` method:
```java
case "newfeature":
    handleNewFeature(source, args);
    break;
```

## License

This project is provided as-is for use with Velocity proxy servers.

## Support

For issues or feature requests, please refer to the GitHub repository.