package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TrashCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final Map<Inventory, String> inventoryTitles = new HashMap<>(); // Store inventory and its title

    public TrashCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender has permission for the "trash" command
        if (!sender.hasPermission("astrocore.trash")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Check if the plugin is enabled
        if (!(plugin instanceof dev.astroolean.AstroCore)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }

        dev.astroolean.AstroCore myPlugin = (dev.astroolean.AstroCore) plugin;

        if (!myPlugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Create the trash GUI with a custom title
        String title = ChatColor.BLUE + "" + ChatColor.BOLD + "     >> " +
                       ChatColor.AQUA + ChatColor.BOLD + "AstroTrash " +
                       ChatColor.BLUE + ChatColor.BOLD + " <<";

        Inventory trashInventory = Bukkit.createInventory(null, 54, title); // 54 for double chest size
        inventoryTitles.put(trashInventory, title); // Associate inventory with its title

        // Open the GUI for the player
        player.openInventory(trashInventory);

        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        // Check if the inventory title matches the Trash title using our Map
        if (inventoryTitles.containsKey(inventory)) {
            // Clear the inventory when closed
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, null); // Remove all items
            }
        }
    }
}
