package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GodCommand implements CommandExecutor, Listener {
    @SuppressWarnings("*")
    private final JavaPlugin plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private final long cooldownTime = 3000; // Cooldown time in milliseconds

    public GodCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender has permission for the "god" command
        if (!sender.hasPermission("astrocore.god")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
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

        // Check cooldown
        if (isOnCooldown(player)) {
            player.sendMessage(ChatColor.RED + "You must wait before using this command again.");
            return true;
        }

        // Toggle God Mode
        toggleGodMode(player);
        setCooldown(player);
        return true;
    }

    private void toggleGodMode(Player player) {
        if (player.getAllowFlight()) {
            // Disable God Mode
            player.setAllowFlight(false);
            player.setFlying(false); // Ensure player is not flying
            player.sendMessage(ChatColor.RED + "God Mode is now OFF.");
        } else {
            // Enable God Mode
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.GREEN + "God Mode is now ON.");
        }
    }

    private boolean isOnCooldown(Player player) {
        return cooldowns.containsKey(player) && (System.currentTimeMillis() - cooldowns.get(player)) < cooldownTime;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        // Prevent flying when God Mode is off
        if (!event.getPlayer().getAllowFlight()) {
            event.setCancelled(true);
        }
    }
}
