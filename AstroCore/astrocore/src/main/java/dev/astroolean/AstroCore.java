package dev.astroolean;

import dev.astroolean.commands.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
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

        // Initialize PlayerVaultCommand and load player vaults
        playerVaultCommand = new PlayerVaultCommand(this);
        playerVaultCommand.loadVaults();

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
        registerCommand("lore", new RenameCommand(this));
        registerCommand("p", new PluginsCommand(this));
        registerCommand("pv", new PlayerVaultCommand(this));
        registerCommand("fix", new RepairCommand(this));
        registerCommand("heal", new HealingCommand(this));
        registerCommand("sethome", new SetHomeCommand(this));
        registerCommand("home", new SetHomeCommand(this));
        registerCommand("delhome", new SetHomeCommand(this));
        registerCommand("homes", new SetHomeCommand(this));
        registerCommand("feed", new FeedCommand(this));
        registerCommand("spawn", new WorldSpawnCommand(this));

        // Register this class as a listener
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(playerVaultCommand, this);


        // Register tab completer for commands
        for (String command : new String[] {"AstroCore", "hello", "help", "snort", "smoke", "gm", "t", "w", "cc", "god", "ac", "rename", "lore", "p"}) {
            PluginCommand cmd = getCommand(command);
            if (cmd != null) {
                cmd.setTabCompleter(this); // Set this class as the tab completer
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
