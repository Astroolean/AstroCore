package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class ShowCoordsCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds
    @SuppressWarnings("*")
    private final JavaPlugin plugin;

    public ShowCoordsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("astrocore.showcoords")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (plugin instanceof dev.astroolean.AstroCore astroCore && !astroCore.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
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

        // Get player coordinates
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        // Broadcast coordinates to all players
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " is at coordinates: " +
                ChatColor.AQUA + "X: " + x + ", Y: " + y + ", Z: " + z);

        return true;
    }
}
