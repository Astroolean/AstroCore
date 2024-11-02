package dev.astroolean.commands;

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

public class WeatherCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public WeatherCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        openWeatherGUI(player);
        return true;
    }

    private void openWeatherGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Select Weather");

        gui.setItem(11, createWeatherItem(Material.COMMAND_BLOCK, ChatColor.GREEN + "Sun"));
        gui.setItem(13, createWeatherItem(Material.COMMAND_BLOCK, ChatColor.YELLOW + "Rain"));
        gui.setItem(15, createWeatherItem(Material.COMMAND_BLOCK, ChatColor.BLUE + "Thunder"));

        fillEmptySlots(gui);
        player.openInventory(gui);
        setCooldown(player);
    }

    private boolean isOnCooldown(Player player) {
        Long lastUsed = cooldowns.get(player);
        if (lastUsed != null) {
            return (System.currentTimeMillis() - lastUsed) < 3000; // 3 seconds cooldown
        }
        return false; // No cooldown if the player is not in the map
    }

    private ItemStack createWeatherItem(Material material, String displayName) {
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
            meta.setDisplayName("");
            meta.setLore(null);
            blueGlass.setItemMeta(meta);
        }

        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, blueGlass);
            }
        }
    }

    private void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.BLUE + "Select Weather")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
    
            if (clickedItem != null && clickedItem.getType() == Material.COMMAND_BLOCK) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    Player player = (Player) event.getWhoClicked();
                    String displayName = meta.getDisplayName();
    
                    // Change the world's weather based on the clicked item
                    if (displayName.equals(ChatColor.GREEN + "Sun")) { // Corrected display name for sunny weather
                        player.getWorld().setWeatherDuration(0); // Clear weather
                        player.getWorld().setStorm(false); // Ensure storm is off
                        player.sendMessage(ChatColor.GREEN + "The weather has been set to sunny!");
                    } else if (displayName.equals(ChatColor.YELLOW + "Rain")) { // Corrected display name for rain
                        player.getWorld().setStorm(true);
                        player.getWorld().setWeatherDuration(Integer.MAX_VALUE); // Continuous rain
                        player.sendMessage(ChatColor.GREEN + "The weather has been set to rain!");
                    } else if (displayName.equals(ChatColor.BLUE + "Thunder")) {
                        player.getWorld().setStorm(true);
                        player.getWorld().setThundering(true);
                        player.getWorld().setWeatherDuration(Integer.MAX_VALUE); // Continuous thunderstorm
                        player.sendMessage(ChatColor.GREEN + "The weather has been set to a thunderstorm!");
                    }
    
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
