package dev.astroolean.commands.player;

import dev.astroolean.AstroCore; // Import your main plugin class
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhoisCommand implements CommandExecutor {

    private final AstroCore plugin;

    public WhoisCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true; // No action needed for null sender
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("astrocore.whois")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Check if the plugin is enabled
        if (!plugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "The AstroCore plugin is currently disabled.");
            return true;
        }

        // Check if the correct arguments are provided
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /whois <player>");
            return false;
        }

        // Find the target player by name or nickname
        String input = args[0];
        Player target = Bukkit.getPlayer(input);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        // Display the player's real name and nickname
        String nickname = target.getDisplayName();
        String realName = target.getName();

        player.sendMessage(ChatColor.GOLD + "Whois information:");
        player.sendMessage(ChatColor.GREEN + "Real name: " + ChatColor.WHITE + realName);
        player.sendMessage(ChatColor.GREEN + "Nickname: " + ChatColor.WHITE + nickname);

        return true;
    }
}
