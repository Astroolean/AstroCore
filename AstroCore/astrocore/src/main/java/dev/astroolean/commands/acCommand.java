package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import dev.astroolean.AstroCore;

public class acCommand implements CommandExecutor {
    private final AstroCore plugin;

    public acCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the plugin is initialized correctly
        if (!(plugin instanceof AstroCore)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }

        // Check if the plugin is enabled
        if (!plugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        int page = 1;

        // Check if an argument is provided for the page number
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid page number. Please enter a valid number.");
                return true;
            }
        }

        showCommands(player, page);
        return true;
    }

    private void showCommands(Player player, int page) {
        List<String> commands = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();

        // Add commands and descriptions
        commands.add("/astrocore"); descriptions.add("Enable or disable AstroCore.");
        commands.add("/hello"); descriptions.add("Say hello.");
        commands.add("/help"); descriptions.add("Get help and resources.");
        commands.add("/god"); descriptions.add("Gain invincibility and flight.");
        commands.add("/cc"); descriptions.add("Clear chat.");
        commands.add("/smoke"); descriptions.add("Consume 16 green dye.");
        commands.add("/snort"); descriptions.add("Consume 16 sugar.");
        commands.add("/gm"); descriptions.add("Open Gamemode menu.");
        commands.add("/t"); descriptions.add("Open Time menu.");
        commands.add("/w"); descriptions.add("Open Weather menu.");
        commands.add("/rename"); descriptions.add("Rename an item.");
        commands.add("/lore"); descriptions.add("Add lore to an item.");
        commands.add("/p"); descriptions.add("Disable default plugins command.");
        commands.add("/pv"); descriptions.add("Use player vaults (max 2).");
        commands.add("/fix"); descriptions.add("Repair items (costs XP).");
        commands.add("/heal"); descriptions.add("Heal yourself (OP only).");
        commands.add("/sethome"); descriptions.add("Set a home location.");
        commands.add("/home"); descriptions.add("Teleport to your home.");
        commands.add("/delhome"); descriptions.add("Delete a specified home.");
        commands.add("/homes"); descriptions.add("List all your homes.");
        commands.add("/feed"); descriptions.add("Feed yourself (OP only).");
        commands.add("/spawn"); descriptions.add("Set or teleport to spawn.");

        // Calculate the start and end index based on the page number
        int startIndex = (page - 1) * 10; // 10 commands per page
        int endIndex = Math.min(startIndex + 10, commands.size());

        // Check if the requested page has any commands to display
        if (startIndex < 0 || startIndex >= commands.size()) {
            player.sendMessage(ChatColor.RED + "No commands available for page " + page + ".");
            return;
        }

        player.sendMessage(ChatColor.DARK_AQUA + "----- Command List (Page " + page + ") -----");
        for (int i = startIndex; i < endIndex; i++) {
            player.sendMessage(ChatColor.AQUA + commands.get(i) + ": " + ChatColor.WHITE + descriptions.get(i));
        }

        // Provide navigation for pages
        if (endIndex < commands.size()) {
            player.sendMessage(ChatColor.DARK_AQUA + "Type /ac " + (page + 1) + " for more commands.");
        }
        if (page > 1) {
            player.sendMessage(ChatColor.DARK_AQUA + "Type /ac " + (page - 1) + " to go back.");
        }
    }

    public AstroCore getPlugin() {
        return plugin;
    }
}
