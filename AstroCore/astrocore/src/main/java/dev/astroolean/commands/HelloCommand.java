package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.astroolean.AstroCore;

public class HelloCommand implements CommandExecutor
{
    private final AstroCore plugin;

    public HelloCommand(AstroCore plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the plugin is "disabled"
        if (!plugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }
    
        // Check if the sender has permission for the "hello" command
        if (!sender.hasPermission("astrocore.hello")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }
    
        // Check if the command is "hello"
        if (command.getName().equalsIgnoreCase("hello")) {
            sender.sendMessage(ChatColor.YELLOW + "Hello, " + sender.getName() + "! Welcome to the server!");
            return true;
        }
        return false;
    }
}