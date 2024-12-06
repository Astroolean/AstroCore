package dev.astroolean.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.astroolean.AstroCore;

public class NicknameCommand implements CommandExecutor {

    @SuppressWarnings("unused")
    private final AstroCore plugin;

    public NicknameCommand(AstroCore plugin) {
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

        if (!player.hasPermission("astrocore.nickname")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /nickname [name/reset]");
            return false;
        }

        String action = args[0].toLowerCase();

        // Reset nickname
        if ("reset".equals(action)) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.sendMessage(ChatColor.GREEN + "Your nickname has been reset to your original name.");
            return true;
        }

        // Charge 500 experience points for nickname change
        int expCost = 500;
        if (getPlayerTotalExperience(player) < expCost) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to change your nickname.");
            return true;
        }

        // Deduct experience
        removeExperience(player, expCost);

        // Set the new nickname
        String nickname = String.join(" ", args);
        player.setDisplayName(nickname);
        player.setPlayerListName(nickname);
        player.sendMessage(ChatColor.GREEN + "Your nickname has been set to: " + nickname);
        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has changed their nickname to " + nickname);

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
