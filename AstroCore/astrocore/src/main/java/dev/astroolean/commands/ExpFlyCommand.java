package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import dev.astroolean.AstroCore;

import java.util.HashMap;
import java.util.Map;

public class ExpFlyCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;
    private final Map<Player, Boolean> flyingInProcess = new HashMap<>();  // Track if flying is in process for each player

    public ExpFlyCommand(AstroCore plugin) {
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

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("astrocore.expfly")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (flyingInProcess.getOrDefault(player, false)) {
            player.sendMessage(ChatColor.RED + "You are already flying. Please wait until the current process is finished.");
            return true;
        }

        int expNeeded = 500;
        if (getPlayerTotalExperience(player) < expNeeded) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to fly.");
            return true;
        }

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(ChatColor.RED + "Flying disabled.");
            flyingInProcess.put(player, false);  // Reset flying status when flying is disabled
        } else {
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.GREEN + "Flying enabled. You will lose 500 XP every minute.");
            flyingInProcess.put(player, true);  // Set flying process to true when flight is enabled

            // Schedule a task to deduct 500 XP every 60 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || !player.getAllowFlight()) {
                        cancel(); // Stop task if player logs out or disables flight
                        flyingInProcess.put(player, false);  // Reset flying status
                        return;
                    }

                    if (getPlayerTotalExperience(player) < expNeeded) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.sendMessage(ChatColor.RED + "You no longer have enough experience to fly. Flying disabled.");
                        flyingInProcess.put(player, false);  // Reset flying status
                        cancel();
                        return;
                    }

                    // Deduct 500 experience points using a custom method
                    removeExperience(player, expNeeded);
                    player.sendMessage(ChatColor.RED + "500 XP deducted for flying.");

                    // Start a countdown timer using Action Bar
                    new BukkitRunnable() {
                        int countdown = 60; // 60 seconds countdown
                        int lastMessageTime = -1; // Track the last time we showed a message

                        @Override
                        public void run() {
                            if (!player.isOnline() || !player.getAllowFlight()) {
                                cancel(); // Stop if player logs out or disables flight
                                flyingInProcess.put(player, false);  // Reset flying status
                                return;
                            }

                            if (countdown <= 0) {
                                cancel(); // End the countdown when it reaches zero
                                return;
                            }

                            // Show message at specific times: 60, 45, 30, 15, then 5, 4, 3, 2, 1
                            if ((countdown == 60 || countdown == 45 || countdown == 30 || countdown == 15 || countdown <= 5) && countdown != lastMessageTime) {
                                player.sendMessage(ChatColor.YELLOW + "Time until next XP deduction: " + ChatColor.RED + countdown + " seconds");
                                lastMessageTime = countdown;
                            }

                            countdown--; // Decrease the countdown by 1 every second
                        }
                    }.runTaskTimer(plugin, 0L, 20L); // Runs every 20 ticks (1 second)
                }
            }.runTaskTimer(plugin, 0L, 1200L); // 1200 ticks = 60 seconds
        }

        return true;
    }

    // Helper method to calculate total experience points accurately
    private int getPlayerTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperienceFromLevels(player.getLevel());
    }

    // Helper method to convert level to equivalent experience points
    private int getExperienceFromLevels(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    // Helper method to remove experience safely without resetting level and progress
    private void removeExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);

        // If the player has less experience than the required amount, don't do anything
        if (currentExp < amount) {
            return;
        }

        // Deduct the experience
        int newExp = currentExp - amount;

        // Calculate the experience to remove
        int expToRemove = currentExp - newExp;
        player.giveExp(-expToRemove);  // Remove the experience without resetting level and progress
    }
}
