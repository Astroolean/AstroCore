package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.astroolean.AstroCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GravityCommand implements TabExecutor {

    private final AstroCore plugin;
    private final Map<Player, BukkitRunnable> activeEffects = new HashMap<>();

    public GravityCommand(AstroCore plugin) {
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

        if (!plugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (!player.hasPermission("astrocore.gravity")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /gravity [low/medium/high/off]");
            return true;
        }

        String level = args[0].toLowerCase();

        if (level.equals("off")) {
            disableGravity(player);
            return true;
        }

        if (activeEffects.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "You already have gravity effects active. Use /gravity off to disable.");
            return true;
        }

        int costPerMinute;
        PotionEffectType effect = PotionEffectType.JUMP;
        int amplifier;

        switch (level) {
            case "low" -> {
                costPerMinute = 500;
                amplifier = 1;
            }
            case "medium" -> {
                costPerMinute = 1000;
                amplifier = 2;
            }
            case "high" -> {
                costPerMinute = 1500;
                amplifier = 3;
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Invalid gravity level. Choose low, medium, high, or off.");
                return true;
            }
        }

        int playerExp = getPlayerTotalExperience(player);
        if (playerExp < costPerMinute) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to activate " + level + " gravity.");
            return true;
        }

        // Deduct initial XP and apply effects
        removeExperience(player, costPerMinute);
        applyGravityEffects(player, level, effect, amplifier);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !activeEffects.containsKey(player)) {
                    cancel();
                    return;
                }

                if (getPlayerTotalExperience(player) < costPerMinute) {
                    player.sendMessage(ChatColor.RED + "You no longer have enough experience for " + level + " gravity. Effect disabled.");
                    disableGravity(player);
                    cancel();
                    return;
                }

                // Deduct XP every minute
                removeExperience(player, costPerMinute);
                player.sendMessage(ChatColor.YELLOW + "Deducted " + costPerMinute + " XP for " + level + " gravity.");

                // Reapply effects to ensure they don't wear off
                applyGravityEffects(player, level, effect, amplifier);
            }
        };

        task.runTaskTimer(plugin, 20L * 60, 20L * 60); // Every minute
        activeEffects.put(player, task);

        player.sendMessage(ChatColor.GREEN + "Activated " + level + " gravity for 1 minute. Cost: " + costPerMinute + " XP/min.");
        return true;
    }

    private void applyGravityEffects(Player player, String level, PotionEffectType effect, int amplifier) {
        player.addPotionEffect(new PotionEffect(effect, 20 * 60, amplifier, true, false)); // Jump boost
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 60, 0, true, false)); // Slow falling
        player.sendMessage(ChatColor.GREEN + "Gravity effect (" + level + ") applied.");
    }

    private void disableGravity(Player player) {
        if (activeEffects.containsKey(player)) {
            activeEffects.get(player).cancel();
            activeEffects.remove(player);
        }
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.SLOW_FALLING);
        player.sendMessage(ChatColor.RED + "Gravity effects disabled.");
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

        // Deduct the experience safely
        int newExp = Math.max(0, totalExp - amount);
        player.setExp(0); // Reset current level progress
        player.setLevel(0); // Reset levels
        player.setTotalExperience(0); // Reset total XP

        // Re-add the remaining experience to the player
        player.giveExp(newExp);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("low");
            options.add("medium");
            options.add("high");
            options.add("off");
        }
        return options;
    }
}
