package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NearCommand implements CommandExecutor {

    private final int RADIUS = 500; // Define the radius for nearby players
    private final JavaPlugin plugin;

    public NearCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender has permission for the "sethome" command
        if (!sender.hasPermission("astrocore.near")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Assuming 'plugin' is an instance of dev.astroolean.Plugin
        if (!(plugin instanceof dev.astroolean.AstroCore)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }

        // Cast 'plugin' to your custom plugin class
        dev.astroolean.AstroCore myPlugin = (dev.astroolean.AstroCore) plugin;

        if (!myPlugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }        

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        List<String> nearbyPlayers = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && onlinePlayer.getLocation().distance(player.getLocation()) <= RADIUS) {
                nearbyPlayers.add(onlinePlayer.getName());
            }
        }

        // Create a clean message for nearby players
        String message;
        if (nearbyPlayers.isEmpty()) {
            message = "No players are nearby within " + RADIUS + " blocks.";
        } else {
            message = "Nearby players within " + RADIUS + " blocks: " + String.join(", ", nearbyPlayers) + ".";
        }

        player.sendMessage(message);
        return true;
    }
}
