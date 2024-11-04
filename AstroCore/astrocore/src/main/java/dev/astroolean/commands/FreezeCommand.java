package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class FreezeCommand implements CommandExecutor, Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds for using the freeze command
    private final JavaPlugin plugin;
    private static final HashMap<UUID, BukkitRunnable> frozenPlayers = new HashMap<>(); // Tracks frozen players and their tasks

    public FreezeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin); // Register the event listener
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

        if (!player.hasPermission("astrocore.freeze")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (plugin instanceof dev.astroolean.AstroCore astroCore && !astroCore.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // Check if the player has the required permission
        if (!player.hasPermission("astrocore.freeze")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
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

        // Check for proper usage
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /freeze [player] [time]");
            return true;
        }

        // Get target player and duration
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int freezeTime;
        try {
            freezeTime = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid time format. Please enter a number of seconds.");
            return true;
        }

        // Prevent freezing if the player is already frozen
        if (frozenPlayers.containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Player is already frozen.");
            return true;
        }

        // Freeze the target player
        player.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen for " + freezeTime + " seconds.");
        target.sendMessage(ChatColor.RED + "You have been frozen!");

        // Create a task to unfreeze the player after the specified time
        BukkitRunnable freezeTask = new BukkitRunnable() {
            @Override
            public void run() {
                frozenPlayers.remove(target.getUniqueId());
                target.sendMessage(ChatColor.GREEN + "You are no longer frozen.");
            }
        };

        // Store the freeze task and start it
        frozenPlayers.put(target.getUniqueId(), freezeTask);
        freezeTask.runTaskLater(plugin, freezeTime * 20L);

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // Cancel movement if the player is frozen
        if (frozenPlayers.containsKey(player.getUniqueId())) {
            event.setTo(event.getFrom()); // Stops movement by setting the player's location back to the previous one
        }
    }
}
