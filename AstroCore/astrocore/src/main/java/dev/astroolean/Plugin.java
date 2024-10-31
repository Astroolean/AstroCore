package dev.astroolean;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/*
 * astrocore java plugin
 */
public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("astrocore");

  @Override
  public void onEnable()
  {
    LOGGER.info("astrocore enabled");
    // Register the command executor for the "hello" command
    // Ensure the command is defined in plugin.yml
    System.out.println("My first plugin has started");
    this.getCommand("hello").setExecutor(new HelloCommand());
  }

  @Override
  public void onDisable()
  {
    LOGGER.info("astrocore disabled");
    System.out.println("My first plugin has started");
  }
}