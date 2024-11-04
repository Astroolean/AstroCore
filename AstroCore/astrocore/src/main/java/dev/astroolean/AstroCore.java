package dev.astroolean;

import dev.astroolean.commands.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AstroCore extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger("AstroCore");
    private boolean enabled = true; // To keep track of enabled/disabled status
    private AutoLongDay autoLongDay; // Instance of AutoLongDay
    private PlayerVaultCommand playerVaultCommand; // Instance of PlayerVaultCommand

    @Override
    public void onEnable() {

        // Create the PlayerData directory if it doesn't exist
        File playerDataDir = new File(getDataFolder(), "PlayerData");
        if (!playerDataDir.exists()) {
            playerDataDir.mkdirs();
        }

        // Initialize PlayerVaultCommand and load vaults
        playerVaultCommand = new PlayerVaultCommand(this); // Initialize here
        playerVaultCommand.loadVaults(); // Now you can safely call loadVaults()

        // Display startup information
        LOGGER.info("AstroCore enabled");
        LOGGER.info("""

                               ____________________________
                              |                            |
                              |      AstroCore Plugin      |
                              |            V1.4            |
                              |____________________________|

                              My first plugin has started...

                               class AstrooleanSignature:
                                   def __init__(self, NAME="Astroolean"):
                                       self.NAME = NAME
                                   def Signature(self):
                                       Astro = "Astro - The online name I've always gone by."
                                       Boolean = "Boolean - Embracing the simplicity of true/false."
                                       return f"/nSignature:/n{Astro}/n{Boolean}/nAll-in-all it's just Astroolean."
                               Astroolean = AstrooleanSignature()
                               print(Astroolean.Signature())

                               Absolutely free and open-source for all to use and enjoy...

               """);

        // Initialize and enable AutoLongDay
        autoLongDay = new AutoLongDay();
        autoLongDay.onEnable(this);

        // Register commands
        registerCommand("AstroCore", new AstroCoreCommand(this));
        registerCommand("hello", new HelloCommand(this));
        registerCommand("help", new HelpCommand(this));
        registerCommand("snort", new CocaineCommand(this));
        registerCommand("smoke", new WeedCommand(this));
        registerCommand("gm", new GamemodeCommand(this));
        registerCommand("t", new TimeCommand(this));
        registerCommand("w", new WeatherCommand(this));
        registerCommand("cc", new ClearChatCommand(this));
        registerCommand("god", new GodCommand(this));
        registerCommand("ac", new acCommand(this));
        registerCommand("rename", new RenameCommand(this));
        registerCommand("lore", new RenameCommand(this)); // Corrected to LoreCommand
        registerCommand("p", new PluginsCommand(this));
        registerCommand("pv", playerVaultCommand); // Use the initialized instance here
        registerCommand("fix", new RepairCommand(this));
        registerCommand("heal", new HealingCommand(this));
        registerCommand("sethome", new SetHomeCommand(this));
        registerCommand("home", new SetHomeCommand(this));
        registerCommand("delhome", new SetHomeCommand(this));
        registerCommand("homes", new SetHomeCommand(this));
        registerCommand("feed", new FeedCommand(this));
        registerCommand("spawn", new WorldSpawnCommand(this));
        registerCommand("lock", new WeatherLockCommand(this));
        registerCommand("freeze", new FreezeCommand(this));
        registerCommand("showcoords", new ShowCoordsCommand(this));
        registerCommand("uncraft", new UncraftCommand(this));
        registerCommand("autorod", new AutoRodCommand(this));
        registerCommand("tos", new TOSCommand(this));
        registerCommand("near", new NearCommand(this));
        registerCommand("trash", new TrashCommand(this));

        // Register this class as a listener
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(playerVaultCommand, this);

        // Register tab completer for commands
        for (String command : new String[] {
            "AstroCore", "hello", "help", "snort", "smoke", "gm", "t", "w", "cc",
            "god", "ac", "rename", "lore", "p", "pv", "fix", "heal", "sethome",
            "home", "delhome", "homes", "feed", "spawn", "lock", "freeze",
            "showcoords", "uncraft", "autorod", "tos", "near", "trash",
        }) {
            PluginCommand cmd = getCommand(command);
            if (cmd != null) {
                cmd.setTabCompleter((CommandSender sender, Command cmd1, String label, String[] args) -> {
                    List<String> completions = new ArrayList<>();

                    switch (cmd1.getName().toLowerCase()) {
                        case "fix" -> {
                            if (args.length == 1) {
                                completions.addAll(Arrays.asList("hand", "all"));
                            }
                        }
                        case "lock" -> {
                            if (args.length == 1) {
                                completions.addAll(Arrays.asList("day", "night"));
                            }
                        }
                        case "pv" -> {
                            if (args.length == 1) {
                                completions.addAll(Arrays.asList("1", "2"));
                            }
                        }
                        case "uncraft" -> {
                            if (args.length == 1) {
                                completions.addAll(Arrays.asList("hand"));
                            }
                        }
                        case "tos" -> {
                            if (args.length == 1) {
                                completions.addAll(Arrays.asList("accept", "deny"));
                            }
                        }
                        case "infinite" -> {
                            switch (args.length) {
                                case 1 -> completions.addAll(Arrays.asList("buy", "sell"));
                                case 2 -> completions.addAll(Arrays.asList("water", "lava"));
                                default -> {
                                }
                            }
                        }
                        case "freeze" -> {
                            if (args.length == 1) {
                                // Add online player names for the first argument
                                Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                            } else if (args.length == 2) {
                                completions.addAll(Arrays.asList("5", "10", "30", "60")); // Example times in seconds
                            }
                        }
                        case "home", "delhome" -> {
                            if (args.length == 1) {
                                // Get home names from the player's data file
                                if (sender instanceof Player player) {
                                    UUID playerId = player.getUniqueId();
                                    File playerFile = new File(getDataFolder() + "/PlayerData", playerId + ".yml");
                                    FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);

                                    ConfigurationSection homesSection = playerData.getConfigurationSection("homes");
                                    if (homesSection != null) {
                                        homesSection.getKeys(false).forEach(homeName -> completions.add(homeName));
                                    }
                                }
                            }
                        }
                        // Add more cases as needed for other commands if tab completion options are required...
                    }

                    // Filter completions based on input
                    return completions.stream()
                        .filter(c -> c.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        .toList();
                });
            }
        }
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = this.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            LOGGER.warning(String.format("Command %s not found. Ensure it is defined in plugin.yml.", commandName));
        }
    }

    @Override
    public void onDisable() {
        if (playerVaultCommand != null) {
            playerVaultCommand.saveVaults(); // Save all player vaults on disable
        }
        LOGGER.info("AstroCore disabled");
        if (autoLongDay != null) {
            autoLongDay.onDisable(); // Disable AutoLongDay
        }
    }

    // Method to toggle plugin status
    public void togglePlugin() {
        enabled = !enabled;
        LOGGER.info(enabled ? "AstroCore enabled" : "AstroCore disabled");
    }

    public boolean isEnabledCustom() {
        return enabled;
    }

    // Event listener to block the "/plugins" command
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();

        LOGGER.log(Level.INFO, "Received command: {0}", command);

        // Check for the /plugins command
        if (command.startsWith("/plugins")) {
            event.getPlayer().sendMessage("You cannot use this command.");
            event.setCancelled(true);
            LOGGER.log(Level.INFO, "Command cancelled: {0}", command);
        }

        // Check for the /spawnpoint command
        if (command.startsWith("/spawnpoint")) {
            event.getPlayer().sendMessage("You cannot use this command.");
            event.setCancelled(true);
            LOGGER.log(Level.INFO, "Command cancelled: {0}", command);
        }
    }
}
