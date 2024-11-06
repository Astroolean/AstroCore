package dev.astroolean.commands;

import dev.astroolean.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutoToolCommand implements CommandExecutor, Listener {
    @SuppressWarnings("unused")
    private final AstroCore plugin;
    private final Map<UUID, Boolean> playerAutoToolStatus = new HashMap<>();

    public AutoToolCommand(AstroCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("astrocore.autotool")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /autotool [enable/disable]");
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            playerAutoToolStatus.put(player.getUniqueId(), true);
            player.sendMessage(ChatColor.GREEN + "Auto tool is now enabled.");
        } else if (args[0].equalsIgnoreCase("disable")) {
            playerAutoToolStatus.put(player.getUniqueId(), false);
            player.sendMessage(ChatColor.RED + "Auto tool is now disabled.");
        } else {
            player.sendMessage(ChatColor.RED + "Invalid argument. Use 'enable' or 'disable'.");
        }

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (playerAutoToolStatus.getOrDefault(player.getUniqueId(), false) &&
            (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null) {
                checkAndEquipBestTool(player, event.getClickedBlock().getType());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (playerAutoToolStatus.getOrDefault(player.getUniqueId(), false)) {
            checkAndEquipBestTool(player, event.getBlock().getType());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (playerAutoToolStatus.getOrDefault(player.getUniqueId(), false)) {
            checkAndEquipBestTool(player, event.getBlock().getType());
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player &&
            playerAutoToolStatus.getOrDefault(player.getUniqueId(), false)) {
            checkAndEquipBestTool(player, null); // Determine context if needed
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (playerAutoToolStatus.getOrDefault(player.getUniqueId(), false) &&
            isToolItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot drop tools while Auto Tool is enabled.");
        }
    }

    public void checkAndEquipBestTool(Player player, Material blockType) {
        if (!playerAutoToolStatus.getOrDefault(player.getUniqueId(), false)) return;
    
        PlayerInventory playerInventory = player.getInventory();
        ItemStack bestTool = null;
    
        // Find the best tool in the inventory for the given block type
        for (ItemStack item : playerInventory.getContents()) {
            if (item == null || item.getType() == Material.AIR || !isToolItem(item)) continue;
    
            if (isEffectiveTool(item, blockType) &&
                (bestTool == null || getToolEfficiency(item, blockType) > getToolEfficiency(bestTool, blockType))) {
                bestTool = item;
            }
        }
    
        // If a best tool was found and it is not already in the main hand
        if (bestTool != null && !bestTool.isSimilar(playerInventory.getItemInMainHand())) {
            // Clone the best tool to equip it
            ItemStack equippedTool = bestTool.clone();
    
            // Remove the original best tool from the inventory
            int bestToolIndex = playerInventory.first(bestTool.getType());
            if (bestToolIndex != -1) {
                playerInventory.setItem(bestToolIndex, null); // Remove the original tool
            }
    
            // Equip the cloned tool
            playerInventory.setItemInMainHand(equippedTool);
            player.sendMessage(ChatColor.GREEN + "You have equipped: " + equippedTool.getType().toString().toLowerCase().replace("_", " "));
        }
    }
    

    private boolean isToolItem(ItemStack item) {
        return item.getType().toString().toLowerCase().contains("pickaxe") ||
               item.getType().toString().toLowerCase().contains("axe") ||
               item.getType().toString().toLowerCase().contains("shovel") ||
               item.getType().toString().toLowerCase().contains("hoe");
    }

    private boolean isEffectiveTool(ItemStack tool, Material blockType) {
        if (tool == null || blockType == null) return false;
    
        Material toolType = tool.getType();
    
        if (isPickaxe(toolType)) {
            return isOreOrStone(blockType);
        } else if (isShovel(toolType)) {
            return isDirtOrSand(blockType);
        } else if (isAxe(toolType)) {
            return isWoodOrLog(blockType);
        } else if (isHoe(toolType)) {
            return isCrops(blockType);
        }
    
        return false;
    }
    
    private int getToolEfficiency(ItemStack tool, Material blockType) {
        if (tool == null || blockType == null) return 0;
    
        Material toolType = tool.getType();
    
        if (isPickaxe(toolType)) {
            return isOreOrStone(blockType) ? 2 : 1;
        } else if (isShovel(toolType)) {
            return isDirtOrSand(blockType) ? 2 : 1;
        } else if (isAxe(toolType)) {
            return isWoodOrLog(blockType) ? 2 : 1;
        } else if (isHoe(toolType)) {
            return isCrops(blockType) ? 2 : 1;
        }
    
        return 0;
    }
    
    // Helper methods for clarity
    private boolean isPickaxe(Material material) {
        return material == Material.WOODEN_PICKAXE || material == Material.STONE_PICKAXE ||
               material == Material.IRON_PICKAXE || material == Material.GOLDEN_PICKAXE ||
               material == Material.DIAMOND_PICKAXE || material == Material.NETHERITE_PICKAXE;
    }
    
    private boolean isShovel(Material material) {
        return material == Material.WOODEN_SHOVEL || material == Material.STONE_SHOVEL ||
               material == Material.IRON_SHOVEL || material == Material.GOLDEN_SHOVEL ||
               material == Material.DIAMOND_SHOVEL || material == Material.NETHERITE_SHOVEL;
    }
    
    private boolean isAxe(Material material) {
        return material == Material.WOODEN_AXE || material == Material.STONE_AXE ||
               material == Material.IRON_AXE || material == Material.GOLDEN_AXE ||
               material == Material.DIAMOND_AXE || material == Material.NETHERITE_AXE;
    }
    
    private boolean isHoe(Material material) {
        return material == Material.WOODEN_HOE || material == Material.STONE_HOE ||
               material == Material.IRON_HOE || material == Material.GOLDEN_HOE ||
               material == Material.DIAMOND_HOE || material == Material.NETHERITE_HOE;
    }
    
    private boolean isOreOrStone(Material blockType) {
        return blockType == Material.DIAMOND_ORE || blockType == Material.GOLD_ORE ||
               blockType == Material.IRON_ORE || blockType == Material.COAL_ORE ||
               blockType == Material.NETHER_QUARTZ_ORE || blockType == Material.EMERALD_ORE ||
               blockType == Material.LAPIS_ORE || blockType == Material.REDSTONE_ORE ||
               blockType == Material.STONE || blockType == Material.GRANITE ||
               blockType == Material.DIORITE || blockType == Material.ANDESITE ||
               blockType == Material.COBBLESTONE || blockType == Material.SMOOTH_STONE;
    }
    
    private boolean isDirtOrSand(Material blockType) {
        return blockType == Material.GRASS_BLOCK || blockType == Material.DIRT ||
               blockType == Material.SAND || blockType == Material.RED_SAND ||
               blockType == Material.PODZOL || blockType == Material.MYCELIUM ||
               blockType == Material.CLAY || blockType == Material.GRAVEL ||
               blockType == Material.SOUL_SAND;
    }
    
    private boolean isWoodOrLog(Material blockType) {
        return blockType == Material.OAK_LOG || blockType == Material.BIRCH_LOG ||
               blockType == Material.SPRUCE_LOG || blockType == Material.JUNGLE_LOG ||
               blockType == Material.ACACIA_LOG || blockType == Material.DARK_OAK_LOG ||
               blockType == Material.STRIPPED_OAK_LOG || blockType == Material.STRIPPED_BIRCH_LOG ||
               blockType == Material.STRIPPED_SPRUCE_LOG || blockType == Material.STRIPPED_JUNGLE_LOG ||
               blockType == Material.STRIPPED_ACACIA_LOG || blockType == Material.STRIPPED_DARK_OAK_LOG ||
               blockType == Material.OAK_WOOD || blockType == Material.BIRCH_WOOD ||
               blockType == Material.SPRUCE_WOOD || blockType == Material.JUNGLE_WOOD ||
               blockType == Material.ACACIA_WOOD || blockType == Material.DARK_OAK_WOOD;
    }
    
    private boolean isCrops(Material blockType) {
        return blockType == Material.WHEAT || blockType == Material.CARROTS ||
               blockType == Material.POTATOES || blockType == Material.BEETROOTS ||
               blockType == Material.NETHER_WART;
    }    
}
