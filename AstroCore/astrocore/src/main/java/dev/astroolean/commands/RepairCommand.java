package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import dev.astroolean.AstroCore;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class RepairCommand implements CommandExecutor {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds

// Set of damageable item types
private static final Set<Material> DAMAGEABLE_MATERIALS = Set.of(
        // Swords
        Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
        Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
        
        // Pickaxes
        Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
        Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,

        // Axes
        Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
        Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,

        // Shovels
        Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
        Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,

        // Hoes
        Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
        Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,

        // Bows
        Material.BOW,

        // Crossbows
        Material.CROSSBOW,

        // Tridents
        Material.TRIDENT,

        // Fishing Rods
        Material.FISHING_ROD,

        // Armor (all types)
        Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
        Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
        Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
        Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
        Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,

        // Other damageable items
        Material.SHEARS // Shears
);

    @SuppressWarnings("*")
    private final AstroCore plugin; // Store the reference to the plugin instance

    public RepairCommand(AstroCore astroCore) {
        this.plugin = astroCore; // Initialize the plugin reference
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

        // Check for cooldown
        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeLeft = (cooldowns.get(player.getUniqueId()) + (cooldownTime * 1000)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000) + " seconds before using this command again.");
                return true;
            }
        }

        // Set the cooldown
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /fix [hand|all]");
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "hand" -> fixItem(player);
            case "all" -> fixAll(player);
            default -> {
                player.sendMessage(ChatColor.RED + "Usage: /fix [hand|all]");
                yield true;
            }
        };
    }

    private boolean fixItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the item is AIR
        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must hold an item to repair it.");
            return true;
        }

        if (player.getTotalExperience() < 1000) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to repair this item.");
            return true;
        }

        // Repair the item using ItemMeta
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(0); // Reset damage
            item.setItemMeta(meta);
            player.setTotalExperience(player.getTotalExperience() - 1000); // Deduct experience
            player.sendMessage(ChatColor.GREEN + "You have repaired your " + item.getType().toString().toLowerCase().replace('_', ' ') + " for 1000 experience.");
        } else {
            player.sendMessage(ChatColor.RED + "This item cannot be repaired.");
        }
        return true;
    }

    private boolean fixAll(Player player) {
        Inventory inventory = player.getInventory();
        int itemsRepairedCount = 0; // Count how many items are repaired
    
        // Loop through the player's inventory
        for (ItemStack item : inventory.getContents()) {
            if (item != null && DAMAGEABLE_MATERIALS.contains(item.getType())) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof Damageable damageable) {
                    // Check if the item is damaged
                    if (damageable.getDamage() > 0) {
                        damageable.setDamage(0); // Reset damage
                        item.setItemMeta(meta); // Update item with repaired meta
                        itemsRepairedCount++; // Increment the repaired items count
                    }
                }
            }
        }
    
        if (itemsRepairedCount == 0) {
            player.sendMessage(ChatColor.RED + "You have no items to repair.");
            return true;
        }
    
        // Set total cost for fixing all items to a flat 10,000 experience
        int totalCost = 10000;
    
        if (player.getTotalExperience() < totalCost) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to repair all items. You need " + totalCost + " experience.");
            return true;
        }
    
        player.setTotalExperience(player.getTotalExperience() - totalCost); // Deduct total experience
        player.sendMessage(ChatColor.GREEN + "You have repaired " + itemsRepairedCount + " items in your inventory for " + totalCost + " experience.");
        return true;
    }    
}