package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import dev.astroolean.AstroCore;

public class BroadcastCommand implements CommandExecutor {

    @SuppressWarnings("unused")
    private final AstroCore plugin;

    public BroadcastCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Check if the plugin is enabled
        if (!plugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // Check if the player has permission
        if (!player.hasPermission("astrocore.broadcast")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Check if the player has enough experience points (500 exp)
        int expCost = 500;
        if (getPlayerTotalExperience(player) < expCost) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to broadcast a message.");
            return true;
        }

        // Deduct experience from the player
        removeExperience(player, expCost);

        // Check if a message is provided
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /broadcast [message]");
            return false;
        }

        // Get the broadcast message
        String message = String.join(" ", args);

        // Broadcast the message with a blue theme
        String broadcastMessage = ChatColor.BLUE + "==========[ BROADCAST ]==========\n"
                + ChatColor.AQUA + message + "\n"
                + ChatColor.BLUE + "===============================";

        // Send the message to the entire server
        player.getServer().broadcastMessage(broadcastMessage);

        // Inform the player
        player.sendMessage(ChatColor.GREEN + "Your message has been broadcasted!");

        return true;
    }

    // Helper method to calculate total experience
    private int getPlayerTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperienceFromLevels(player.getLevel());
    }

    private int getExperienceFromLevels(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    // Helper method to remove experience
    private void removeExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);
        if (currentExp < amount) {
            return;
        }
        int newExp = currentExp - amount;
        int expToRemove = currentExp - newExp;
        player.giveExp(-expToRemove);
    }
}
