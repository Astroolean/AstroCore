package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import dev.astroolean.AstroCore;

import java.util.HashSet;
import java.util.Set;

public class VoidSafeCommand implements CommandExecutor, Listener {
    @SuppressWarnings("*")
    private final AstroCore plugin;
    private boolean voidSafeEnabled = false;  // Toggle for the void safety system
    @SuppressWarnings("unused")
    private final Set<Player> protectedPlayers = new HashSet<>();

    public VoidSafeCommand(AstroCore plugin) {
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

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /voidsafe [enable/disable]");
            return true;
        }

        if (!player.hasPermission("astrocore.voidsafe")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "enable" -> {
                voidSafeEnabled = true;
                player.sendMessage(ChatColor.GREEN + "Void Safe has been enabled. Falling into the void in the End will teleport you to spawn.");
            }

            case "disable" -> {
                voidSafeEnabled = false;
                player.sendMessage(ChatColor.RED + "Void Safe has been disabled. You will no longer be protected from void falls.");
            }

            default -> {
                player.sendMessage(ChatColor.RED + "Invalid argument. Use /voidsafe [enable/disable].");
                return true;
            }
        }

        return true;
    }

    @EventHandler
    public void onPlayerFallIntoVoid(EntityDamageEvent event) {
        if (!voidSafeEnabled || !(event.getEntity() instanceof Player player)) return;

        // Check if the player is in the void in the End dimension
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && player.getWorld().getEnvironment() == World.Environment.THE_END) {
            // Cancel the void damage event
            event.setCancelled(true);

            // Teleport the player to the actual world spawn (simulating the /spawn command)
            World overworld = plugin.getServer().getWorld("world"); // Ensure this matches the actual world name
            if (overworld != null) {
                Location worldSpawnLocation = overworld.getSpawnLocation();
                player.teleport(worldSpawnLocation);
                player.sendMessage(ChatColor.GREEN + "Void Safe activated! You have been teleported to the world spawn.");
            } else {
                player.sendMessage(ChatColor.RED + "World spawn not found. Contact an administrator.");
            }
        }
    }
}