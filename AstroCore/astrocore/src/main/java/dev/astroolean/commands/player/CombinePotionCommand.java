package dev.astroolean.commands.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.astroolean.AstroCore;

public class CombinePotionCommand implements CommandExecutor, Listener {

    private final AstroCore plugin;

    public CombinePotionCommand(AstroCore plugin) {
        this.plugin = plugin;
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
    
        // Check if the plugin is enabled
        if (!plugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }
    
        // Check if the player has permission
        if (!player.hasPermission("astrocore.combine")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }
    
        // Get the item in the player's main hand
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
    
        // Check if the player is holding something
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding anything.");
            return true;
        }
    
        // Ensure the command has enough arguments
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Please enter the command correctly.");
            return true; // Do not continue with the command if the arguments are missing
        }
    
        // Check if the player has enough experience points (1000 exp)
        int expCost = 1000;
        if (getPlayerTotalExperience(player) < expCost) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to combine the item.");
            return true;
        }
    
        // Deduct experience from the player
        removeExperience(player, expCost);
    
        // Get the potion effect name
        String potionEffectName = args[1].toLowerCase();
        PotionEffectType potionEffectType = getPotionEffectType(potionEffectName);
    
        if (potionEffectType == null) {
            player.sendMessage(ChatColor.RED + "Invalid potion effect.");
            return false;
        }
    
        // Apply the potion effect to the item as lore
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta != null) {
            // Add lore to the item with the potion effect (without renaming the item)
            meta.setLore(Arrays.asList("Potion Effect: " + potionEffectName));
    
            // Add glow effect
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true); // Adds glow effect
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); // Hides the enchantment from the item
    
            itemInHand.setItemMeta(meta);
    
            player.sendMessage(ChatColor.GREEN + "Potion effect " + potionEffectName + " has been applied to your item");
        }
    
        return true;
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        // Check if the damager is a player
        if (event.getDamager() instanceof Player player) {
            // Check if the entity hit is a living entity (like mobs, players)
            Entity target = event.getEntity();
            if (target instanceof LivingEntity livingEntity) {
                // Get the item in the player's hand
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand.getType() != Material.AIR) {
                    // Check if the item has the potion effect applied in its lore
                    if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasLore()) {
                        // Find the potion effect from the item lore
                        String lore = itemInHand.getItemMeta().getLore().get(0); // Assuming first lore line contains potion effect
                        if (lore != null && lore.startsWith("Potion Effect: ")) {
                            String potionEffectName = lore.substring("Potion Effect: ".length()).toLowerCase();
                            PotionEffectType potionEffectType = getPotionEffectType(potionEffectName);

                            if (potionEffectType != null) {
                                // Apply the potion effect to the entity that was hit
                                livingEntity.addPotionEffect(new PotionEffect(potionEffectType, 100, 0)); // 100 ticks = 5 seconds
                                player.sendMessage("You applied " + potionEffectName + " to " + target.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper method to calculate total experience
    private int getPlayerTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperienceFromLevels(player.getLevel());
    }

    private int getExperienceFromLevels(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    // Helper method to remove experience
    private void removeExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);
        if (currentExp < amount) {
            return;
        }
        int newExp = currentExp - amount;
        int expToRemove = currentExp - newExp;
        player.giveExp(-expToRemove);
    }

    private PotionEffectType getPotionEffectType(String name) {
        return switch (name.toLowerCase()) {
            case "poison" -> PotionEffectType.POISON;
            case "weakness" -> PotionEffectType.WEAKNESS;
            case "strength" -> PotionEffectType.INCREASE_DAMAGE;
            case "speed" -> PotionEffectType.SPEED;
            case "regeneration" -> PotionEffectType.REGENERATION;
            case "slowness" -> PotionEffectType.SLOW;
            case "haste" -> PotionEffectType.FAST_DIGGING;
            case "mining_fatigue" -> PotionEffectType.SLOW_DIGGING;
            case "jump_boost" -> PotionEffectType.JUMP;
            case "nausea" -> PotionEffectType.CONFUSION;
            case "blindness" -> PotionEffectType.BLINDNESS;
            case "night_vision" -> PotionEffectType.NIGHT_VISION;
            case "hunger" -> PotionEffectType.HUNGER;
            case "wither" -> PotionEffectType.WITHER;
            case "health_boost" -> PotionEffectType.HEALTH_BOOST;
            case "absorption" -> PotionEffectType.ABSORPTION;
            case "saturation" -> PotionEffectType.SATURATION;
            case "levitation" -> PotionEffectType.LEVITATION;
            case "bad_omen" -> PotionEffectType.BAD_OMEN;
            case "resistance" -> PotionEffectType.DAMAGE_RESISTANCE;
            case "fire_resistance" -> PotionEffectType.FIRE_RESISTANCE;
            case "water_breathing" -> PotionEffectType.WATER_BREATHING;
            case "invisibility" -> PotionEffectType.INVISIBILITY;
            case "slow_falling" -> PotionEffectType.SLOW_FALLING;
            default -> null;
        };
    }
}
