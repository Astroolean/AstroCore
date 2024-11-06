package dev.astroolean.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import dev.astroolean.AstroCore;

public class MessageCommand implements CommandExecutor {
    private final AstroCore plugin;
    private final HashMap<UUID, UUID> lastMessaged = new HashMap<>(); // Stores the last player each sender messaged
    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); // Cooldowns for players
    private final int cooldownTime = 3; // Cooldown time in seconds

    public MessageCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        if (!player.hasPermission("astrocore.message")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to message.");
            return true;
        }

        // Determine whether this is a /message or /reply command
        if (label.equalsIgnoreCase("message")) {
            return handleMessageCommand(player, args);
        } else if (label.equalsIgnoreCase("reply")) {
            return handleReplyCommand(player, args);
        }

        return false;
    }

    private boolean handleMessageCommand(Player sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /message <player> <message>");
            return true;
        }
    
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }
    
        // Check cooldown for the sender
        if (isOnCooldown(sender)) {
            long timeLeft = (cooldowns.get(sender.getUniqueId()) + (cooldownTime * 1000)) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000) + " seconds before using this command again.");
            return true;
        }
    
        // Set the cooldown
        setCooldown(sender);
    
        // Check if sender is messaging themselves
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot message yourself.");
            return true;
        }
    
        // Store the last messaged player
        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
    
        // Construct and send the message
        String message = String.join(" ", args).substring(args[0].length()).trim();
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "To " + target.getName() + ": " + ChatColor.WHITE + message);
        target.sendMessage(ChatColor.LIGHT_PURPLE + "From " + sender.getName() + ": " + ChatColor.WHITE + message);
    
        return true;
    }

    private boolean handleReplyCommand(Player sender, String[] args) {
        UUID lastTargetUUID = lastMessaged.get(sender.getUniqueId());
        if (lastTargetUUID == null) {
            sender.sendMessage(ChatColor.RED + "You have no one to reply to.");
            return true;
        }

        Player target = plugin.getServer().getPlayer(lastTargetUUID);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "The player you are trying to reply to is no longer online.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /reply <message>");
            return true;
        }

        // Check cooldown for the sender
        if (isOnCooldown(sender)) {
            long timeLeft = (cooldowns.get(sender.getUniqueId()) + (cooldownTime * 1000)) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.RED + "You must wait " + (timeLeft / 1000) + " seconds before using this command again.");
            return true;
        }

        // Set the cooldown
        setCooldown(sender);

        // Construct and send the reply message
        String message = String.join(" ", args);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "To " + target.getName() + ": " + ChatColor.WHITE + message);
        target.sendMessage(ChatColor.LIGHT_PURPLE + "From " + sender.getName() + ": " + ChatColor.WHITE + message);

        // Update the last messaged player for sender and target
        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), sender.getUniqueId());

        return true;
    }

    private boolean isOnCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) &&
               (System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) < cooldownTime * 1000;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
