package dev.astroolean.commands.player;

import dev.astroolean.AstroCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ItemRollCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;

    public ItemRollCommand(AstroCore plugin) {
        this.plugin = plugin;
    }

    // Modify your onCommand method to handle add/remove with percentage
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

        if (!player.hasPermission("astrocore.itemroll")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1 && args.length != 2 && args.length != 3) {
            player.sendMessage(ChatColor.RED + "Usage: /itemroll [auto/once] [amount]");
            return true;
        }

        String rollType = args[0].toLowerCase();
        int experienceCost = 500;

        if (getPlayerTotalExperience(player) < experienceCost) {
            player.sendMessage(ChatColor.RED + "You don't have enough experience points for the roll.");
            return true;
        }

        switch (rollType) {
            case "once" -> rollOnce(player, experienceCost);
            case "auto" -> {
                if (args.length == 2) {
                    try {
                        int rollCount = Integer.parseInt(args[1]);
                        rollAuto(player, experienceCost, rollCount);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number of rolls.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /itemroll auto [amount]");
                }
            }
        }
        return true;
    }

    // Declare the map to track if a roll is in progress
    private final Map<UUID, Boolean> playerRollInProgressMap = new HashMap<>();
    private final Map<UUID, Boolean> playerAnimationInProgressMap = new HashMap<>();

    // Method to check if the roll is in progress
    private boolean isRollInProgress(Player player) {
        return playerRollInProgressMap.getOrDefault(player.getUniqueId(), false);
    }

    // Method to check if the animation is in progress
    private boolean isAnimationInProgress(Player player) {
        return playerAnimationInProgressMap.getOrDefault(player.getUniqueId(), false);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
    
        Player player = (Player) event.getWhoClicked();
    
        // Only handle the item roll inventory
        if (!event.getView().getTitle().equals("Item Roll")) {
            return;
        }
    
        // If the animation is in progress, block all clicks
        if (isAnimationInProgress(player)) {
            event.setCancelled(true);
            return;
        }
    
        // If the roll is in progress, block interaction with any inventory slot except the reward slot (13)
        if (isRollInProgress(player)) {
            // If the click is in slot 13 and the item is not AIR (allow taking the reward)
            if (event.getSlot() == 13 && event.getCurrentItem().getType() != Material.AIR) {
                // Allow taking the reward (slot 13)
                event.setCancelled(false);
                plugin.getServer().getScheduler().runTask(plugin, () -> player.closeInventory()); // Close the inventory after taking the reward
            } else {
                // Prevent interaction with any other slot (including slot 13 if the item is AIR)
                event.setCancelled(true);
            }
        }
    
        // Block dragging: If the player tries to drag items between slots (this is still a problem even if clicks are cancelled)
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            event.setCancelled(true);  // Cancel drag actions
        }
    
        // Block shift-clicking: If the player tries to shift-click (move an item to another slot)
        if (event.isShiftClick()) {
            event.setCancelled(true);  // Cancel shift-click actions
        }
    
        // Block clicking to remove items (anything besides slot 13)
        if (event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF ||
            event.getAction() == InventoryAction.PICKUP_ONE || event.getAction() == InventoryAction.DROP_ALL_SLOT ||
            event.getAction() == InventoryAction.DROP_ONE_SLOT || event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            event.setCancelled(true);  // Cancel removing or interacting with any items
        }
    }

    // Modified rollOnce method to manage animation state
    private void rollOnce(Player player, int experienceCost) {
        removeExperience(player, experienceCost);

        // Mark that animation is in progress
        playerAnimationInProgressMap.put(player.getUniqueId(), true);
        playerRollInProgressMap.put(player.getUniqueId(), true);

        // Create the inventory and show the rolling animation
        Inventory inventory = plugin.getServer().createInventory(null, 27, "Item Roll");
        showRollingAnimation(inventory, player);

        // After the roll finishes, mark the roll and animation as complete
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            playerAnimationInProgressMap.put(player.getUniqueId(), false); // End animation
            // Allow the player to take the reward
            plugin.getServer().getScheduler().runTask(plugin, () -> {
            });
        }, 40L);  // Assuming the animation lasts 2 seconds (40 ticks)

        // After the animation is done, mark the roll as finished
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            playerRollInProgressMap.put(player.getUniqueId(), false);
        }, 40L); // Roll ends after animation
    }

    private void rollAuto(Player player, int experienceCost, int rollCount) {
        new BukkitRunnable() {
            int rollsLeft = rollCount; // Number of rolls remaining
    
            @Override
            public void run() {
                if (getPlayerTotalExperience(player) >= experienceCost) {
                    // Start the roll
                    rollOnce(player, experienceCost);
                    rollsLeft--;
    
                    if (rollsLeft <= 0) {
                        cancel(); // Cancel the task after completing all rolls
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough experience points.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 100L); // Repeat every 5 seconds
    }

    private void showRollingAnimation(Inventory inventory, Player player) {
        Random rand = new Random();
    
        // Different shades of blue stained glass panes
        ItemStack[] blueGlass = {
            new ItemStack(Material.BLUE_STAINED_GLASS_PANE),
            new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
            new ItemStack(Material.BLUE_STAINED_GLASS_PANE), // You can add more if needed
        };
    
        ItemStack soulTorch = new ItemStack(Material.SOUL_TORCH); // Soul Torch as the indicator
    
        // Configurable list of reward items and their probabilities
        List<ItemReward> rollItems = List.of(
            new ItemReward(Material.COAL, 10),  // 10% chance for Coal
            new ItemReward(Material.IRON_INGOT, 15), // 15% chance for Iron Ingot
            new ItemReward(Material.GOLD_INGOT, 10), // 10% chance for Gold Ingot
            new ItemReward(Material.DIAMOND, 5), // 5% chance for Diamond
            new ItemReward(Material.EMERALD, 3), // 3% chance for Emerald
            new ItemReward(Material.LAPIS_LAZULI, 7), // 7% chance for Lapis Lazuli
            new ItemReward(Material.REDSTONE, 10), // 10% chance for Redstone
            new ItemReward(Material.COPPER_INGOT, 10), // 10% chance for Copper Ingot
            new ItemReward(Material.AMETHYST_SHARD, 5), // 5% chance for Amethyst Shard
            new ItemReward(Material.GOLDEN_APPLE, 5) // 5% chance for Golden Apple
        );
    
        // Normalize the reward item probabilities to sum to 100%
        int totalWeight = rollItems.stream().mapToInt(ItemReward::getWeight).sum();
        int scaleFactor = 100 / totalWeight;
    
        // Scale the rewards to sum up to 100%
        List<ItemReward> normalizedRollItems = rollItems.stream()
            .map(item -> new ItemReward(item.getMaterial(), item.getWeight() * scaleFactor))
            .toList();
    
        // Predefined glass positions for the blue pattern (edges of the inventory)
        int[] glassPositions = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, // Top row
            17, 26, // Right column
            25, 24, 23, 22, 21, 20, 19, 18, // Bottom row
            9 // Left column
        };
    
        // Set the soul torches at fixed positions (top middle and bottom middle)
        inventory.setItem(4, soulTorch);  // Top middle
        inventory.setItem(22, soulTorch);  // Bottom middle
    
        // Set the center position where the reward will appear (initially empty)
        ItemStack rewardPlaceholder = new ItemStack(Material.AIR);
        inventory.setItem(13, rewardPlaceholder);
    
        // Open the inventory immediately
        plugin.getServer().getScheduler().runTask(plugin, () -> player.openInventory(inventory));
    
        new BukkitRunnable() {
            int waveStep = 0;
            int middleStep = 0;
    
            @Override
            public void run() {
                // Randomly change the glass panes at the specified positions
                for (int i = 0; i < glassPositions.length; i++) {
                    // Skip positions 4 and 22 to avoid overwriting the soul torches
                    if (glassPositions[i] == 4 || glassPositions[i] == 22) {
                        continue;
                    }
                    inventory.setItem(glassPositions[i], blueGlass[rand.nextInt(blueGlass.length)]);
                }
    
                // Animate the middle with the predefined reward items (scrolling items)
                for (int i = 0; i < 7; i++) {
                    inventory.setItem(10 + i, new ItemStack(normalizedRollItems.get((middleStep + i) % normalizedRollItems.size()).getMaterial()));
                }
    
                // Continue cycling the items and glass panes
                if (waveStep < 40) {
                    waveStep++;
                } else {
                    // After 40 steps, show the final reward in the center and stop the animation
                    cancel();
                    ItemReward reward = getRandomReward(normalizedRollItems);
                    finalizeReward(inventory, player, reward);
                }
    
                // Increment the middleStep for the next scrolling reward
                middleStep++;
            }
        }.runTaskTimer(plugin, 0L, 1L); // Smooth animation with a 5-tick interval
    }    

    private void finalizeReward(Inventory inventory, Player player, ItemReward reward) {
        // Replace the placeholder with the final reward
        ItemStack rewardItem = new ItemStack(reward.getMaterial());
        inventory.setItem(13, rewardItem);  // Set the reward in the center slot

        // Notify the player
        player.sendMessage(ChatColor.GREEN + "You received: " + formatMaterialName(rewardItem.getType()));

        // Add the reward to the player's inventory
        player.getInventory().addItem(rewardItem);

        // Close the GUI after a short delay
        plugin.getServer().getScheduler().runTaskLater(plugin, player::closeInventory, 40L); // Close after 2 seconds
    }

    private ItemReward getRandomReward(List<ItemReward> rewards) {
        int totalWeight = rewards.stream().mapToInt(ItemReward::getWeight).sum();
        Random rand = new Random();
        int randomInt = rand.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (ItemReward reward : rewards) {
            cumulativeWeight += reward.getWeight();
            if (randomInt < cumulativeWeight) {
                return reward;
            }
        }
        return rewards.get(rewards.size() - 1); // In case something goes wrong, return the last item
    }

    private String formatMaterialName(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            formattedName.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
        }
        return formattedName.toString().trim();
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

    // Helper class to store material and weight for rewards
    public static class ItemReward {
        private final Material material;
        private final int weight;

        public ItemReward(Material material, int weight) {
            this.material = material;
            this.weight = weight;
        }

        public Material getMaterial() {
            return material;
        }

        public int getWeight() {
            return weight;
        }
    }
}
