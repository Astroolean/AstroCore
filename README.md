<p align="center">
    <img src="https://github.com/user-attachments/assets/b43bb3d0-a75a-47a7-a069-7f1729f22bc3" alt="AstroCore Image" />
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

AstroCore provides a range of commands designed to enhance gameplay and offer unique features tailored to the Minecraft experience. The plugin is designed with both new and experienced players in mind, ensuring that everyone can enjoy the added functionalities. Over 50+ custom commands and or features...

### Command Overview

1. **/astrocore**: Enables or disables the AstroCore plugin. Absolutely nothing will work.
2. **/help**: Provides basic information about the plugin.
3. **/god**: Grants invincibility and the ability to fly, making you a formidable player.
4. **/cc**: Clears the chat for a fresh start.
5. **/gm**: Opens the Gamemode GUI for easy mode switching.
6. **/t**: Opens the Time GUI for adjusting time settings.
7. **/w**: Opens the Weather GUI to change weather conditions.
8. **/rename [name]**: Rename the item you are holding.
9. **/lore [line] [text]**: Set lore for the item you are holding.
10. **/pv [number]**: PlayerVault storage for players.
11. **/fix [hand/all]**: Repair items for experience.
12. **/heal**: Heal the player to full health (OP only).
13. **/sethome [name]**: Set a home location.
14. **/home [name]**: Teleport to a home location.
15. **/delhome [name]**: Delete a home location.
16. **/homes**: View current homes.
17. **/feed**: Feed the player (OP only).
18. **/spawn [set]**: Set and/or teleport to spawn.
19. **/lock [day/night]**: Lock the current time to day or night.
20. **/freeze [player] [time]**: Freeze other players.
21. **/showcoords**: Show current coordinates within game chat.
22. **/uncraft [hand]**: Uncraft an item you are holding.
23. **/autorod**: Gives a fishing rod that auto-fishes.
24. **/tos [accept/deny]**: Accept or deny the terms of service.
25. **/near**: See who may be nearby.
26. **/trash**: Throw useless garbage away for good.
27. **/message [player]**: Send a message to another player.
28. **/reply [player]**: Replies to another player.
29. **/color [color/style]**: Provides color examples.
30. **/invsee [player]**: View another player's inventory for a short duration.
31. **/autoarmor [enable/disable]**: Automatically applies best armor within inventory.
32. **/autotool [enable/disable]**: Automatically applies best tool within inventory.
33. **/expfly**: Allows you to fly at a cost of 500 experience points every minute.
34. **/back**: Back to the last death location.
35. **/voidsafe [enable/disable]**: Fall into the void without the risk of dying.
36. **/hard [enable/disable]**: This makes the game very difficult...
37. **/explosion [enable/disable]**: Enable or disable creeper or TNT explosions.
38. **/multibreak**: Allows you to break 3x3x3 at a cost of 500 experience points every minute.
39. **/nickname [name/reset]**: Give yourself a nickname of choice; costs experience.
40. **/whois [player]**: Find out who someone is no matter the nickname.
41. **/show [hand]**: Show off your numerous items and stuff.
42. **/clone [hand]**: Why dupe when you can do it legally; costs experience.
43. **/broadcast [message]**: Broadcast your message to the server at the cost of experience.
44. **/autotorch [enable/disable]**: Automatically place torches depending on light level efficiently.
45. **/morexp [enable/disable]**: Gives XP based on what you do—mining, farming, etc.
46. **/combine [hand] [potion]**: Combine potions with anything in your main hand.
47. **/gravity [low/medium/high]**: Moon-like gravity to traverse the world itself.
48. **/blink [distance]**: Teleport a short distance in any direction.
49. **/itemroll [auto/once] [amount]**: Allows the player to roll for random stuff.
50. **/infinite [water/lava]**: Purchase infinite water/lava bucket via experience.
51. **/boost [enable/disable]**: As fast as the flash; costs experience.
52. **/border [square] [size]**: Gives XP based on what you do—mining, farming, etc.
53. **/rtp**: Random teleport throughout your world.


---

## Permissions

