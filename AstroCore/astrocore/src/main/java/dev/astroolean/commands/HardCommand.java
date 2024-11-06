package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HardCommand implements CommandExecutor, Listener {

    private boolean hardModeEnabled = false;
    private final JavaPlugin plugin;
    private final Set<UUID> enhancedEntities = new HashSet<>();

    public HardCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender == null) return true;

        if (!(plugin instanceof dev.astroolean.AstroCore)) {
            sender.sendMessage(ChatColor.RED + "Plugin is not initialized correctly.");
            return true;
        }

        dev.astroolean.AstroCore myPlugin = (dev.astroolean.AstroCore) plugin;

        if (!myPlugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        if (!sender.hasPermission("astrocore.hard")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /hard [enable/disable]");  
            return false;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            hardModeEnabled = true;
            enhancedEntities.clear();
            sender.sendMessage(ChatColor.GREEN + "Hard mode enabled! All hostile mobs are enhanced.");
            enhanceExistingMobs();
            return true;

        } else if (args[0].equalsIgnoreCase("disable")) {
            hardModeEnabled = false;
            revertAllEnhancedMobs();
            sender.sendMessage(ChatColor.RED + "Hard mode disabled. Hostile mobs reverted to normal.");
            return true;
        }

        return false;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Monster && hardModeEnabled && !enhancedEntities.contains(entity.getUniqueId())) {
            applyHardModeEffects((LivingEntity) entity);
            enhancedEntities.add(entity.getUniqueId());
        }

        if (entity instanceof Creeper && hardModeEnabled) {
            ((Creeper) entity).setPowered(true); // Make all creepers charged
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Creeper && hardModeEnabled) {
            ((Creeper) event.getEntity()).setExplosionRadius(10); // Increase charged creeper explosion radius
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (hardModeEnabled && enhancedEntities.contains(event.getEntity().getUniqueId())) {
            event.getDrops().clear();
        }
    }

    private void applyHardModeEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1)); // Increase damage dealt
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // Increase movement speed
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1)); // Increase damage resistance
        entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1)); // Fire resistance
        entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1)); // Regeneration

        // Equip full enchanted netherite armor
        entity.getEquipment().setHelmet(createFullyEnchantedItem(Material.NETHERITE_HELMET));
        entity.getEquipment().setChestplate(createFullyEnchantedItem(Material.NETHERITE_CHESTPLATE));
        entity.getEquipment().setLeggings(createFullyEnchantedItem(Material.NETHERITE_LEGGINGS));
        entity.getEquipment().setBoots(createFullyEnchantedItem(Material.NETHERITE_BOOTS));

        // Equip a powerful weapon (default to Netherite Sword)
        entity.getEquipment().setItemInMainHand(createFullyEnchantedItem(Material.NETHERITE_SWORD));

        if (entity instanceof Creeper creeper) {
            creeper.setPowered(true); // Make creeper charged
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2)); // Increased speed for charged creeper
        } else if (entity instanceof Skeleton skeleton) {
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // Skeleton speed
            skeleton.getEquipment().setItemInMainHand(createEnchantedItem(Material.BOW, Enchantment.ARROW_DAMAGE, 5)); // Enchanted bow for skeletons
        } else if (entity instanceof Zombie zombie) {
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3)); // Increased damage for zombie
            zombie.getEquipment().setItemInMainHand(createEnchantedItem(Material.NETHERITE_SWORD, Enchantment.DAMAGE_ALL, 3)); // Enchanted sword for zombies
        } else if (entity instanceof Blaze blaze) {
            blaze.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1)); // Fire resistance for Blaze
            blaze.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Increased damage for Blaze
        } else if (entity instanceof WitherSkeleton witherSkeleton) {
            witherSkeleton.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 4)); // Even more damage for Wither Skeletons
            witherSkeleton.getEquipment().setItemInMainHand(createFullyEnchantedItem(Material.NETHERITE_SWORD, Enchantment.DAMAGE_ALL, 6)); // Powerful sword for Wither Skeleton
        } else if (entity instanceof Enderman enderman) {
            enderman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2)); // Speed for Enderman
            enderman.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1)); // Invisibility for Enderman
        } else if (entity instanceof Witch witch) {
            witch.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2)); // Regeneration for Witch
            witch.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Increased damage for Witch
        } else if (entity instanceof Ravager ravager) {
            ravager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1)); // Damage resistance for Ravager
            ravager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5)); // Increased damage for Ravager
        } else if (entity instanceof Drowned drowned) {
            drowned.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Increased damage for Drowned
            drowned.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1)); // Water breathing for Drowned
        } else if (entity instanceof Phantom phantom) {
            phantom.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // Speed for Phantom
            phantom.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Increased damage for Phantom
        } else if (entity instanceof Stray stray) {
            stray.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // Speed for Stray
            stray.getEquipment().setItemInMainHand(createEnchantedItem(Material.BOW, Enchantment.ARROW_DAMAGE, 5)); // Enchanted bow for Stray
        } else if (entity instanceof Pillager pillager) {
            pillager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Increased damage for Pillagers
            pillager.getEquipment().setItemInMainHand(createEnchantedItem(Material.CROSSBOW, Enchantment.MULTISHOT, 1)); // Enchanted crossbow for Pillagers
        } else if (entity instanceof Illusioner illusioner) {
            illusioner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1)); // Invisibility for Illusioner
            illusioner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Increased damage for Illusioner
        }
    }

    private ItemStack createEnchantedItem(Material material, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createFullyEnchantedItem(Material material) {
        return createFullyEnchantedItem(material, Enchantment.DURABILITY, 3);
    }

    private ItemStack createFullyEnchantedItem(Material material, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void enhanceExistingMobs() {
        // Enhance all existing hostile mobs in the world
        plugin.getServer().getWorlds().forEach(world -> 
            world.getEntitiesByClass(Monster.class).forEach(monster -> {
                if (!enhancedEntities.contains(monster.getUniqueId())) {
                    applyHardModeEffects(monster);
                    enhancedEntities.add(monster.getUniqueId());
                }
            })
        );
    }

    private void revertAllEnhancedMobs() {
        // Revert all enhanced mobs to their normal state
        enhancedEntities.forEach(uuid -> {
            Entity entity = plugin.getServer().getEntity(uuid);
            if (entity instanceof LivingEntity livingEntity) {
                revertHardModeEffects(livingEntity);
            }
        });
        enhancedEntities.clear(); // Clear the set of enhanced entities after reverting
    }
    
    private void revertHardModeEffects(LivingEntity entity) {
        // Remove any active potion effects
        entity.getActivePotionEffects().forEach(effect -> entity.removePotionEffect(effect.getType()));
        
        // Reset equipment (e.g., remove enchanted gear)
        entity.getEquipment().clear();
        
        // Reset the health of the mob (ensure it is set back to normal)
        if (entity instanceof Monster) {
            entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // Reset health to max health
        }
    
        // Revert specific mob enhancements
        if (entity instanceof Creeper creeper) {
            creeper.setPowered(false); // Revert charged creeper back to normal
        } else if (entity instanceof Skeleton skeleton) {
            skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.AIR)); // Remove enchanted bow
        } else if (entity instanceof Zombie zombie) {
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.AIR)); // Remove enchanted sword
        } else if (entity instanceof Blaze blaze) {
            blaze.removePotionEffect(PotionEffectType.FIRE_RESISTANCE); // Remove fire resistance
            blaze.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
        } else if (entity instanceof WitherSkeleton witherSkeleton) {
            witherSkeleton.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
            witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.AIR)); // Remove enchanted sword
        } else if (entity instanceof Enderman enderman) {
            enderman.removePotionEffect(PotionEffectType.SPEED); // Remove speed boost
            enderman.removePotionEffect(PotionEffectType.INVISIBILITY); // Remove invisibility
        } else if (entity instanceof Witch witch) {
            witch.removePotionEffect(PotionEffectType.REGENERATION); // Remove regeneration
            witch.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
        } else if (entity instanceof Ravager ravager) {
            ravager.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE); // Remove damage resistance
            ravager.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
        } else if (entity instanceof Drowned drowned) {
            drowned.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
            drowned.removePotionEffect(PotionEffectType.WATER_BREATHING); // Remove water breathing
        } else if (entity instanceof Phantom phantom) {
            phantom.removePotionEffect(PotionEffectType.SPEED); // Remove speed boost
            phantom.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
        } else if (entity instanceof Stray stray) {
            stray.getEquipment().setItemInMainHand(new ItemStack(Material.AIR)); // Remove enchanted bow
        } else if (entity instanceof Pillager pillager) {
            pillager.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
            pillager.getEquipment().setItemInMainHand(new ItemStack(Material.AIR)); // Remove enchanted crossbow
        } else if (entity instanceof Illusioner illusioner) {
            illusioner.removePotionEffect(PotionEffectType.INVISIBILITY); // Remove invisibility
            illusioner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Remove damage boost
        }
    }
    
}
