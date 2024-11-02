package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.astroolean.AstroCore;

public class AstroCoreCommand implements CommandExecutor
{
    private final AstroCore plugin;

    public AstroCoreCommand(AstroCore plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("AstroCore"))
        {
            plugin.togglePlugin(); // Toggle the plugin status

            if (plugin.isEnabledCustom())
            {
                sender.sendMessage(ChatColor.GREEN + "AstroCore plugin enabled");
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "AstroCore plugin disabled");
            }
            return true;
        }
        return false;
    }
}