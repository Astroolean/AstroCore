package dev.astroolean.commands.admin;

import dev.astroolean.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BorderCommand implements CommandExecutor {

    private final AstroCore plugin;

    public BorderCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = validatePlayer(sender);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        // Ensure the plugin is enabled
        if (!plugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // Ensure the player has the required permission
        if (!player.hasPermission("astrocore.border")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Handle border removal
        if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            removeBorder(player);
            player.sendMessage(ChatColor.GREEN + "Border removed and saved to the configuration.");
            return true;
        }

        // Validate argument length
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /border square [size] or /border remove");
            return true;
        }

        // Parse the size argument
        int size;
        try {
            size = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid size value.");
            return true;
        }

        // Get the current world
        World world = player.getWorld();

        // Scale the size based on the world type (Overworld, Nether, End)
        double scaledSize = scaleBorderSize(world.getEnvironment(), size);

        // Set the square world border
        if (!setSquareBorder(player, scaledSize)) {
            player.sendMessage(ChatColor.RED + "Unable to set the world border because your location could not be determined.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Square border set with size " + scaledSize + " and saved to the configuration.");
        return true;
    }

    private Player validatePlayer(CommandSender sender) {
        if (sender == null || !(sender instanceof Player)) return null;
        return (Player) sender;
    }

    private void removeBorder(Player player) {
        World world = player.getWorld();
        org.bukkit.WorldBorder worldBorder = world.getWorldBorder();

        // Reset the world border to default
        worldBorder.reset();

        // Remove saved border information from the config
        plugin.getConfig().set("border." + world.getName(), null);
        plugin.saveConfig();
    }

    private boolean setSquareBorder(Player player, double size) {
        World world = player.getWorld();
        org.bukkit.WorldBorder worldBorder = world.getWorldBorder();

        Location location = player.getLocation();
        if (location == null) {
            return false; // Fail if location is null
        }

        // Set the world border to the player's current location and specified size
        worldBorder.setCenter(location);
        worldBorder.setSize(size);

        // Save the border information to the config
        plugin.getConfig().set("border." + world.getName() + ".center.x", location.getX());
        plugin.getConfig().set("border." + world.getName() + ".center.z", location.getZ());
        plugin.getConfig().set("border." + world.getName() + ".size", size);
        plugin.saveConfig();
        return true;
    }

    private double scaleBorderSize(World.Environment environment, int size) {
        return switch (environment) {
            case NORMAL -> size; // Overworld
            case NETHER -> size * 8; // Nether
            case THE_END -> size * 4; // End
            default -> size;
        };
    }
}
