package dev.astroolean.commands.player;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import dev.astroolean.AstroCore;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InfiniteCommand implements CommandExecutor, Listener {
    @SuppressWarnings("*")
    private final JavaPlugin plugin;
    private final Map<Player, Long> lastUseTime = new HashMap<>();
    private static final long COOLDOWN_TIME = 5000; // 5000 milliseconds cooldown to prevent spamming

    public InfiniteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private int getPlayerTotalExperience(Player player) {
        int level = player.getLevel();
        int expForLevel;

        // Calculate XP from previous levels
        if (level <= 16) {
            expForLevel = level * level + 6 * level;
        } else if (level <= 31) {
            expForLevel = (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            expForLevel = (int) (4.5 * level * level - 162.5 * level + 2220);
        }

        // Add progress in the current level
        expForLevel += Math.round(player.getExp() * player.getExpToLevel());
        return expForLevel;
    }

    private void removeExperience(Player player, int amount) {
        int totalExp = getPlayerTotalExperience(player);
        // Ensure the total experience is greater than or equal to the amount to deduct
        if (totalExp < amount) {
            player.sendMessage(ChatColor.RED + "Not enough XP to deduct.");
            return;
        }

        // Deduct the experience safely
        int newExp = totalExp - amount;
        // Reset the player's experience and set the new experience points
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        // Re-add the remaining experience to the player
        player.giveExp(newExp);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Check if the plugin is enabled using the custom method
        AstroCore corePlugin = (AstroCore) plugin;
        if (!corePlugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }
    
        // Check for correct permissions
        if (!player.hasPermission("astrocore.infinite")) {
            player.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
    
        // Validate the number of arguments
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /infinite [water/lava]");
            return true; // Only show usage once and exit
        }
    
        String type = args[0].toLowerCase();
    
        // Validate the bucket type
        if (!type.equals("water") && !type.equals("lava")) {
            player.sendMessage(ChatColor.RED + "Invalid argument. Use 'water' or 'lava'.");
            return true; // Immediately exit after showing the error
        }
    
        // Set the cost for the infinite bucket
        int cost = 1000; // Both buckets cost 1000 XP
    
        // Check if the player has enough experience
        if (getPlayerTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You don't have enough experience for this command.");
            return true; // Exit after showing error message
        }
    
        // Remove the XP cost from the player
        removeExperience(player, cost);
    
        // Create the infinite water or lava bucket
        ItemStack bucket = (type.equals("water")) ? new ItemStack(Material.WATER_BUCKET) : new ItemStack(Material.LAVA_BUCKET);
    
        // Add custom metadata to mark the bucket as infinite
        ItemMeta meta = bucket.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Infinite " + type.substring(0, 1).toUpperCase() + type.substring(1) + " Bucket");
            meta.setLore(List.of(ChatColor.GRAY + "This bucket is infinite!"));
            meta.getPersistentDataContainer().set(new NamespacedKey("astrocore", "infinite"), PersistentDataType.BYTE, (byte) 1);
            bucket.setItemMeta(meta);
        }
    
        // Give the bucket to the player
        player.getInventory().addItem(bucket);
        player.sendMessage(ChatColor.GREEN + "You have purchased an infinite " + type + " bucket!");
    
        return true;
    }    

    @EventHandler
    public void onPlayerUseBucket(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        long currentTime = System.currentTimeMillis();
    
        // Prevent spamming by checking cooldown
        if (lastUseTime.containsKey(player) && currentTime - lastUseTime.get(player) < COOLDOWN_TIME) {
            return;  // Ignore if it's too soon to use again
        }
    
        lastUseTime.put(player, currentTime);  // Update last use time
    
        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey("astrocore", "infinite"), PersistentDataType.BYTE)) {
                // This is an infinite bucket
                if (item.getType() == Material.WATER_BUCKET || item.getType() == Material.LAVA_BUCKET) {
                    Block block = event.getClickedBlock();
    
                    if (block != null) {
                        Block blockToPlace = block.getRelative(BlockFace.UP);  // Get the block above where the player clicked
    
                        // Place the water or lava block in the correct position
                        if (item.getType() == Material.WATER_BUCKET) {
                            blockToPlace.setType(Material.WATER);
                        } else if (item.getType() == Material.LAVA_BUCKET) {
                            blockToPlace.setType(Material.LAVA);
                        }
    
                        // Cancel the event to prevent the bucket from being used normally (consumed)
                        event.setCancelled(true);
    
                        // Refresh the item meta to maintain the "infinite" metadata after spam
                        ItemMeta newMeta = item.getItemMeta();
                        if (newMeta != null) {
                            // Ensure persistent data is kept even after the event is canceled
                            newMeta.getPersistentDataContainer().set(new NamespacedKey("astrocore", "infinite"), PersistentDataType.BYTE, (byte) 1);
                            item.setItemMeta(newMeta);
                        }
    
                        // Restore the infinite bucket to the player's hand
                        player.getInventory().setItemInMainHand(item);
                    }
                }
            }
        }
    }    
}    
