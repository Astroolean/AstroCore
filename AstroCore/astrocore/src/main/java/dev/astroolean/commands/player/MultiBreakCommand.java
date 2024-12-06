package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import dev.astroolean.AstroCore;

import java.util.HashMap;
import java.util.Map;

public class MultiBreakCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;
    private final Map<Player, Boolean> multiBreakEnabled = new HashMap<>();
    @SuppressWarnings("unused")
    private final int expCostPerMinute = 500;

    public MultiBreakCommand(AstroCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true; // No action needed for null sender
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
            
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
    
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Usage: /multibreak");
            return true;
        }
    
        // Check if player has the permission to use the command
        if (!player.hasPermission("astrocore.multibreak")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }
    
        // Check for enough experience
        int expNeeded = 500;
        if (getPlayerTotalExperience(player) < expNeeded) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to enable MultiBreak.");
            return true;
        }
    
        if (multiBreakEnabled.getOrDefault(player, false)) {
            multiBreakEnabled.put(player, false);
            player.sendMessage(ChatColor.RED + "MultiBreak disabled.");
            return true;
        } else {
            // Enable MultiBreak
            multiBreakEnabled.put(player, true);
            player.sendMessage(ChatColor.GREEN + "MultiBreak enabled. You will lose " + expNeeded + " XP every minute.");
    
            // Deduct experience every minute
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Check if the player still has enough experience to continue using MultiBreak
                    if (!multiBreakEnabled.getOrDefault(player, false) || getPlayerTotalExperience(player) < expNeeded) {
                        multiBreakEnabled.put(player, false);
                        player.sendMessage(ChatColor.RED + "You no longer have enough experience. MultiBreak disabled.");
                        cancel();
                        return;
                    }
    
                    removeExperience(player, expNeeded);
                }
            }.runTaskTimer(plugin, 0L, 1200L); // Runs every 60 seconds
    
            return true;
        }
    }
    

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (multiBreakEnabled.getOrDefault(player, false)) {
            Block block = event.getBlock();
    
            // Break surrounding blocks in a 3x3x3 area
            for (int x = -1; x <= 1; x++) {  // Length: -1 to 1 (3 blocks wide)
                for (int y = -1; y <= 1; y++) {  // Depth: -1 to 1 (3 blocks deep vertically)
                    for (int z = -1; z <= 1; z++) {  // Depth: -1 to 1 (3 blocks deep horizontally)
                        // Skip the original block itself (the one the player broke)
                        if (x == 0 && y == 0 && z == 0) continue;
    
                        Block relativeBlock = block.getRelative(x, y, z);
                        
                        // Debugging: Output the block positions being checked
                        System.out.println("Checking block: " + relativeBlock.getLocation());
    
                        // Only break the block if it's not air
                        if (relativeBlock.getType() != Material.AIR) {
                            relativeBlock.breakNaturally(player.getInventory().getItemInMainHand());
                        }
                    }
                }
            }
        }
    }
    
    
    // Helper methods (getPlayerTotalExperience, removeExperience) similar to your ExpFlyCommand
    private int getPlayerTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperienceFromLevels(player.getLevel());
    }

    private int getExperienceFromLevels(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    private void removeExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);
        if (currentExp < amount) {
            return;
        }
        int newExp = currentExp - amount;
        int expToRemove = currentExp - newExp;
        player.giveExp(-expToRemove);
    }
}
