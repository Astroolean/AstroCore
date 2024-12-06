package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import dev.astroolean.AstroCore;

import java.util.HashMap;
import java.util.Map;

public class BackCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;
    private final Map<Player, Location> lastDeathLocations = new HashMap<>();
    private final Map<Player, Integer> deathExperience = new HashMap<>();
    private final Map<Player, Boolean> backInProcess = new HashMap<>();

    public BackCommand(AstroCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Track player's death location and calculate half experience
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        lastDeathLocations.put(player, deathLocation);

        int currentExp = getPlayerTotalExperience(player);
        if (currentExp > 0) {
            int halfExp = currentExp / 2;
            deathExperience.put(player, halfExp);  // Store half experience

            player.setTotalExperience(0);  // Clear player's experience
            player.setLevel(0);
            player.setExp(0);
            event.setDroppedExp(0);  // Prevent experience from dropping at the death location
        }
    }

    // Restore half experience on respawn
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Integer halfExp = deathExperience.remove(player);  // Retrieve and remove stored half experience

        if (halfExp != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    addExperience(player, halfExp);  // Grant half experience back on respawn
                    player.sendMessage(ChatColor.GREEN + "Half of your experience has been restored.");
                }
            }.runTaskLater(plugin, 1L);  // Delay to ensure player is fully respawned
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
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

        if (!player.hasPermission("astrocore.back")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (backInProcess.getOrDefault(player, false)) {
            player.sendMessage(ChatColor.RED + "You are already using the /back command.");
            return true;
        }

        int expNeeded = 500;
        if (getPlayerTotalExperience(player) < expNeeded) {
            player.sendMessage(ChatColor.RED + "You do not have enough experience to use /back.");
            return true;
        }

        Location lastDeathLocation = lastDeathLocations.get(player);
        if (lastDeathLocation == null) {
            player.sendMessage(ChatColor.RED + "You have no death location to return to.");
            return true;
        }

        backInProcess.put(player, true);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    backInProcess.put(player, false);
                    cancel();
                    return;
                }

                if (getPlayerTotalExperience(player) < expNeeded) {
                    player.sendMessage(ChatColor.RED + "You no longer have enough experience to teleport back.");
                    backInProcess.put(player, false);
                    cancel();
                    return;
                }

                removeExperience(player, expNeeded);
                player.sendMessage(ChatColor.GREEN + "You have been teleported back to your last death location.");
                player.teleport(lastDeathLocation);
                backInProcess.put(player, false);
            }
        }.runTaskLater(plugin, 20L);

        return true;
    }

    // Helper method to calculate total experience points accurately
    private int getPlayerTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperienceFromLevels(player.getLevel());
    }

    // Helper method to convert level to equivalent experience points
    private int getExperienceFromLevels(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    // Helper method to remove experience safely
    private void removeExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);
        if (currentExp < amount) return;

        int newExp = currentExp - amount;
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int level = 0;
        int expForNextLevel = getExperienceFromLevels(level + 1);

        while (newExp >= expForNextLevel) {
            newExp -= expForNextLevel;
            level++;
            expForNextLevel = getExperienceFromLevels(level + 1);
        }

        player.setLevel(level);
        player.setExp(newExp / (float) expForNextLevel);
    }

    // Helper method to add experience safely
    private void addExperience(Player player, int amount) {
        int currentExp = getPlayerTotalExperience(player);
        int newExp = currentExp + amount;

        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int level = 0;
        int expForNextLevel = getExperienceFromLevels(level + 1);

        while (newExp >= expForNextLevel) {
            newExp -= expForNextLevel;
            level++;
            expForNextLevel = getExperienceFromLevels(level + 1);
        }

        player.setLevel(level);
        player.setExp(newExp / (float) expForNextLevel);
    }
}
