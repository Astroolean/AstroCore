package dev.astroolean.commands;

import dev.astroolean.AstroCore;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AutoArmorCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;
    private boolean autoArmorEnabled = false;

    public AutoArmorCommand(AstroCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return true; // No action needed for null sender
        }

        if (!(plugin instanceof dev.astroolean.AstroCore)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }

        dev.astroolean.AstroCore myPlugin = (dev.astroolean.AstroCore) plugin;

        if (!myPlugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("astrocore.autoarmor")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /autoarmor [enable/disable]");
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            autoArmorEnabled = true;
            player.sendMessage(ChatColor.GREEN + "Auto armor is now enabled.");
            checkAndEquipArmor(player);
        } else if (args[0].equalsIgnoreCase("disable")) {
            autoArmorEnabled = false;
            player.sendMessage(ChatColor.RED + "Auto armor is now disabled.");
        } else {
            player.sendMessage(ChatColor.RED + "Invalid argument. Use 'enable' or 'disable'.");
        }

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (autoArmorEnabled && event.getWhoClicked() instanceof Player player) {
            checkAndEquipArmor(player);
            // Prevent removing armor from the inventory
            if (isArmorSlot(event.getSlot()) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot unequip armor while Auto Armor is enabled.");
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (autoArmorEnabled && event.getWhoClicked() instanceof Player player) {
            checkAndEquipArmor(player);
            for (int slot : event.getRawSlots()) {
                if (isArmorSlot(slot)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot unequip armor while Auto Armor is enabled.");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (autoArmorEnabled && event.getEntity() instanceof Player player) {
            checkAndEquipArmor(player);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (autoArmorEnabled && isArmorItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop armor while Auto Armor is enabled.");
        }
    }

    public void checkAndEquipArmor(Player player) {
        if (!autoArmorEnabled) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack currentHelmet = playerInventory.getHelmet();
        ItemStack currentChestplate = playerInventory.getChestplate();
        ItemStack currentLeggings = playerInventory.getLeggings();
        ItemStack currentBoots = playerInventory.getBoots();

        ItemStack bestHelmet = currentHelmet;
        ItemStack bestChestplate = currentChestplate;
        ItemStack bestLeggings = currentLeggings;
        ItemStack bestBoots = currentBoots;

        // Find the best armor items in the inventory
        for (ItemStack item : playerInventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            // Debugging: Log the item type
            System.out.println("Item found: " + item.getType()); // Log item type to console

            // Compare and update the best armor pieces
            if (isHelmet(item) && (bestHelmet == null || getArmorValue(item) > getArmorValue(bestHelmet))) {
                bestHelmet = item;
            } else if (isChestplate(item) && (bestChestplate == null || getArmorValue(item) > getArmorValue(bestChestplate))) {
                bestChestplate = item;
            } else if (isLeggings(item) && (bestLeggings == null || getArmorValue(item) > getArmorValue(bestLeggings))) {
                bestLeggings = item;
            } else if (isBoots(item) && (bestBoots == null || getArmorValue(item) > getArmorValue(bestBoots))) {
                bestBoots = item;
            }
        }

        // Equip the best armor in each slot only if it's better than the current item
        if (bestHelmet != currentHelmet) {
            playerInventory.setHelmet(bestHelmet);
            playerInventory.removeItem(bestHelmet); // Remove the item from inventory
        }
        if (bestChestplate != currentChestplate) {
            playerInventory.setChestplate(bestChestplate);
            playerInventory.removeItem(bestChestplate); // Remove the item from inventory
        }
        if (bestLeggings != currentLeggings) {
            playerInventory.setLeggings(bestLeggings);
            playerInventory.removeItem(bestLeggings); // Remove the item from inventory
        }
        if (bestBoots != currentBoots) {
            playerInventory.setBoots(bestBoots);
            playerInventory.removeItem(bestBoots); // Remove the item from inventory
        }
    }

    private boolean isArmorSlot(int slot) {
        return slot == 36 || slot == 37 || slot == 38 || slot == 39; // Helmet, Chestplate, Leggings, Boots
    }

    private boolean isArmorItem(ItemStack item) {
        return isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item);
    }

    private boolean isHelmet(ItemStack item) {
        return item.getType().toString().endsWith("_HELMET");
    }

    private boolean isChestplate(ItemStack item) {
        return item.getType().toString().endsWith("_CHESTPLATE");
    }

    private boolean isLeggings(ItemStack item) {
        return item.getType().toString().endsWith("_LEGGINGS");
    }

    private boolean isBoots(ItemStack item) {
        return item.getType().toString().endsWith("_BOOTS");
    }

    private int getArmorValue(ItemStack item) {
        return switch (item.getType()) {
            case NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS -> 5;
            case DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS -> 4;
            case IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS -> 3;
            case CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS -> 2;
            case GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS -> 1;
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> 1;
            default -> 0;
        };
    }
}
