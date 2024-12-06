package dev.astroolean.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import dev.astroolean.AstroCore;

import java.util.EnumSet;
import java.util.Set;

public class XPCommand implements CommandExecutor, Listener {

    private static boolean xpEnabled = false;
    private final AstroCore plugin; // Reference to the main plugin

    private static final Set<Material> CROPS = EnumSet.of(
        // Basic Overworld Crops
        Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
        // Nether Crops
        Material.NETHER_WART,
        // Other Overworld Crops
        Material.MELON_STEM, Material.ATTACHED_MELON_STEM,
        Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM,
        Material.SUGAR_CANE, Material.BAMBOO,
        // Aquatic Crops
        Material.KELP, Material.KELP_PLANT,
        Material.SEA_PICKLE,
        // Suspicious Stew Ingredients
        Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
        Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS,
        // Newer Farming Additions
        Material.SWEET_BERRY_BUSH, Material.COCOA,
        Material.CHORUS_FLOWER, Material.CHORUS_PLANT
    );    

    public XPCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /xp [enable/disable]");
            return false;
        }

        if (!player.hasPermission("astrocore.morexp")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Ensure AstroCore is enabled before toggling XP
        if (!plugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        String action = args[0].toLowerCase();

        // Enable XP system
        if ("enable".equals(action)) {
            if (!xpEnabled) {
                xpEnabled = true;
                plugin.getServer().getPluginManager().registerEvents(this, plugin);
                player.sendMessage(ChatColor.GREEN + "XP system has been enabled.");
            } else {
                player.sendMessage(ChatColor.RED + "XP system is already enabled.");
            }
            return true;
        }

        // Disable XP system
        if ("disable".equals(action)) {
            if (xpEnabled) {
                xpEnabled = false;
                BlockBreakEvent.getHandlerList().unregister(this);
                player.sendMessage(ChatColor.RED + "XP system has been disabled.");
            } else {
                player.sendMessage(ChatColor.RED + "XP system is already disabled.");
            }
            return true;
        }

        // Invalid argument
        player.sendMessage(ChatColor.RED + "Usage: /xp [enable/disable]");
        return false;
    }

    // Listen for block breaks (crops or ores)
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!xpEnabled) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();

        // Check if the block is a crop
        if (CROPS.contains(material)) {
            player.giveExp(25); // Give 25 XP for crops
            return;
        }

        // Check if the block is an ore
        if (isOre(material)) {
            player.giveExp(100); // Give 100 XP for ores
        }
    }

    // Helper method to check if the block is an ore
    private boolean isOre(Material material) {
        return switch (material) {
            case COAL_ORE, DEEPSLATE_COAL_ORE,
                 IRON_ORE, DEEPSLATE_IRON_ORE,
                 GOLD_ORE, DEEPSLATE_GOLD_ORE,
                 DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE,
                 NETHER_QUARTZ_ORE, NETHER_GOLD_ORE,
                 ANCIENT_DEBRIS, COPPER_ORE, DEEPSLATE_COPPER_ORE -> true;
            default -> false;
        };
    }
}
