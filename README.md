<p align="center">
    <img src="https://github.com/user-attachments/assets/61825b89-dc00-4ff8-89e2-36c834812bc5" alt="AstroCore Image" />
</p>


**AstroCore** is a demonstration of a minecraft plugin I am creating. First ever Minecraft plugin. Very new to Java code. Issues and or problems are expected. This is purely for fun and or educational purposes. With the help of AI and trial and error; with the use of my knowledge of Python. Gave this a chance in order to fully learn. This is a public free and open-source minecraft plugin; available to anyone and all. I have a lot planned but little by little ill learn and continue to improve this even further along the way. This is only just the beginning... My coding journey continues here!

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
- **/rename**: Rename any item within the game. All color codes possible.
- **/lore**: Add lore onto any item within the game. All color codes possible.
- **/p**: Disabled the default plugins command. Introduces a custom one viewable to opped players only.
- **/pv**: Player vaults, max 2 per player. Just like the OG days. One of the classics; extra storage.
- **/fix**: The ability to repair items via a command specifying hand or all. Costs experience.
- **/heal**: A command only availble to opped players that will heal you.
- **/sethome**: A way for players to set a home in order to travel between places easier.
- **/home**: The command that allows you to teleport from home to home. Making travel easier.
- **/delhome**: A command that will delete a specified home via the name given.
- **/homes**: A command that will list all of your current homes.
- **/feed**: A command only availble to opped players that will feed you.
- **/spawn**: Set and or teleport to spawn. Disables the spawnpoint command.

---

## Permissions

| Permission          | Description                                           | Default  |
|---------------------|-------------------------------------------------------|----------|
| `astrocore.use`     | Allows usage of the AstroCore commands.              | `false`  |
| `astrocore.rename`  | Allows the player to rename items.                   | `false`  |
| `astrocore.lore`    | Allows the player to set item lore.                  | `false`  |
| `astrocore.plugins`  | Allows the player to see the list of installed plugins. | `op`     |
| `astrocore.pv`      | Allows the player to access the player vault.        | `false`  |
| `astrocore.fix`     | Allows the player to repair items.                   | `false`  |
| `astrocore.heal`    | Allows the player to heal themselves.                | `false`  |
| `astrocore.sethome` | Allows the player to set home locations.             | `false`  |
| `astrocore.home`    | Allows the player to teleport to home locations.     | `false`  |
| `astrocore.delhome` | Allows the player to delete home locations.          | `false`  |
| `astrocore.homes`   | Allows the player to view their home locations.      | `false`  |
| `astrocore.feed`    | Allows the player to feed themselves.                | `false`  |
| `astrocore.spawn`   | Allows the player to set and teleport to spawn.      | `false`  |
| `astrocore.clearChat`| Allows the player to clear the chat.                | `false`  |
| `astrocore.god`     | Allows the player to use god mode.                   | `false`  |
| `astrocore.smoke`   | Allows the player to get high.                        | `false`  |
| `astrocore.snort`   | Allows the player to get high.                        | `false`  |
| `astrocore.astrocore`| Enable/disable the plugin.                          | `op`     |
| `astrocore.hello`   | Friendly hello message to the player.                | `false`  |
| `astrocore.help`    | Directs you to the suicide hotline.                  | `false`     |


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

If you continue to experience issues, please feel free to direct yourself over to unalive.me

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Contact

For questions or feedback, please contact me @Astroolean on discord...
