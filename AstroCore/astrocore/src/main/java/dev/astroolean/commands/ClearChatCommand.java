package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ClearChatCommand implements CommandExecutor {
    private final JavaPlugin plugin; // Store a reference to the JavaPlugin
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int clearMessageCount = 100; // Number of messages to send to clear chat
    private final int cooldownTime = 3; // Cooldown time in seconds

    public ClearChatCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender has permission for the "sethome" command
        if (!sender.hasPermission("astrocore.clearChat")) {
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

        // Notify all players that the chat is being cleared
        String clearMessage = ChatColor.GREEN + player.getName() + " has cleared the chat!";
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(clearMessage);
        }

        // Clear the chat with animation
        new BukkitRunnable() {
            int messagesSent = 0;

            @Override
            public void run() {
                if (messagesSent < clearMessageCount) {
                    Bukkit.broadcastMessage(""); // Send empty messages to clear the chat
                    messagesSent++;
                } else {
                    this.cancel(); // Stop the task
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick

        // Log the action
        Bukkit.getLogger().log(Level.INFO, "{0} cleared the chat.", player.getName());

        // Feedback message to the player
        player.sendMessage(ChatColor.GREEN + "Chat cleared successfully!");

        return true; // Command executed successfully
    }
}