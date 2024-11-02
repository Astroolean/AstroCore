package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RenameCommand implements CommandExecutor {
    @SuppressWarnings("*")
    private final JavaPlugin plugin;

    public RenameCommand(JavaPlugin plugin) {
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

        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Handle the rename or lore command based on command name
        if (command.getName().equalsIgnoreCase("rename")) {
            return handleRename(player, args);
        } else if (command.getName().equalsIgnoreCase("lore")) {
            return handleLore(player, args);
        }

        return false;
    }

    private boolean handleRename(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Usage: /rename <name>");
            return true;
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (String arg : args) {
            nameBuilder.append(arg).append(" ");
        }
        String newName = nameBuilder.toString().trim();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.AIR) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(formatColor(newName));
                itemInHand.setItemMeta(meta);
                player.sendMessage("Item renamed to: " + formatColor(newName)); // Show colored name
            }
        } else {
            player.sendMessage("You are not holding any item to rename.");
        }
        return true;
    }

    private boolean handleLore(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /lore <line> <text>");
            return true;
        }

        int line;
        try {
            line = Integer.parseInt(args[0]);
            if (line < 1 || line > 3) {
                player.sendMessage("Lore line must be 1, 2, or 3.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid line number. Please enter 1, 2, or 3.");
            return true;
        }

        StringBuilder loreBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            loreBuilder.append(args[i]).append(" ");
        }
        String loreText = loreBuilder.toString().trim();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.AIR) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                // Ensure the list is long enough
                while (lore.size() < 3) {
                    lore.add("");
                }

                lore.set(line - 1, formatColor(loreText)); // Set the lore for the selected line
                meta.setLore(lore);
                itemInHand.setItemMeta(meta);
                player.sendMessage("Lore line " + line + " set to: " + formatColor(loreText)); // Show colored lore
            }
        } else {
            player.sendMessage("You are not holding any item to add lore.");
        }
        return true;
    }

    // Utility method to format text with color codes
    private String formatColor(String text) {
        return text.replaceAll("&", "§"); // Replace '&' with the Minecraft color character
    }
}
