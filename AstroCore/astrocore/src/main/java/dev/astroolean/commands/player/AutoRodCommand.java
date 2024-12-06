package dev.astroolean.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AutoRodCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final NamespacedKey autoFishingKey;
    private final Random random;
    private final Map<Player, BukkitRunnable> activeFishingTasks;

    public AutoRodCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.autoFishingKey = new NamespacedKey(plugin, "auto_fishing_rod");
        this.random = new Random();
        this.activeFishingTasks = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("astrocore.autorod")) {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
    
            if (plugin instanceof dev.astroolean.AstroCore astroCore && !astroCore.isEnabledCustom()) {
                player.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
                return true;
            }
            if (activeFishingTasks.containsKey(player)) {
                player.sendMessage(ChatColor.RED + "You are already auto-fishing!");
                return true;
            }
    
            // Experience cost in points
            int experienceCost = 1000; // Cost of the command in experience points
    
            // Check if the player has enough experience points
            if (player.getTotalExperience() < experienceCost) {
                player.sendMessage(ChatColor.RED + "You need at least " + experienceCost + " experience points.");
                return true;
            }
    
            // Deduct experience points from the player
            player.giveExp(-experienceCost);
    
            // Give the player a normal fishing rod with custom data
            ItemStack autoRod = new ItemStack(Material.FISHING_ROD);
            ItemMeta meta = autoRod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Auto Fishing Rod");
                meta.getPersistentDataContainer().set(autoFishingKey, PersistentDataType.BOOLEAN, true);
                autoRod.setItemMeta(meta);
            }
            player.getInventory().addItem(autoRod);
            player.sendMessage(ChatColor.GREEN + "You have received an Auto Fishing Rod!");
    
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rod = player.getInventory().getItemInMainHand();

        // Check if the player is holding the custom rod
        if (rod.getType() == Material.FISHING_ROD && rod.hasItemMeta() &&
            rod.getItemMeta().getPersistentDataContainer().has(autoFishingKey, PersistentDataType.BOOLEAN)) {
            // Only proceed if the player is actively fishing
            if (event.getState() == PlayerFishEvent.State.FISHING) {
                // Cancel the normal fishing event
                event.setCancelled(true);

                // Start auto fishing only if not already fishing
                if (!activeFishingTasks.containsKey(player)) {
                    int durationInSeconds = 60;
                    int fishingInterval = 5;
                    startAutoFishing(player, durationInSeconds, fishingInterval);
                }
            }
        } else {
            // Cancel auto fishing if not holding the rod
            if (activeFishingTasks.containsKey(player)) {
                activeFishingTasks.get(player).cancel();
                activeFishingTasks.remove(player);
                player.sendMessage(ChatColor.YELLOW + "You have stopped auto fishing.");
            }
        }
    }

    private void startAutoFishing(Player player, int durationInSeconds, int fishingInterval) {
        BukkitRunnable fishingTask = new BukkitRunnable() {
            int elapsedTime = 0;
    
            @Override
            public void run() {
                // Check if the player is still holding the custom fishing rod
                ItemStack rod = player.getInventory().getItemInMainHand();
                if (rod.getType() != Material.FISHING_ROD || !rod.hasItemMeta() ||
                    !rod.getItemMeta().getPersistentDataContainer().has(autoFishingKey, PersistentDataType.BOOLEAN)) {
                    player.sendMessage(ChatColor.YELLOW + "You have stopped auto fishing because you are no longer holding the rod.");
                    cancel();
                    activeFishingTasks.remove(player);
                    return;
                }
    
                // Simulate the fishing result based on the loot table
                ItemStack caughtItem = simulateFishing(player);
                if (caughtItem != null) {
                    player.getInventory().addItem(caughtItem);
                    player.sendMessage(ChatColor.GREEN + "You caught: " + caughtItem.getAmount() + " " + caughtItem.getType().name());
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0F, 1.0F);
    
                    // Decrease durability of the fishing rod
                    ItemMeta meta = rod.getItemMeta();
                    if (meta instanceof Damageable damageableMeta) {
                        damageableMeta.setDamage(damageableMeta.getDamage() + 1);
                        rod.setItemMeta(meta);
                        if (damageableMeta.getDamage() >= rod.getType().getMaxDurability()) {
                            player.getInventory().remove(rod);
                            player.sendMessage(ChatColor.RED + "Your Auto Fishing Rod has broken!");
                            cancel(); // Stop auto-fishing if the rod breaks
                            activeFishingTasks.remove(player);
                            return;
                        }
                    }
                }
    
                elapsedTime += fishingInterval;
    
                // Stop the task if the duration has been reached
                if (elapsedTime >= durationInSeconds) {
                    player.sendMessage(ChatColor.YELLOW + "Your auto fishing session has ended.");
                    cancel();
                    activeFishingTasks.remove(player);
                }
            }
        };
    
        fishingTask.runTaskTimer(plugin, 0, fishingInterval * 20L);
        activeFishingTasks.put(player, fishingTask);
    }    

    private ItemStack simulateFishing(Player player) {
        double chance = random.nextDouble();
        ItemStack caughtItem = null;

        if (chance < 0.85) {
            chance = random.nextDouble();
            if (chance < 0.60) {
                caughtItem = new ItemStack(Material.COD);
                player.giveExp(1); // Assuming COD gives 2 exp, so we give half
            } else if (chance < 0.85) {
                caughtItem = new ItemStack(Material.SALMON);
                player.giveExp(1); // Assuming SALMON gives 2 exp
            } else if (chance < 0.87) {
                caughtItem = new ItemStack(Material.TROPICAL_FISH);
                player.giveExp(1); // Assuming TROPICAL_FISH gives 2 exp
            } else {
                caughtItem = new ItemStack(Material.PUFFERFISH);
                player.giveExp(1); // Assuming PUFFERFISH gives 2 exp
            }
        } else if (chance < 0.90) {
            chance = random.nextDouble();
            if (chance < 0.167) {
                caughtItem = new ItemStack(Material.BOW);
                player.giveExp(1); // Assuming BOW gives 2 exp
            } else if (chance < 0.334) {
                caughtItem = new ItemStack(Material.ENCHANTED_BOOK);
                player.giveExp(1); // Assuming ENCHANTED_BOOK gives 2 exp
            } else if (chance < 0.501) {
                caughtItem = new ItemStack(Material.FISHING_ROD);
                player.giveExp(1); // Assuming FISHING_ROD gives 2 exp
            } else if (chance < 0.668) {
                caughtItem = new ItemStack(Material.NAME_TAG);
                player.giveExp(1); // Assuming NAME_TAG gives 2 exp
            } else if (chance < 0.835) {
                caughtItem = new ItemStack(Material.NAUTILUS_SHELL);
                player.giveExp(1); // Assuming NAUTILUS_SHELL gives 2 exp
            } else {
                caughtItem = new ItemStack(Material.SADDLE);
                player.giveExp(1); // Assuming SADDLE gives 2 exp
            }
        } else {
            chance = random.nextDouble();
            if (chance < 0.17) {
                caughtItem = new ItemStack(Material.LILY_PAD);
                player.giveExp(1); // Assuming LILY_PAD gives 2 exp
            } else if (chance < 0.27) {
                caughtItem = new ItemStack(Material.BOWL);
                player.giveExp(1); // Assuming BOWL gives 2 exp
            } else if (chance < 0.29) {
                caughtItem = new ItemStack(Material.FISHING_ROD);
                player.giveExp(1); // Assuming FISHING_ROD gives 2 exp
            } else if (chance < 0.39) {
                caughtItem = new ItemStack(Material.LEATHER);
                player.giveExp(1); // Assuming LEATHER gives 2 exp
            } else if (chance < 0.49) {
                caughtItem = new ItemStack(Material.STRING);
                player.giveExp(1); // Assuming STRING gives 2 exp
            }
        }

        return caughtItem;
    }
}
