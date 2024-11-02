package dev.astroolean.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

import dev.astroolean.AstroCore;

public class HealingCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds

    @SuppressWarnings("unused")
    private final AstroCore plugin;

    public HealingCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if the player is an OP
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
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

        // Heal the player to full health
        var healthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) { // Check if the attribute is not null
            double maxHealth = healthAttribute.getValue();
            if (maxHealth > 0) {
                player.setHealth(maxHealth); // Set health to max if it is greater than 0
                player.sendMessage(ChatColor.GREEN + "You have been healed to full health!");
            } else {
                player.sendMessage(ChatColor.RED + "Unable to determine maximum health.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Unable to retrieve health attribute.");
        }

        return true;
    }
}
