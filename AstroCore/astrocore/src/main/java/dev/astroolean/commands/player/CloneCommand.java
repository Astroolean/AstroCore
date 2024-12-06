package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import dev.astroolean.AstroCore;

public class CloneCommand implements CommandExecutor {

    @SuppressWarnings("unused")
    private final AstroCore plugin;

    public CloneCommand(AstroCore plugin) {
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

        // Check if the player has permission
        if (!player.hasPermission("astrocore.clone")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Check if the player has enough experience points (1000 exp)
        int expCost = 1000;
        if (getPlayerTotalExperience(player) < expCost) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to clone the item.");
            return true;
        }

        // Deduct experience from the player
        removeExperience(player, expCost);

        // Get the item in the player's main hand
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player is holding something
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding anything.");
            return true;
        }

        // Clone the item (create a new ItemStack with the same properties)
        ItemStack clonedItem = itemInHand.clone();

        // Give the cloned item to the player
        player.getInventory().addItem(clonedItem);
        player.sendMessage(ChatColor.GREEN + "You have cloned the item in your hand!");

        return true;
    }

    // Helper method to calculate total experience
    private int getPlayerTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperienceFromLevels(player.getLevel());
    }

    private int getExperienceFromLevels(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    // Helper method to remove experience
    private void removeExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);
        if (currentExp < amount) {
            return;
        }
        int newExp = currentExp - amount;
        int expToRemove = currentExp - newExp;
        player.giveExp(-expToRemove);
    }
}
