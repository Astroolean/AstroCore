package dev.astroolean.commands;

import dev.astroolean.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class SetHomeCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds
    private final AstroCore plugin;

    public SetHomeCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null || !(sender instanceof Player player)) {
            if (sender != null) sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        String homeName;

        // Handle the /homes command
        if (label.equalsIgnoreCase("homes")) {
            UUID playerId = player.getUniqueId();
            File playerFile = new File(plugin.getDataFolder() + "/PlayerData", playerId + ".yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);

            if (!playerData.contains("homes")) {
                player.sendMessage(ChatColor.RED + "You have no homes set.");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "Your homes:");
            for (String key : playerData.getConfigurationSection("homes").getKeys(false)) {
                player.sendMessage(ChatColor.GOLD + "- " + key);
            }
            return true;
        }

        // Check for cooldown
        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeLeft = (cooldowns.get(player.getUniqueId()) + (cooldownTime * 1000)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000) + " seconds before using this command again.");
                return true;
            }
        }

        // Set the cooldown
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        // Handle the /sethome command
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <homeName>");
            return true;
        }

        homeName = args[0].toLowerCase();
        UUID playerId = player.getUniqueId();
        File playerFile = new File(plugin.getDataFolder() + "/PlayerData", playerId + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);

        if (label.equalsIgnoreCase("sethome")) {
            if (playerData.contains("homes." + homeName)) {
                player.sendMessage(ChatColor.RED + "Home '" + homeName + "' already exists. Please choose a different name.");
                return true;
            }

            Location location = player.getLocation();
            World world = location.getWorld();

            if (world == null) {
                player.sendMessage(ChatColor.RED + "Unable to set home: World is not loaded.");
                return true;
            }

            playerData.set("homes." + homeName + ".world", world.getName());
            playerData.set("homes." + homeName + ".x", location.getX());
            playerData.set("homes." + homeName + ".y", location.getY());
            playerData.set("homes." + homeName + ".z", location.getZ());
            playerData.set("homes." + homeName + ".yaw", location.getYaw());
            playerData.set("homes." + homeName + ".pitch", location.getPitch());

            try {
                playerData.save(playerFile);
                player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' set!");
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Failed to save home. Please try again.");
                plugin.getLogger().log(Level.SEVERE, "Could not save home for player {0}: {1}", new Object[]{playerId, e.getMessage()});
            }
            return true;

        } else if (label.equalsIgnoreCase("home")) {
            if (!playerData.contains("homes." + homeName)) {
                player.sendMessage(ChatColor.RED + "Home '" + homeName + "' does not exist.");
                return true;
            }

            String worldName = playerData.getString("homes." + homeName + ".world");
            if (worldName == null || Bukkit.getWorld(worldName) == null) {
                player.sendMessage(ChatColor.RED + "The world for home '" + homeName + "' is not loaded or does not exist.");
                return true;
            }

            World world = Bukkit.getWorld(worldName);
            double x = playerData.getDouble("homes." + homeName + ".x");
            double y = playerData.getDouble("homes." + homeName + ".y");
            double z = playerData.getDouble("homes." + homeName + ".z");
            float yaw = (float) playerData.getDouble("homes." + homeName + ".yaw");
            float pitch = (float) playerData.getDouble("homes." + homeName + ".pitch");

            Location homeLocation = new Location(world, x, y, z, yaw, pitch);
            player.teleport(homeLocation);
            player.sendMessage(ChatColor.GREEN + "Teleported to home '" + homeName + "'!");
            return true;

        } else if (label.equalsIgnoreCase("delhome")) {
            if (!playerData.contains("homes." + homeName)) {
                player.sendMessage(ChatColor.RED + "Home '" + homeName + "' does not exist.");
                return true;
            }

            playerData.set("homes." + homeName, null);

            try {
                playerData.save(playerFile);
                player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' deleted successfully.");
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Failed to delete home. Please try again.");
                plugin.getLogger().log(Level.SEVERE, "Could not delete home for player {0}: {1}", new Object[]{playerId, e.getMessage()});
            }
            return true;
        }

        return false;
    }
}
