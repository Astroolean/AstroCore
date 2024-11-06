package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import dev.astroolean.AstroCore;

public class InvseeCommand implements CommandExecutor {
    private final AstroCore plugin;

    public InvseeCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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


        // Check if the sender has permission for the "invsee" command
        if (!sender.hasPermission("astrocore.invsee")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /invsee [player]");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or is not online.");
            return true;
        }

        // Check if the player has enough experience points
        int expNeeded = 500;
        int currentExp = player.getTotalExperience();
        if (currentExp < expNeeded) {
            player.sendMessage(ChatColor.RED + "You need at least " + expNeeded + " experience points to use this command.");
            return true;
        }

        // Deduct experience points
        player.setTotalExperience(currentExp - expNeeded);
        player.setExp(0); // Reset current exp to 0 for the visual bar
        player.setLevel(player.getLevel() - (expNeeded / 100)); // Adjust the level if needed

        // Inform the player
        player.sendMessage(ChatColor.GREEN + "You have spent " + expNeeded + " experience points to view " + targetPlayer.getName() + "'s inventory.");

        // Create and open the target player's inventory for the viewing player
        Inventory targetInventory = targetPlayer.getInventory();
        player.openInventory(targetInventory);

        // Set a metadata value to track the inventory viewing duration
        player.setMetadata("invsee", new FixedMetadataValue(plugin, System.currentTimeMillis()));

        // Schedule a task to close the inventory after 60 seconds
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && player.hasMetadata("invsee")) {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "You have finished viewing " + targetPlayer.getName() + "'s inventory.");
                player.removeMetadata("invsee", plugin); // Clean up metadata
            }
        }, 1200L); // 1200 ticks = 60 seconds

        return true;
    }
}
