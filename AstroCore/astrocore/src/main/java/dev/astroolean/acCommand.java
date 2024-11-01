package dev.astroolean;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class acCommand implements CommandExecutor {
    private final Plugin plugin;

    public acCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Assuming 'plugin' is an instance of dev.astroolean.Plugin
        if (!(plugin instanceof dev.astroolean.Plugin)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }

        // Cast 'plugin' to your custom plugin class
        dev.astroolean.Plugin myPlugin = (dev.astroolean.Plugin) plugin;

        if (!myPlugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }   
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
        commands.add("/astrocore");
        descriptions.add("Manage the AstroCore plugin.");

        commands.add("/hello");
        descriptions.add("Sends a greeting message.");

        commands.add("/help");
        descriptions.add("Shows a list of available commands.");

        commands.add("/cc");
        descriptions.add("Clears the chat.");

        commands.add("/god");
        descriptions.add("Grants god mode to the player.");

        commands.add("/smoke");
        descriptions.add("Triggers a smoke effect.");

        commands.add("/snort");
        descriptions.add("Triggers a snorting effect.");

        commands.add("/gm");
        descriptions.add("Changes your game mode.");

        commands.add("/w");
        descriptions.add("Send a private message to a player.");

        commands.add("/t");
        descriptions.add("Teleport to a specified location.");

        // Add second page commands and descriptions
        if (page == 2) {
            commands.add("/example");
            descriptions.add("Example...");
        }

        // Calculate the start and end index based on the page number
        int startIndex = (page - 1) * 10;
        int endIndex = Math.min(startIndex + 10, commands.size());

        player.sendMessage(ChatColor.GREEN + "----- Command List (Page " + page + ") -----");
        for (int i = startIndex; i < endIndex; i++) {
            player.sendMessage(ChatColor.YELLOW + commands.get(i) + ": " + ChatColor.WHITE + descriptions.get(i));
        }

        // Optionally, provide navigation for pages
        if (endIndex < commands.size()) {
            player.sendMessage(ChatColor.BLUE + "Type /ac " + (page + 1) + " for more commands.");
        }
        if (page > 1) {
            player.sendMessage(ChatColor.BLUE + "Type /ac " + (page - 1) + " to go back.");
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
