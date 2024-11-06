package dev.astroolean.commands;

import dev.astroolean.AstroCore;  // Import your main plugin class
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import java.util.List;

public class ExplosionCommand implements CommandExecutor, Listener {

    private static boolean explosionsEnabled = true;  // Default to enabled
    private final AstroCore plugin;  // Reference to the main plugin class

    // Constructor that accepts the plugin instance
    public ExplosionCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    // Command handler for /explosion [enable/disable]
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

        // Check if the sender has permission for the "sethome" command
        if (!sender.hasPermission("astrocore.explosion")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        if (args.length == 1) {
            String action = args[0].toLowerCase();

            switch (action) {
                case "enable" -> {
                    explosionsEnabled = true;
                    sender.sendMessage(ChatColor.GREEN + "Explosions are now enabled. Blocks will be affected.");
                }
                case "disable" -> {
                    explosionsEnabled = false;
                    sender.sendMessage(ChatColor.RED + "Explosions are now disabled. Blocks will not be affected.");
                }
                default -> {
                    return false;  // Invalid argument
                }
            }

            return true;
        }

        return false;
    }

    // Method to check if explosions are enabled
    public static boolean areExplosionsEnabled() {
        return explosionsEnabled;
    }

    // Handle TNT and Creeper explosions to prevent block damage
    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if (!areExplosionsEnabled()) {
            // Get the list of blocks affected by the explosion
            List<Block> blocks = event.blockList();

            // Cancel block damage (but keep the explosion effect)
            blocks.clear();
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (!areExplosionsEnabled()) {
            // Prevent blocks from breaking by disabling block damage, but still allow explosion
            event.setCancelled(false);  // This keeps the explosion from being cancelled
        }
    }
}