| Permission            | Description                                           | Default  |
|------------------------|-------------------------------------------------------|----------|
| `astrocore.use`        | Allows usage of the AstroCore commands.               | `false`  |
| `astrocore.rename`     | Allows the player to rename items.                    | `false`  |
| `astrocore.lore`       | Allows the player to set item lore.                   | `false`  |
| `astrocore.pv`         | Allows the player to access the player vault.         | `false`  |
| `astrocore.fix`        | Allows the player to repair items.                    | `false`  |
| `astrocore.heal`       | Allows the player to heal themselves.                 | `false`  |
| `astrocore.sethome`    | Allows the player to set home locations.              | `false`  |
| `astrocore.home`       | Allows the player to teleport to home locations.      | `false`  |
| `astrocore.delhome`    | Allows the player to delete home locations.           | `false`  |
| `astrocore.homes`      | Allows the player to view their home locations.       | `false`  |
| `astrocore.feed`       | Allows the player to feed themselves.                 | `false`  |
| `astrocore.spawn`      | Allows the player to set and teleport to spawn.       | `false`  |
| `astrocore.clearChat`  | Allows the player to clear the chat.                  | `false`  |
| `astrocore.god`        | Allows the player to use god mode.                    | `false`  |
| `astrocore.astrocore`  | Enable/disable the plugin.                            | `op`     |
| `astrocore.lock`       | Allows the player to lock the time to day or night.   | `false`  |
| `astrocore.freeze`     | Freezes other players if your into that stuff.        | `false`  |
| `astrocore.showcoords` | Shows your current coordinates within game chat.      | `false`  |
| `astrocore.uncraft`    | Uncraft an item you are holding.                      | `false`  |
| `astrocore.autorod`    | Gives you a fishing rod that auto-fishes.             | `false`  |
| `astrocore.tos`        | Accept or deny the terms of service.                  | `false`  |
| `astrocore.near`       | See who may be nearby.                                | `false`  |
| `astrocore.trash`      | Throw useless garbage away for good.                  | `false`  |
| `astrocore.message`    | Send a message to another player.                     | `false`  |
| `astrocore.color`      | Provides color examples.                              | `false`  |
| `astrocore.invsee`     | View another player's inventory for a short duration. | `false`  |
| `astrocore.autoarmor`  | Automatically applies best armor within inventory.    | `false`  |
| `astrocore.autotool`   | Automatically applies best tool within inventory.     | `false`  |
| `astrocore.expfly`     | Fly at a cost of 500 experience points every minute.  | `false`  |
| `astrocore.back`       | Back to the last death location.                      | `false`  |
| `astrocore.voidsafe`   | Fall into the void without the risk of dying.         | `false`  |
| `astrocore.hard`       | This makes the game very difficult...                 | `false`  |
| `astrocore.explosion`  | Enable or disable creeper or tnt explosions.          | `false`  |
| `astrocore.multibreak` | Allows you to break 3x3x3 at a cost of XP.            | `false`  |
| `astrocore.nickname`   | Give yourself a nickname of choice; costs XP.         | `false`  |
| `astrocore.whois`      | Find out who someone is. Explains itself...           | `false`  |
| `astrocore.show`       | Show off your numerous items and stuff.               | `false`  |
| `astrocore.clone`      | Why dupe? when you can do it legally; costs XP...     | `false`  |
| `astrocore.broadcast`  | Broadcast your message to the server; costs XP.       | `false`  |
| `astrocore.autotorch`  | Automatically place torches; depends on light level.  | `false`  |
| `astrocore.morexp`     | Gives xp based on stuff. Mining, farming, etc...      | `false`  |
| `astrocore.combine`    | Combine potions with anything in your main hand.      | `false`  |
| `astrocore.gravity`    | Moon-like gravity to traverse the world itself.       | `false`  |
| `astrocore.blink`      | Teleport a short distance in any direction.           | `false`  |
| `astrocore.itemroll`   | Allows the player to roll for random stuff.           | `false`  |
| `astrocore.infinite`   | Purchase infinite water/lava bucket via experience.   | `false`  |
| `astrocore.boost`      | As fast as the flash; costs experience.               | `false`  |
| `astrocore.border`     | Make/remove a square world border.                    | `false`  |
| `astrocore.broadcast`  | Broadcast your message to the server; costs XP.       | `false`  |
| `astrocore.autotorch`  | Automatically place torches; depends on light level.  | `false`  |
| `astrocore.rtp`        | Random teleport throughout your world.                | `false`  |


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
