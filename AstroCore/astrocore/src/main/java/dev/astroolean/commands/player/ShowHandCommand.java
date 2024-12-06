package dev.astroolean.commands.player;

import dev.astroolean.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShowHandCommand implements CommandExecutor {

    private final AstroCore plugin;

    public ShowHandCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true; // No action needed for null sender
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

        // Check if player has permission to use the command
        if (!player.hasPermission("astrocore.show")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Get the item in hand
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player is holding something
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding anything.");
            return true;
        }

        // Get item details
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED + "The item has no meta data.");
            return true;
        }

        String itemName = itemInHand.getType().name();

        // Check for custom model data (from the ItemMeta)
        if (meta.hasCustomModelData()) {
            itemName = meta.getCustomModelData() + " - " + itemInHand.getType().name();
        }

        String displayName = meta.hasDisplayName() ? meta.getDisplayName() : itemName;

        // Build message with item details
        StringBuilder message = new StringBuilder();
        message.append(ChatColor.GREEN).append("Item in hand: ").append(ChatColor.WHITE).append(displayName).append("\n");

        // Show lore if available
        if (meta.hasLore()) {
            message.append(ChatColor.GREEN).append("Lore: ").append(ChatColor.WHITE).append(String.join(", ", meta.getLore())).append("\n");
        }

        // Show enchantments if any
        Map<Enchantment, Integer> enchantments = meta.getEnchants();
        if (!enchantments.isEmpty()) {
            message.append(ChatColor.GREEN).append("Enchantments: ").append(ChatColor.WHITE);
            enchantments.forEach((enchantment, level) ->
                message.append(enchantment.getKey().getKey())
                       .append(" ").append(level)
                       .append(", ")
            );
            message.setLength(message.length() - 2);  // Remove last comma
            message.append("\n");
        }

        // Send the message to the player
        player.sendMessage(message.toString());
        return true;
    }
}
