package dev.astroolean;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

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
        LOGGER.info("""
           
            ____________________________
           |                            |
           |      AstroCore Plugin      |
           |            V1.1            |
           |____________________________|
           
           My first plugin has started...

           class AstrooleanSignature:
               def __init__(self, NAME="Astroolean"):
                   self.NAME = NAME
               def Signature(self):
                   Astro = "Astro - The online name ive always went by."
                   Boolean = "Boolean - Embracing the simplicity of true/false."
                   return f"/nSignature:/n{Astro}/n{Boolean}/nAll-in-all its just Astroolean."
           Astroolean = AstrooleanSignature()
           print(Astroolean.Signature())

        """);    
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
