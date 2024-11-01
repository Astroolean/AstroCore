package dev.astroolean;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class TimeCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin; // Store a reference to the JavaPlugin
    private final Map<Player, Long> cooldowns = new HashMap<>(); // Cooldown map

    public TimeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin); // Register the listener
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if the player is an operator
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check for cooldown
        if (isOnCooldown(player)) {
            player.sendMessage(ChatColor.RED + "You must wait before using this command again.");
            return true;
        }

        // Open the GUI
        openTimeGUI(player);
        return true;
    }

    private void openTimeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Select Time of Day");

        // Set items for time of day
        gui.setItem(10, createTimeItem(Material.COMMAND_BLOCK, ChatColor.GREEN + "Day"));
        gui.setItem(12, createTimeItem(Material.COMMAND_BLOCK, ChatColor.YELLOW + "Noon"));
        gui.setItem(14, createTimeItem(Material.COMMAND_BLOCK, ChatColor.RED + "Night"));
        gui.setItem(16, createTimeItem(Material.COMMAND_BLOCK, ChatColor.LIGHT_PURPLE + "Midnight"));

        // Fill empty slots with blue glass panes
        fillEmptySlots(gui);

        player.openInventory(gui);
        setCooldown(player); // Set cooldown when opening the GUI
    }

    private ItemStack createTimeItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void fillEmptySlots(Inventory gui) {
        ItemStack blueGlass = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = blueGlass.getItemMeta();
        if (meta != null) {
            // Clear display name and lore to show nothing on hover
            meta.setDisplayName(""); // Set display name to empty
            meta.setLore(null); // Remove lore
            blueGlass.setItemMeta(meta); // Apply changes to the item
        }
    
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, blueGlass);
            }
        }
    }

    private boolean isOnCooldown(Player player) {
        Long lastUsed = cooldowns.get(player);
        if (lastUsed != null) {
            return (System.currentTimeMillis() - lastUsed) < 3000; // 3 seconds cooldown
        }
        return false; // No cooldown if the player is not in the map
    }

    private void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis()); // Store the current time
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.BLUE + "Select Time of Day")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.COMMAND_BLOCK) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    Player player = (Player) event.getWhoClicked();
                    String displayName = meta.getDisplayName();

                    // Change the world's time based on the clicked item
                    if (displayName.equals(ChatColor.GREEN + "Day")) {
                        player.getWorld().setTime(1000); // Day
                    } else if (displayName.equals(ChatColor.YELLOW + "Noon")) {
                        player.getWorld().setTime(6000); // Noon
                    } else if (displayName.equals(ChatColor.RED + "Night")) {
                        player.getWorld().setTime(13000); // Night
                    } else if (displayName.equals(ChatColor.LIGHT_PURPLE + "Midnight")) {
                        player.getWorld().setTime(18000); // Midnight
                    }
    
                    player.sendMessage(ChatColor.GREEN + "The time has been changed!");
                    player.closeInventory();
                }
            }
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<Player, Long> getCooldowns() {
        return cooldowns;
    }
}
