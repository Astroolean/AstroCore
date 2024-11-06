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
        commands.add("/hello"); descriptions.add("A friendly Hello command...");
        commands.add("/help"); descriptions.add("An anti-help command...");
        commands.add("/snort"); descriptions.add("Consume cocaine to gain effects...");
        commands.add("/smoke"); descriptions.add("Consume weed to gain effects...");
        commands.add("/gm"); descriptions.add("Open the game mode selection GUI");
        commands.add("/t"); descriptions.add("Open the time selection GUI");
        commands.add("/w"); descriptions.add("Open the weather selection GUI");
        commands.add("/cc"); descriptions.add("Clears the fucking chat...");
        commands.add("/god"); descriptions.add("Gives absolute godmode...");
        commands.add("/rename"); descriptions.add("Rename the item you are holding.");
        commands.add("/lore"); descriptions.add("Set lore for the item you are holding.");
        commands.add("/p"); descriptions.add("Shows the list of installed plugins if you are OP.");
        commands.add("/pv"); descriptions.add("PlayerVault storage for players...");
        commands.add("/fix"); descriptions.add("Repair your fucking items you lazy bum...");
        commands.add("/heal"); descriptions.add("Heal yourself to full health.");
        commands.add("/sethome"); descriptions.add("Set a home location.");
        commands.add("/home"); descriptions.add("Teleport to a home location.");
        commands.add("/delhome"); descriptions.add("Delete a home location.");
        commands.add("/homes"); descriptions.add("View your current homes.");
        commands.add("/feed"); descriptions.add("Go ahead and feed yourself.");
        commands.add("/spawn"); descriptions.add("Set and go to the fucking spawn.");
        commands.add("/lock"); descriptions.add("Lock the current time to day or night.");
        commands.add("/freeze"); descriptions.add("Freezes other players if your into that stuff.");
        commands.add("/showcoords"); descriptions.add("Shows your current coordinates within game chat.");
        commands.add("/uncraft"); descriptions.add("Uncraft an item you are holding.");
        commands.add("/autorod"); descriptions.add("Gives you a fishing rod that auto-fishes.");
        commands.add("/tos"); descriptions.add("Accept or deny the terms of service.");
        commands.add("/near"); descriptions.add("See who may be nearby.");
        commands.add("/trash"); descriptions.add("Throw useless garbage away for good.");
        commands.add("/message"); descriptions.add("Send a message to another player.");
        commands.add("/reply"); descriptions.add("Replies to another player.");
        commands.add("/color"); descriptions.add("Provides color examples.");
        commands.add("/invsee"); descriptions.add("View another players inventory for a short duration.");
        commands.add("/autoarmor"); descriptions.add("Automatically applies best armor within inventory.");
        commands.add("/autotool"); descriptions.add("Automatically applies best tool within inventory.");
        commands.add("/expfly"); descriptions.add("Allows you to fly at a cost of 500 experience points every minute.");
        commands.add("/back"); descriptions.add("Back to the last death location.");
        commands.add("/voidsafe"); descriptions.add("Fall into the void without the risk of dying.");
        commands.add("/hard"); descriptions.add("Don't do this while high.");
        commands.add("/explosion"); descriptions.add("Enable or disable creeper or tnt explosions.");

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
        if (startIndex > 0) {
            player.sendMessage(ChatColor.DARK_AQUA + "Type /ac " + (page - 1) + " to go back.");
        }
    }

    public AstroCore getPlugin() {
        return plugin;
    }
}
