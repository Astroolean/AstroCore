package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dev.astroolean.AstroCore;

import java.util.HashSet;
import java.util.Set;

public class BoostCommand implements CommandExecutor {

    private final AstroCore plugin;
    private final Set<Player> boostedPlayers = new HashSet<>();
    private final float BOOST_SPEED = 1.0f; // Boosted walk speed (normal is 0.2f)

    public BoostCommand(AstroCore plugin) {
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

        if (!player.hasPermission("astrocore.boost")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /boost [enable/disable]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "enable" -> {
                if (boostedPlayers.contains(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Boost is already enabled.");
                } else {
                    startBoost(player);
                }
            }
            case "disable" -> {
                if (boostedPlayers.contains(player)) {
                    stopBoost(player);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Boost is not currently enabled.");
                }
            }
            default -> player.sendMessage(ChatColor.RED + "Usage: /boost [enable/disable]");
        }

        return true;
    }

    private void startBoost(Player player) {
        boostedPlayers.add(player);
        setPlayerSpeed(player, BOOST_SPEED);
        player.sendMessage(ChatColor.GREEN + "Speed Boost has been enabled.");

        new BukkitRunnable() {
            private final int experienceCost = 500; // XP cost every 60 seconds

            @Override
            public void run() {
                if (!boostedPlayers.contains(player)) {
                    cancel();
                    return;
                }

                int playerExp = getPlayerTotalExperience(player);

                if (playerExp < experienceCost) {
                    stopBoost(player);
                    player.sendMessage(ChatColor.RED + "You ran out of experience. Speed Boost has been disabled.");
                    cancel();
                    return;
                }

                removeExperience(player, experienceCost);
                player.sendMessage(ChatColor.YELLOW + "Speed Boost cost " + experienceCost + " XP. Remaining XP: " + getPlayerTotalExperience(player));
            }
        }.runTaskTimer(plugin, 0L, 1200L); // Runs every 60 seconds (1200 ticks)
    }

    private void stopBoost(Player player) {
        boostedPlayers.remove(player);
        resetPlayerSpeed(player);
        player.sendMessage(ChatColor.RED + "Speed Boost has been disabled.");
    }

    private void setPlayerSpeed(Player player, float speed) {
        player.setWalkSpeed(speed); // Adjust walk speed
    }

    private void resetPlayerSpeed(Player player) {
        player.setWalkSpeed(0.2f); // Default Minecraft walk speed
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
}
