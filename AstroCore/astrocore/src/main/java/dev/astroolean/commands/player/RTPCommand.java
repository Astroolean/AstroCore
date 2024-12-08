package dev.astroolean.commands.player;

import dev.astroolean.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class RTPCommand implements CommandExecutor {

    private final AstroCore plugin;

    public RTPCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) return true;

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        if (!plugin.isEnabledCustom()) {
            player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (!player.hasPermission("astrocore.rtp")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (getPlayerTotalExperience(player) < 1000) {
            player.sendMessage(ChatColor.RED + "You do not have enough XP to use this command. You need at least 1000 XP.");
            return true;
        }

        boolean success = teleportToRandomLocation(player);

        if (success) {
            removeExperience(player, 1000);
            player.sendMessage(ChatColor.GREEN + "Teleported to a random location!");
        } else {
            player.sendMessage(ChatColor.RED + "Unable to find a safe location to teleport. Teleporting you to the world spawn as a fallback.");
            player.teleport(player.getWorld().getSpawnLocation());
        }

        return true;
    }

    private int getPlayerTotalExperience(Player player) {
        int level = player.getLevel();
        int expForLevel;

        if (level <= 16) {
            expForLevel = level * level + 6 * level;
        } else if (level <= 31) {
            expForLevel = (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            expForLevel = (int) (4.5 * level * level - 162.5 * level + 2220);
        }

        expForLevel += Math.round(player.getExp() * player.getExpToLevel());
        return expForLevel;
    }

    private void removeExperience(Player player, int amount) {
        int totalExp = getPlayerTotalExperience(player);
        int newExp = Math.max(0, totalExp - amount);
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.giveExp(newExp);
    }

    private boolean teleportToRandomLocation(Player player) {
        Random random = new Random();
        Vector currentPosition = player.getLocation().toVector();
        int maxRange = 5000;
        World world = player.getWorld();

        for (int attempts = 0; attempts < 50; attempts++) { // Increased attempts to 50
            double x = currentPosition.getX() + (random.nextInt(maxRange * 2) - maxRange);
            double z = currentPosition.getZ() + (random.nextInt(maxRange * 2) - maxRange);
            double y = getSafeYCoordinate(world, x, z);

            if (y == -1) continue; // Skip invalid locations

            if (isSafeLocation(world, x, y, z)) {
                player.teleport(new org.bukkit.Location(world, x, y, z));
                return true;
            }
        }

        return false;
    }

    private double getSafeYCoordinate(World world, double x, double z) {
        int highestY = world.getHighestBlockYAt((int) x, (int) z);
        if (highestY < 1) return -1; // No valid height found
        return highestY;
    }

    private boolean isSafeLocation(World world, double x, double y, double z) {
        org.bukkit.Location location = new org.bukkit.Location(world, x, y, z);

        Block blockBelow = world.getBlockAt(location.clone().subtract(0, 1, 0));
        Block blockAt = world.getBlockAt(location);
        Block blockAbove = world.getBlockAt(location.clone().add(0, 1, 0));

        if (!blockBelow.getType().isSolid()) return false;
        if (!blockAt.isEmpty()) return false;
        if (!blockAbove.isEmpty()) return false;

        Material[] dangerousMaterials = {Material.LAVA, Material.CACTUS, Material.MAGMA_BLOCK, Material.FIRE};
        for (Material material : dangerousMaterials) {
            if (blockBelow.getType() == material || blockAt.getType() == material || blockAbove.getType() == material) {
                return false; // Unsafe due to dangerous materials
            }
        }

        return true;
    }
}
