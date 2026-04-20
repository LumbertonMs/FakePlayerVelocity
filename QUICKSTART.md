# FakePlayerVelocity - Quick Start Guide

## Installation

1. **Download the Plugin**
   - The compiled JAR is available at: `target/fake-player-velocity-1.0.0.jar`

2. **Deploy to Velocity**
   - Copy `fake-player-velocity-1.0.0.jar` to your Velocity proxy's `plugins/` directory
   - Restart your Velocity proxy server

3. **Verify Installation**
   - Check logs for: `FakePlayerVelocity plugin initialized successfully!`
   - Use `/fakeplay help` to verify the command is available

## Basic Usage

### Create Fake Players
```
/fakeplay create Bot1
/fakeplay create TestPlayer
```

### Connect to Servers
```
/fakeplay connect Bot1 survival
/fakeplay connect TestPlayer creative
```

### View Fake Players
```
/fakeplay list
```

Output example:
```
Fake Players List (2):
  • Bot1 [connected to survival]
  • TestPlayer [connected to creative]
```

### Get Player Details
```
/fakeplay info Bot1
```

### Disconnect from Server
```
/fakeplay disconnect Bot1
```

### Delete Fake Player
```
/fakeplay delete Bot1
```

## Complete Command Reference

| Command | Usage | Description |
|---------|-------|-------------|
| create | `/fakeplay create <name>` | Create a new fake player (3-16 char alphanumeric) |
| delete | `/fakeplay delete <name>` | Remove a fake player |
| connect | `/fakeplay connect <player> <server>` | Connect fake player to backend server |
| disconnect | `/fakeplay disconnect <player>` | Disconnect fake player from server |
| list | `/fakeplay list` | List all fake players and their status |
| info | `/fakeplay info <player>` | Display detailed player information |
| help | `/fakeplay help` | Show all commands |

## Integration with /glist

Once connected to a server, fake players appear in that server's `/glist` output because they're part of Velocity's player tracking system. This means:

1. Fake players will show up when players run `/glist` on backend servers
2. They count towards server player limits
3. They're visible in motd player count if configured

## Example Scenarios

### Load Testing
```
/fakeplay create LoadTest_1
/fakeplay create LoadTest_2
/fakeplay create LoadTest_3
/fakeplay connect LoadTest_1 server1
/fakeplay connect LoadTest_2 server1
/fakeplay connect LoadTest_3 server2
```

### Server Population Display
```bash
# Create multiple fake players
for i in {1..10}; do
  /fakeplay create Player_$i
  /fakeplay connect Player_$i survival
done
```

Now `/glist` on the survival server will show 10 fake players!

### Cleanup
```bash
# Delete all fake players
/fakeplay delete Player_1
/fakeplay delete Player_2
# ... or use /fakeplay list to see all and delete them
```

## Troubleshooting

**Fake players not appearing in /glist?**
- Verify player is created: `/fakeplay list`
- Verify player is connected: Check the server in the list output
- Ensure the backend server is properly configured to query player lists

**Permission denied?**
- Ensure you have `fakeplayer.command` permission
- Add to your permission manager (LuckPerms example):
  ```
  /lp user <username> permission set fakeplayer.command true
  ```

**Plugin not loading?**
- Check that `plugins/` directory exists in your Velocity folder
- Verify the JAR filename is exactly `fake-player-velocity-1.0.0.jar`
- Check proxy logs for any errors

## Build from Source

If you need to rebuild or modify the plugin:

```bash
cd /workspaces/FakePlayerVelocity
mvn clean package
```

The compiled JAR will be in `target/fake-player-velocity-1.0.0.jar`

## Technical Details

- **Framework**: Velocity Proxy 3.1.1+
- **Language**: Java 21
- **Build Tool**: Maven
- **Storage**: In-memory (no persistence across restarts)
- **Thread Safety**: Fully concurrent-safe using ConcurrentHashMap

## Project Structure

```
src/main/java/com/fakeplayer/
├── FakePlayerPlugin.java          # Main plugin entry point
├── FakePlayerManager.java         # Manages fake player lifecycle
├── FakePlayer.java                # Fake player data model
├── FakePlayerCommand.java         # Command handler
└── FakePlayerEventListener.java   # Event listener

src/main/resources/
└── velocity-plugin.json           # Plugin manifest
```

## Future Enhancements (Planned)

- Configuration file support for default settings
- Persistent fake player storage (database/file)
- Auto-respawn functionality
- Fake player movement/activity simulation
- Advanced player list filtering

## Support

For issues or feature requests, refer to the repository or modify the source code as needed.

Good luck with your Velocity plugin!
