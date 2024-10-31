package dev.astroolean;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelloCommand implements CommandExecutor
{
    private final Plugin plugin;

    public HelloCommand(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!plugin.isEnabledCustom()) // Check if the plugin is "disabled"
        {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("hello"))
        {
            sender.sendMessage(ChatColor.YELLOW + "Hello, " + sender.getName() + "! Welcome to the server!");
            return true;
        }
        return false;
    }
}
