package dev.astroolean.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WorldSpawnCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private Location spawnLocation; // Default spawn location

    public WorldSpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        loadSpawnLocation(); // Load the spawn location from config on initialization
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
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

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!sender.hasPermission("astrocore.spawn")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length == 0) {
            player.teleport(spawnLocation);
            player.sendMessage(ChatColor.GREEN + "You have been teleported to the spawn location.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            spawnLocation = player.getLocation();
            saveSpawnLocation(); // Save the new spawn location to config
            player.sendMessage(ChatColor.GREEN + "Spawn location has been set to your current location.");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /spawn to teleport or /spawn set to set spawn location.");
        return true;
    }

    private void saveSpawnLocation() {
        FileConfiguration config = plugin.getConfig();
        config.set("spawn.world", spawnLocation.getWorld().getName());
        config.set("spawn.x", spawnLocation.getX());
        config.set("spawn.y", spawnLocation.getY());
        config.set("spawn.z", spawnLocation.getZ());
        config.set("spawn.yaw", spawnLocation.getYaw());
        config.set("spawn.pitch", spawnLocation.getPitch());
        plugin.saveConfig();
    }

    private void loadSpawnLocation() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("spawn.world")) {
            String worldName = config.getString("spawn.world");
            double x = config.getDouble("spawn.x");
            double y = config.getDouble("spawn.y");
            double z = config.getDouble("spawn.z");
            float yaw = (float) config.getDouble("spawn.yaw");
            float pitch = (float) config.getDouble("spawn.pitch");

            spawnLocation = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            // Default spawn location if not set in config
            spawnLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }
    }
}
