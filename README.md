<p align="center">
    <img src="https://github.com/user-attachments/assets/b60afeff-a144-4dff-9e29-4d1dc60597fc" alt="AstroCore Image" />
</p>

**AstroCore** is a demonstration of a Minecraft plugin I am creating. It's my first-ever Minecraft plugin, and I'm very new to Java coding. Issues and problems are expected. This is purely for fun and educational purposes. With the help of AI and trial and error, along with my knowledge of Python, I decided to give this a chance to fully learn. This is a public, free, and open-source Minecraft plugin available to everyone. I have a lot planned, and I will continue to learn and improve it along the way. This is only just the beginning... My coding journey continues here!

---

## Requirements

Before running this plugin, ensure you have the following:

- **Minecraft Server**: A server running Paper 1.20.4.
- **Java**: JDK 17 or higher installed on your system.
- **Plugin Installation**: Familiarity with how to install plugins on a Minecraft server.
- **Basic Configuration Knowledge**: Understanding how to modify configuration files for plugins.
- **Permissions Plugin**: LuckPerms or another permissions plugin is required to set up permissions for this plugin.

---

## Installation

1. **Download the Plugin**:
   - Obtain the latest version of the AstroCore plugin from the [Releases](https://github.com/Astroolean/AstroCore/releases) section of this repository.

2. **Place the Plugin in the Server**:
   - Move the downloaded `.jar` file into the `plugins` folder of your Paper server directory.

3. **Start the Server**:
   - Launch your Paper server. The plugin will generate its configuration files upon startup.

---

## Usage

AstroCore provides a range of commands designed to enhance gameplay and offer unique features tailored to the Minecraft experience. The plugin is designed with both new and experienced players in mind, ensuring that everyone can enjoy the added functionalities.

### Command Overview

- **/astrocore**: Enables or disables the AstroCore plugin. Absolutely nothing will work.
- **/hello**: Responds with a friendly greeting.
- **/help**: Provides information and directs you to mental health resources.
- **/god**: Grants invincibility and the ability to fly, making you a formidable player.
- **/cc**: Clears the chat for a fresh start.
- **/smoke**: Consumes 16 green dye for an in-game high (custom model data support will enhance this feature).
- **/snort**: Consumes 16 sugar for a different in-game high (also enhanced by custom model data).
- **/gm**: Opens the Gamemode GUI for easy mode switching.
- **/t**: Opens the Time GUI for adjusting time settings.
- **/w**: Opens the Weather GUI to change weather conditions.
- **/rename [name]**: Rename the item you are holding.
- **/lore [line] [text]**: Set lore for the item you are holding.
- **/p**: Shows the list of installed plugins if the player is OP.
- **/pv [number]**: PlayerVault storage for players.
- **/fix [hand/all]**: Repair items for experience.
- **/heal**: Heal the player to full health (OP only).
- **/sethome [name]**: Set a home location.
- **/home [name]**: Teleport to a home location.
- **/delhome [name]**: Delete a home location.
- **/homes**: View current homes.
- **/feed**: Feed the player (OP only).
- **/spawn [set]**: Set and/or teleport to spawn.
- **/lock [day/night]**: Lock the current time to day or night.
- **/freeze [player] [time]**: Freeze other players.
- **/showcoords**: Show current coordinates within game chat.
- **/uncraft [hand]**: Uncraft an item you are holding.
- **/autorod**: Gives a fishing rod that auto-fishes.
- **/tos [accept/deny]**: Accept or deny the terms of service.
- **/near**: See who may be nearby.
- **/trash**: Throw useless garbage away for good.

---

## Permissions

| Permission            | Description                                           | Default  |
|-----------------------|-------------------------------------------------------|----------|
| `astrocore.use`       | Allows usage of the AstroCore commands.              | `false`  |
| `astrocore.rename`    | Allows the player to rename items.                   | `false`  |
| `astrocore.lore`      | Allows the player to set item lore.                  | `false`  |
| `astrocore.plugins`    | Allows the player to see the list of installed plugins. | `op`     |
| `astrocore.pv`        | Allows the player to access the player vault.        | `false`  |
| `astrocore.fix`       | Allows the player to repair items.                   | `false`  |
| `astrocore.heal`      | Allows the player to heal themselves.                | `false`  |
| `astrocore.sethome`   | Allows the player to set home locations.             | `false`  |
| `astrocore.home`      | Allows the player to teleport to home locations.     | `false`  |
| `astrocore.delhome`   | Allows the player to delete home locations.          | `false`  |
| `astrocore.homes`     | Allows the player to view their home locations.      | `false`  |
| `astrocore.feed`      | Allows the player to feed themselves.                | `false`  |
| `astrocore.spawn`     | Allows the player to set and teleport to spawn.      | `false`  |
| `astrocore.clearChat` | Allows the player to clear the chat.                 | `false`  |
| `astrocore.god`       | Allows the player to use god mode.                   | `false`  |
| `astrocore.smoke`     | Allows the player to get high.                        | `false`  |
| `astrocore.snort`     | Allows the player to get high.                        | `false`  |
| `astrocore.astrocore` | Enable/disable the plugin.                           | `op`     |
| `astrocore.hello`     | Friendly hello message to the player.                | `false`  |
| `astrocore.help`      | Directs you to the mental health hotline.            | `false`  |
| `astrocore.lock`      | Allows the player to lock the time to day or night.  | `false`  |
| `astrocore.freeze`    | Allows the player to freeze other players.           | `false`  |
| `astrocore.showcoords`| Allows the player to show their coordinates.         | `false`  |
| `astrocore.uncraft`   | Allows the player to uncraft an item.                | `false`  |
| `astrocore.autorod`   | Allows the player to use the auto-fishing rod.       | `false`  |
| `astrocore.tos`   | Accept or deny the terms of service.   | `false`  |
| `astrocore.near`      | Allows the player to see nearby players.             | `false`  |
| `astrocore.trash`     | Allows the player to access the trash GUI.           | `false`  |


---

## Troubleshooting

- **Plugin Not Loading**: 
  - If AstroCore fails to load, check the server logs for any error messages that may indicate the issue. Common problems include:
    - Incompatible versions of the server (ensure you are using Paper 1.20.4).
    - Missing or corrupted plugin files. Make sure the `.jar` file is placed correctly in the `plugins` folder.
    - Ensure that Java is properly installed and updated to a compatible version for Minecraft.

- **Command Issues**: 
  - If commands do not seem to work:
    - Double-check that you are typing the commands correctly. Use `/ac` to see a list of available commands and their syntax.
    - Since AstroCore does not require specific permissions for commands, ensure you have the right context (e.g., being a player in the game).
    - If a command returns an unexpected response or error, consult the server logs for additional context on what might have gone wrong.

- **Gameplay Features Not Functioning**: 
  - If any features are not behaving as intended, consider:
    - Restarting your server to refresh the plugin's state.
    - Reviewing any configuration settings (if applicable) that might affect the plugin's functionality.
    - Checking for conflicts with other installed plugins that might override or interfere with AstroCore's commands.

If you continue to experience issues, please feel free to direct yourself over to unalive.me.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Contact

For questions or feedback, please contact me @Astroolean on Discord.
