package dev.astroolean;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

/*
 * AstroCore java plugin
 */
public class Plugin extends JavaPlugin
{
    private static final Logger LOGGER = Logger.getLogger("AstroCore");
    private boolean enabled = true; // Variable to keep track of enabled/disabled status

    @Override
    public void onEnable()
    {
        LOGGER.info("AstroCore enabled");
        System.out.println("My first plugin has started");
    
        // Register commands
        this.getCommand("AstroCore").setExecutor(new AstroCoreCommand(this));
        this.getCommand("hello").setExecutor(new HelloCommand(this)); // Pass the plugin instance
    }

    @Override
    public void onDisable()
    {
        LOGGER.info("AstroCore disabled");
        System.out.println("AstroCore plugin has stopped");
    }

    // Method to toggle plugin status
    public void togglePlugin()
    {
        enabled = !enabled;
        if (enabled)
        {
            LOGGER.info("AstroCore enabled");
        }
        else
        {
            LOGGER.info("AstroCore disabled");
        }
    }

    public boolean isEnabledCustom()
    {
        return enabled;
    }
}
