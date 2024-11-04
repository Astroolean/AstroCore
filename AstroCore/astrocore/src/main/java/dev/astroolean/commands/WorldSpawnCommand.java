package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WorldSpawnCommand implements CommandExecutor {

    @SuppressWarnings("unused")
    private final JavaPlugin plugin;
    private Location spawnLocation; // Default spawn location

    public WorldSpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        // Initialize with default spawn location (can be modified)
        this.spawnLocation = new Location(plugin.getServer().getWorlds().get(0), 0, 100, 0); // Example coordinates
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender has permission for the "spawn" command
        if (!sender.hasPermission("astrocore.spawn")) {
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

        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Handle command arguments
        if (args.length == 0) {
            // Teleport the player to the spawn location
            player.teleport(spawnLocation);
            player.sendMessage(ChatColor.GREEN + "You have been teleported to the spawn location.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            // Set the spawn location to the player's current location
            spawnLocation = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Spawn location has been set to your current location.");
            return true;
        }

        // Invalid command usage
        player.sendMessage(ChatColor.RED + "Usage: /spawn to teleport or /spawn set to set spawn location.");
        return true;
    }
}
