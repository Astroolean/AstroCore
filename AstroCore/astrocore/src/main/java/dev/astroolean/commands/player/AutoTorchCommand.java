package dev.astroolean.commands.player;

import dev.astroolean.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class AutoTorchCommand implements CommandExecutor {

    private final AstroCore plugin;
    private final Set<Player> activePlayers = new HashSet<>();

    public AutoTorchCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Check if the plugin is enabled
        if (!plugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // Check if the player has permission to use the command
        if (!player.hasPermission("astrocore.autotorch")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Handle command arguments
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /autotorch [enable/disable]");
            return false;
        }

        String action = args[0].toLowerCase();

        // Enable Auto-Torch functionality
        if ("enable".equals(action)) {
            if (activePlayers.contains(player)) {
                player.sendMessage(ChatColor.RED + "Auto-Torch is already enabled.");
                return true;
            }

            // Start auto-torching if the player has torches in their inventory
            if (hasTorchesInInventory(player)) {
                activePlayers.add(player);
                startAutoTorching(player);
                player.sendMessage(ChatColor.GREEN + "Auto-Torch has been enabled.");
            } else {
                player.sendMessage(ChatColor.RED + "You need at least one torch in your inventory.");
            }
            return true;
        }

        // Disable Auto-Torch functionality
        if ("disable".equals(action)) {
            if (!activePlayers.contains(player)) {
                player.sendMessage(ChatColor.RED + "Auto-Torch is not enabled.");
                return true;
            }

            // Stop auto-torching
            activePlayers.remove(player);
            player.sendMessage(ChatColor.RED + "Auto-Torch has been disabled.");
            return true;
        }

        // Invalid argument
        player.sendMessage(ChatColor.RED + "Usage: /autotorch [enable/disable]");
        return false;
    }

    // Start the auto-torching process for a player
    private void startAutoTorching(Player player) {
        // Schedule the task to place torches at regular intervals (every 5 seconds for example)
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!activePlayers.contains(player)) return; // If the player disables it, stop checking

            // Get the player's location and check nearby blocks
            Block block = player.getLocation().getBlock();

            // Check surrounding blocks, use the block directly beneath the player
            Block targetBlock = block.getRelative(BlockFace.DOWN);

            // If the light level is below the threshold (e.g., 8)
            if (targetBlock.getLightLevel() < 4 && targetBlock.getType() == Material.AIR) {
                // Check if the player has torches in inventory
                if (hasTorchesInInventory(player)) {
                    // Place a torch on the block
                    targetBlock.setType(Material.TORCH);
                    removeTorchFromInventory(player);
                }
            }
        }, 0L, 1L);
    }

    // Helper method to check if the player has torches in their inventory
    private boolean hasTorchesInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.TORCH && item.getAmount() > 0) {
                return true;
            }
        }
        return false;
    }

    // Helper method to remove a torch from the player's inventory
    private void removeTorchFromInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.TORCH && item.getAmount() > 0) {
                if (item.getAmount() == 1) {
                    player.getInventory().remove(item);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
                break;
            }
        }
    }
}
