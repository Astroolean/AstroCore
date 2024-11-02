package dev.astroolean.commands;

import dev.astroolean.AstroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class PlayerVaultCommand implements CommandExecutor, Listener {

    private static final int INVENTORY_SIZE = 54; // Vault size (double chest)
    private final HashMap<UUID, HashMap<Integer, Inventory>> playerVaults = new HashMap<>();
    private final AstroCore plugin;

    public PlayerVaultCommand(AstroCore plugin) {
        this.plugin = plugin;
        loadVaults(); // Load all vault data on plugin start
    }

    // Command execution for /pv command
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
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /pv [1|2]");
            return true;
        }

        int vaultNumber;
        try {
            vaultNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid vault number.");
            return true;
        }

        if (vaultNumber < 1 || vaultNumber > 2) {
            player.sendMessage(ChatColor.RED + "You can only access vault 1 or 2.");
            return true;
        }

        // Create or get the vault inventory
        playerVaults.putIfAbsent(player.getUniqueId(), new HashMap<>());
        HashMap<Integer, Inventory> vaultMap = playerVaults.get(player.getUniqueId());
        Inventory vault = vaultMap.computeIfAbsent(vaultNumber, id -> {
            String title = ChatColor.BLUE + "" + ChatColor.BOLD + "     >> " + ChatColor.AQUA + ChatColor.BOLD + "AstroVaults " + id + ChatColor.BLUE + ChatColor.BOLD + " <<";
            return Bukkit.createInventory(null, INVENTORY_SIZE, title);
        });

        player.openInventory(vault);
        return true;
    }

    // Save vault data when player quits or inventory closes
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveVault(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("Vault")) {
            Player player = (Player) event.getPlayer();
            saveVault(player.getUniqueId());
        }
    }

    // Load all vaults from file on plugin enable
    public void loadVaults() {
        File playerDataDir = new File(plugin.getDataFolder(), "PlayerData");
        if (!playerDataDir.exists()) playerDataDir.mkdirs();
    
        File[] playerFiles = playerDataDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                FileConfiguration vaultConfig = YamlConfiguration.loadConfiguration(playerFile);
                UUID playerId = UUID.fromString(playerFile.getName().replace(".yml", ""));
                HashMap<Integer, Inventory> vaults = new HashMap<>();
    
                for (String vaultNum : vaultConfig.getKeys(false)) {
                    int vaultNumber = Integer.parseInt(vaultNum);
                    
                    // Apply the same custom title as in `onCommand`
                    String title = ChatColor.BLUE + "" + ChatColor.BOLD + "     >> " + ChatColor.AQUA + ChatColor.BOLD + "AstroVaults " + vaultNumber + ChatColor.BLUE + ChatColor.BOLD + " <<";
                    Inventory vault = Bukkit.createInventory(null, INVENTORY_SIZE, title);
    
                    for (int i = 0; i < INVENTORY_SIZE; i++) {
                        ItemStack item = vaultConfig.getItemStack(vaultNum + "." + i);
                        vault.setItem(i, item);
                    }
                    vaults.put(vaultNumber, vault);
                }
                playerVaults.put(playerId, vaults);
                plugin.getLogger().log(Level.INFO, "Loaded vaults for player: {0}", playerId);
            }
        }
    }
    
    // Save all player vaults to file on plugin disable
    public void saveVaults() {
        for (UUID playerId : playerVaults.keySet()) {
            saveVault(playerId);
        }
    }

    // Save a specific player's vault data to file
    private void saveVault(UUID playerId) {
        HashMap<Integer, Inventory> vaults = playerVaults.get(playerId);
        if (vaults == null) return;

        File playerFile = new File(plugin.getDataFolder() + "/PlayerData", playerId + ".yml");
        FileConfiguration vaultConfig = YamlConfiguration.loadConfiguration(playerFile);

        for (Map.Entry<Integer, Inventory> vaultEntry : vaults.entrySet()) {
            int vaultNumber = vaultEntry.getKey();
            Inventory vault = vaultEntry.getValue();

            for (int i = 0; i < vault.getSize(); i++) {
                ItemStack item = vault.getItem(i);
                vaultConfig.set(vaultNumber + "." + i, item);
            }
        }

        try {
            vaultConfig.save(playerFile);
            plugin.getLogger().log(Level.INFO, "Saved vault for player: {0}", playerId);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save vault for player {0}: {1}", new Object[]{playerId, e.getMessage()});
        }
    }

    // Handle player join: load vault data if necessary
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (!playerVaults.containsKey(playerId)) {
            loadVaults();
        }
    }
}