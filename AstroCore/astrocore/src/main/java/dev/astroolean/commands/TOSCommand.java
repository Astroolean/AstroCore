package dev.astroolean.commands;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import dev.astroolean.AstroCore;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class TOSCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;
    private final HashSet<UUID> frozenPlayers = new HashSet<>();
    private final String playerDataFolderPath;

    public TOSCommand(AstroCore plugin) {
        this.plugin = plugin;
        this.playerDataFolderPath = plugin.getDataFolder() + File.separator + "PlayerData"; // Path to PlayerData folder
        createTOSFile();
        createPlayerDataFolder(); // Ensure the PlayerData folder exists
        plugin.getServer().getPluginManager().registerEvents(this, plugin); // Register event listener
    }

    private void createTOSFile() {
        File file = new File(plugin.getDataFolder(), "TOS.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Create directories if they do not exist
                
                // Creating a structured TOS with easy customization
                String defaultTOS = """
                    # Terms of Service
                    # =================
                    rules:
                      - "&c(✘) Do not use hacks on this Server!"
                      - "&4(✘) You are not allowed to use X-Ray"
                      - "&a(✔) Respect all players and staff!"
                      - "&b(✔) Follow all server rules."
                      - "&e(✎) Customize these rules to fit your server’s needs."
                      - "&2(★) &bUse color codes for enhanced readability."
    
                    information:
                      welcome: "&6Welcome to the server! Please review and accept the Terms of Service to continue."
                      accept: "Type /tos accept to agree to the Terms of Service."
                      deny: "Type /tos deny to decline the Terms of Service."
    
                    links:
                      website: "&6https://github.com/Astroolean"
                """;
    
                Files.write(Paths.get(file.toURI()), defaultTOS.getBytes());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create Terms of Service file: {0}", e.getMessage());
            }
        }
    }
    

    private void createPlayerDataFolder() {
        File playerDataFolder = new File(playerDataFolderPath);
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs(); // Create PlayerData folder if it does not exist
        }
    }

public void freezePlayer(Player player) {
    if (!isFrozen(player)) {
        frozenPlayers.add(player.getUniqueId());
        player.setWalkSpeed(0); // Freezing player by setting walk speed to 0
        player.setInvulnerable(true); // Make player invincible
        
        // Set the player's health to maximum using the Attribute system
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            player.setHealth(healthAttribute.getValue()); // Ensure the player is at full health
        }
    }
}

public void unfreezePlayer(Player player) {
    if (isFrozen(player)) {
        frozenPlayers.remove(player.getUniqueId());
        player.setWalkSpeed(0.2f); // Resetting walk speed
        player.setInvulnerable(false); // Remove invincibility
    }
}

    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }

    public void displayTerms(Player player) {
        player.sendMessage(ChatColor.GOLD + "Please read and accept the Terms of Service:");
        File file = new File(plugin.getDataFolder(), "TOS.yml");

        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            // Send welcome message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("information.welcome")));

            // Load and send each rule in the list
            List<String> rules = config.getStringList("rules");
            for (String rule : rules) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', rule));
            }

            // Send acceptance and denial messages
            player.sendMessage(ChatColor.GREEN + config.getString("information.accept"));
            player.sendMessage(ChatColor.RED + config.getString("information.deny"));

            // Create a clickable link component for the website
            String linkText = ChatColor.translateAlternateColorCodes('&', config.getString("links.website"));
            TextComponent link = new TextComponent(linkText);
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Astroolean"));

            // Send the clickable link to the player
            player.spigot().sendMessage(link);

        } else {
            player.sendMessage(ChatColor.RED + "Terms of Service file not found!");
        }
    }

    private void savePlayerData(Player player) {
        File playerFile = new File(playerDataFolderPath, player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("accepted", true); // Mark player as having accepted the TOS
        try {
            config.save(playerFile); // Save the player's acceptance
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data: {0}", e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("astrocore.tos")) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        File playerFile = new File(playerDataFolderPath, player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        if (config.getBoolean("accepted", false)) {
            player.sendMessage(ChatColor.YELLOW + "You have already accepted the Terms of Service.");
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("accept")) {
                unfreezePlayer(player);
                savePlayerData(player);
                player.sendMessage(ChatColor.GREEN + "You have accepted the Terms of Service.");
                return true;
            } else if (args[0].equalsIgnoreCase("deny")) {
                unfreezePlayer(player);
                player.kickPlayer(ChatColor.RED + "You need to accept the server's Terms of Service to play.");
                return true;
            }
        }

        displayTerms(player);
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File playerFile = new File(playerDataFolderPath, player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        if (config.getBoolean("accepted", false)) {
            return;
        }

        freezePlayer(player);
        displayTerms(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            event.setTo(event.getFrom()); // Prevent movement
        }
    }
}
