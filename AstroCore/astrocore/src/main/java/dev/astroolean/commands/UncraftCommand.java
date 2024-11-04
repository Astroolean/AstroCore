package dev.astroolean.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class UncraftCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds
    @SuppressWarnings("*")
    private final JavaPlugin plugin;
    private final int experienceCost = 1000; // Experience cost for uncrafting

    public UncraftCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true;
        }
    
        if (!(plugin instanceof dev.astroolean.AstroCore)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }
    
        dev.astroolean.AstroCore myPlugin = (dev.astroolean.AstroCore) plugin;
    
        if (!myPlugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }
    
        if (!sender.hasPermission("astrocore.uncraft")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }
    
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }
    
        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeLeft = (cooldowns.get(player.getUniqueId()) + (cooldownTime * 1000)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000) + " seconds before using this command again.");
                return true;
            }
        }
    
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    
        if (args.length < 1 || !args[0].equalsIgnoreCase("hand")) {
            player.sendMessage(ChatColor.RED + "Usage: /uncraft hand");
            return true;
        }
    
        if (player.getTotalExperience() < experienceCost) {
            player.sendMessage(ChatColor.RED + "You need at least 1000 experience points to uncraft an item.");
            return true;
        }
    
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding any item.");
            return true;
        }

        // Uncraft items based on their type
        Material itemType = itemInHand.getType();
        switch (itemType) {
            // Tools
            case DIAMOND_PICKAXE -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Pickaxe into 3 Diamonds and 2 Sticks.");
            }
            case GOLDEN_PICKAXE -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Pickaxe into 3 Gold Ingots and 2 Sticks.");
            }
            case IRON_PICKAXE -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Pickaxe into 3 Iron Ingots and 2 Sticks.");
            }
            case STONE_PICKAXE -> {
                player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Stone Pickaxe into 3 Cobblestones and 2 Sticks.");
            }
            case WOODEN_PICKAXE -> {
                player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Wooden Pickaxe into 3 Wooden Planks and 2 Sticks.");
            }
            case DIAMOND_AXE -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Axe into 3 Diamonds and 2 Sticks.");
            }
            case GOLDEN_AXE -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Axe into 3 Gold Ingots and 2 Sticks.");
            }
            case IRON_AXE -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Axe into 3 Iron Ingots and 2 Sticks.");
            }
            case STONE_AXE -> {
                player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Stone Axe into 3 Cobblestones and 2 Sticks.");
            }
            case WOODEN_AXE -> {
                player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 3), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Wooden Axe into 3 Wooden Planks and 2 Sticks.");
            }
            case DIAMOND_SHOVEL -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Shovel into 1 Diamond and 2 Sticks.");
            }
            case GOLDEN_SHOVEL -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Shovel into 1 Gold Ingot and 2 Sticks.");
            }
            case IRON_SHOVEL -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Shovel into 1 Iron Ingot and 2 Sticks.");
            }
            case STONE_SHOVEL -> {
                player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 1), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Stone Shovel into 1 Cobblestone and 2 Sticks.");
            }
            case WOODEN_SHOVEL -> {
                player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 1), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Wooden Shovel into 1 Wooden Plank and 2 Sticks.");
            }
            case DIAMOND_HOE -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Hoe into 2 Diamonds and 2 Sticks.");
            }
            case GOLDEN_HOE -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 2), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Hoe into 2 Gold Ingots and 2 Sticks.");
            }
            case IRON_HOE -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 2), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Hoe into 2 Iron Ingots and 2 Sticks.");
            }
            case STONE_HOE -> {
                player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 2), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Stone Hoe into 2 Cobblestones and 2 Sticks.");
            }
            case WOODEN_HOE -> {
                player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 2), new ItemStack(Material.STICK, 2));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Wooden Hoe into 2 Wooden Planks and 2 Sticks.");
            }

            // Armor
            case DIAMOND_HELMET -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Helmet into 5 Diamonds.");
            }
            case DIAMOND_CHESTPLATE -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 8));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Chestplate into 8 Diamonds.");
            }
            case DIAMOND_LEGGINGS -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 7));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Diamond Leggings into 7 Diamonds.");
            }
            case DIAMOND_BOOTS -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 4));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Diamond Boots into 4 Diamonds.");
            }
            case GOLDEN_HELMET -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 5));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Helmet into 5 Gold Ingots.");
            }
            case GOLDEN_CHESTPLATE -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 8));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Chestplate into 8 Gold Ingots.");
            }
            case GOLDEN_LEGGINGS -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 7));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Golden Leggings into 7 Gold Ingots.");
            }
            case GOLDEN_BOOTS -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 4));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Golden Boots into 4 Gold Ingots.");
            }
            case IRON_HELMET -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 5));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Helmet into 5 Iron Ingots.");
            }
            case IRON_CHESTPLATE -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 8));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Chestplate into 8 Iron Ingots.");
            }
            case IRON_LEGGINGS -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 7));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Iron Leggings into 7 Iron Ingots.");
            }
            case IRON_BOOTS -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 4));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Iron Boots into 4 Iron Ingots.");
            }
            case CHAINMAIL_HELMET -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 5));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Chainmail Helmet into 5 Iron Ingots.");
            }
            case CHAINMAIL_CHESTPLATE -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 8));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Chainmail Chestplate into 8 Iron Ingots.");
            }
            case CHAINMAIL_LEGGINGS -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 7));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Chainmail Leggings into 7 Iron Ingots.");
            }
            case CHAINMAIL_BOOTS -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 4));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Chainmail Boots into 4 Iron Ingots.");
            }
            case LEATHER_HELMET -> {
                player.getInventory().addItem(new ItemStack(Material.LEATHER, 5));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Leather Helmet into 5 Leather.");
            }
            case LEATHER_CHESTPLATE -> {
                player.getInventory().addItem(new ItemStack(Material.LEATHER, 8));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Leather Chestplate into 8 Leather.");
            }
            case LEATHER_LEGGINGS -> {
                player.getInventory().addItem(new ItemStack(Material.LEATHER, 7));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Leather Leggings into 7 Leather.");
            }
            case LEATHER_BOOTS -> {
                player.getInventory().addItem(new ItemStack(Material.LEATHER, 4));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted Leather Boots into 4 Leather.");
            }

            // Weapons
            case DIAMOND_SWORD -> {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.STICK, 1));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Diamond Sword into 2 Diamonds and 1 Stick.");
            }
            case GOLDEN_SWORD -> {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 2), new ItemStack(Material.STICK, 1));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Golden Sword into 2 Gold Ingots and 1 Stick.");
            }
            case IRON_SWORD -> {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 2), new ItemStack(Material.STICK, 1));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted an Iron Sword into 2 Iron Ingots and 1 Stick.");
            }
            case STONE_SWORD -> {
                player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 2), new ItemStack(Material.STICK, 1));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Stone Sword into 2 Cobblestones and 1 Stick.");
            }
            case WOODEN_SWORD -> {
                player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 2), new ItemStack(Material.STICK, 1));
                player.sendMessage(ChatColor.GREEN + "You have uncrafted a Wooden Sword into 2 Wooden Planks and 1 Stick.");
            }
            // Continue adding more items...

            default -> {
                player.sendMessage(ChatColor.RED + "You cannot uncraft this item.");
                return true;
            }
        }

        // Remove the item from the player's inventory after uncrafting
        player.getInventory().remove(itemInHand);

        // Deduct experience points
        int newExp = player.getTotalExperience() - experienceCost;
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        player.giveExp(newExp);

        return true;
    }
}
