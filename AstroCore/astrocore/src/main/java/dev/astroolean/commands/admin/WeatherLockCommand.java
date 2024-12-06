package dev.astroolean.commands.admin;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeatherLockCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds
    private final JavaPlugin plugin;
    private boolean isTimeLocked = false; // Tracks if the time is locked
    private String lockedTime = ""; // Stores the locked time (either "day" or "night")
    private BukkitRunnable timeLockTask; // Task to repeatedly set the time

    public WeatherLockCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("astrocore.lock")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (plugin instanceof dev.astroolean.AstroCore astroCore && !astroCore.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeLeft = (cooldowns.get(player.getUniqueId()) + (cooldownTime * 1000)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000) + " seconds before using this command again.");
                return true;
            }
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /lock [day/night]");
            return true;
        }

        String time = args[0].toLowerCase();

        switch (time) {
            case "day" -> {
                lockTime("day", player, 1000);
            }
            case "night" -> {
                lockTime("night", player, 13000);
            }
            default -> player.sendMessage(ChatColor.RED + "Invalid argument. Please use 'day' or 'night'.");
        }

        return true;
    }

    private void lockTime(String time, Player player, long ticks) {
        if (isTimeLocked) {
            player.sendMessage(ChatColor.YELLOW + "Time lock disabled.");
            if (timeLockTask != null) {
                timeLockTask.cancel();
                timeLockTask = null;
            }
            isTimeLocked = false;
            lockedTime = "";
            return;
        }

        // Set initial time
        Bukkit.getWorlds().forEach(world -> world.setTime(ticks));
        player.sendMessage(ChatColor.GREEN + "The time has been locked to " + time + ".");

        // Lock the time by scheduling a repeating task
        isTimeLocked = true;
        lockedTime = time;

        timeLockTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isTimeLocked && lockedTime.equals(time)) {
                    Bukkit.getWorlds().forEach(world -> world.setTime(ticks));
                } else {
                    this.cancel();
                }
            }
        };
        timeLockTask.runTaskTimer(plugin, 0L, 100L); // Run every 100 ticks (5 seconds)
    }
}
