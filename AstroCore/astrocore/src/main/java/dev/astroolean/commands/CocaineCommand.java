package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class CocaineCommand implements CommandExecutor {
    private final Plugin plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final int effectDurationSeconds = 3600; // 1 hour in seconds

    public CocaineCommand(Plugin plugin) {
        this.plugin = plugin;
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

        // Full hour cooldown check
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long timeLeft = (cooldowns.get(playerUUID) + (effectDurationSeconds * 1000)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000 / 60) + " minutes before using this command again.");
                return true;
            }
        }

        // Check if the player is holding 16 sugar in the main hand
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() != Material.SUGAR || heldItem.getAmount() < 16) {
            player.sendMessage(ChatColor.RED + "You need to be holding 16 sugar to use this command.");
            return true;
        }

        // Remove 16 sugar from the player's inventory
        heldItem.setAmount(heldItem.getAmount() - 16);
        player.sendMessage(ChatColor.GREEN + "You have consumed 16 sugar! Enjoy the effects!");

        // Apply potion effects
        applyPotionEffects(player);

        // Set the full-hour cooldown
        cooldowns.put(playerUUID, System.currentTimeMillis());

        return true;
    }

    private void applyPotionEffects(Player player) {
        // Potion effects for 1 hour (3600 seconds)
        int durationTicks = effectDurationSeconds * 20; // Duration in ticks (20 ticks = 1 second)

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, 1)); // Speed effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, durationTicks, 1)); // Jump boost
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, durationTicks, 1)); // Regeneration
        // Add more potion effects as needed
    }
}
