package dev.astroolean;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeedCommand implements CommandExecutor {
    private final Plugin plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final int effectDurationSeconds = 3600; // 1 hour in seconds

    public WeedCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            }
            return true;
        }

        // Check if the plugin is enabled
        if (!plugin.isEnabled()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // Full hour cooldown check
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long timeLeft = (cooldowns.get(playerUUID) + (effectDurationSeconds * 1000)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000 / 60) + " minutes before using this command again.");
                return true;
            }
        }

        // Check if the player is holding 16 GREEN_DYE in the main hand
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() != Material.GREEN_DYE || heldItem.getAmount() < 16) {
            player.sendMessage(ChatColor.RED + "You need to be holding 16 GREEN_DYE to use this command.");
            return true;
        }

        // Remove 16 GREEN_DYE from the player's inventory
        heldItem.setAmount(heldItem.getAmount() - 16);
        player.sendMessage(ChatColor.GREEN + "You have consumed 16 green dye! Enjoy the effects!");

        // Apply potion effects
        applyPotionEffects(player);

        // Set the full-hour cooldown
        cooldowns.put(playerUUID, System.currentTimeMillis());

        return true;
    }

    private void applyPotionEffects(Player player) {
        // Example potion effects for 1 hour (3600 seconds)
        int duration = 3600 * 20; // Duration in ticks (20 ticks = 1 second)

        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, 1)); // Hunger effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 1)); // Slowness effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 1)); // Strength effect
        // Add more potion effects as needed
    }
}
