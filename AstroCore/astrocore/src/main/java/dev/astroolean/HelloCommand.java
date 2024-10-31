package dev.astroolean;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelloCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Send a message to the player
            player.sendMessage("Hello, " + player.getName() + "!");
            return true; // Command executed successfully
        } else {
            // If the sender is not a player, send a message to the console
            sender.sendMessage("This command can only be run by a player.");
            return false; // Command not executed successfully
        }
    }
}