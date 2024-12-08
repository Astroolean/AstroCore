package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import dev.astroolean.AstroCore;

import java.util.ArrayList;
import java.util.List;

public class BlinkCommand implements TabExecutor {

    private final AstroCore plugin;

    public BlinkCommand(AstroCore plugin) {
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

        if (!plugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (!player.hasPermission("astrocore.blink")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /blink [distance]");
            return true;
        }

        int distance;
        try {
            distance = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid distance. Please enter a valid number.");
            return true;
        }

        if (distance < 1 || distance > 10) {
            player.sendMessage(ChatColor.RED + "Distance must be between 1 and 10 blocks.");
            return true;
        }

        int experienceCost = 500; // XP cost per blink
        int playerExp = getPlayerTotalExperience(player);
        
        if (playerExp < experienceCost) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience. You need at least " + experienceCost + " XP.");
            return true;
        }

        // Deduct experience and perform blink
        removeExperience(player, experienceCost);
        blinkTeleport(player, distance);

        player.sendMessage(ChatColor.GREEN + "You blinked " + distance + " blocks in the direction you are facing. Cost: " + experienceCost + " XP.");

        return true;
    }

    private void blinkTeleport(Player player, int distance) {
        Vector direction = player.getLocation().getDirection();
        player.teleport(player.getLocation().add(direction.multiply(distance)));
    }

    private int getPlayerTotalExperience(Player player) {
        int level = player.getLevel();
        int expForLevel;

        // Calculate XP from previous levels
        if (level <= 16) {
            expForLevel = level * level + 6 * level;
        } else if (level <= 31) {
            expForLevel = (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            expForLevel = (int) (4.5 * level * level - 162.5 * level + 2220);
        }

        // Add progress in the current level
        expForLevel += Math.round(player.getExp() * player.getExpToLevel());
        return expForLevel;
    }

    private void removeExperience(Player player, int amount) {
        int totalExp = getPlayerTotalExperience(player);

        // Deduct the experience safely
        int newExp = Math.max(0, totalExp - amount);
        player.setExp(0); // Reset current level progress
        player.setLevel(0); // Reset levels
        player.setTotalExperience(0); // Reset total XP

        // Re-add the remaining experience to the player
        player.giveExp(newExp);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("1");
            options.add("2");
            options.add("3");
            options.add("4");
            options.add("5");
            options.add("6");
            options.add("7");
            options.add("8");
            options.add("9");
            options.add("10");
        }
        return options;
    }
}
