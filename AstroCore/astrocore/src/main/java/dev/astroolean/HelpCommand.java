package dev.astroolean;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor
{
    private final Plugin plugin;

    public HelpCommand(Plugin plugin)
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

        if (command.getName().equalsIgnoreCase("help"))
        {
            sender.sendMessage(ChatColor.DARK_RED + "Hello, " + sender.getName() + "! Please direct yourself over to" + ChatColor.RED + " unalive.me");
            return true;
        }
        return false;
    }
}