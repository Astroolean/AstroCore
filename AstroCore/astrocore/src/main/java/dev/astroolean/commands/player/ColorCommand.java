package dev.astroolean.commands.player;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;

import dev.astroolean.AstroCore;

public class ColorCommand implements CommandExecutor, Listener {
    private final AstroCore plugin;
    private final Map<String, String[]> colors = new LinkedHashMap<>();

    public ColorCommand(AstroCore plugin) {
        this.plugin = plugin;

        // Initialize the color map with names and their corresponding details
        colors.put("white", new String[]{"§f", "<white>"});
        colors.put("yellow", new String[]{"§e", "<yellow>"});
        colors.put("light_purple", new String[]{"§d", "<light_purple>"});
        colors.put("aqua", new String[]{"§b", "<aqua>"});
        colors.put("green", new String[]{"§a", "<green>"});
        colors.put("red", new String[]{"§c", "<red>"});
        colors.put("gray", new String[]{"§7", "<gray>"});
        colors.put("dark_gray", new String[]{"§8", "<dark_gray>"});
        colors.put("blue", new String[]{"§9", "<blue>"});
        colors.put("dark_aqua", new String[]{"§3", "<dark_aqua>"});
        colors.put("dark_green", new String[]{"§2", "<dark_green>"});
        colors.put("dark_red", new String[]{"§4", "<dark_red>"});
        colors.put("dark_purple", new String[]{"§5", "<dark_purple>"});
        colors.put("dark_blue", new String[]{"§1", "<dark_blue>"});
        colors.put("black", new String[]{"§0", "<black>"});
        colors.put("gold", new String[]{"§6", "<gold>"});
        colors.put("bold", new String[]{"§l", "<bold>"});
        colors.put("underline", new String[]{"§n", "<underline>"});
        colors.put("italic", new String[]{"§o", "<italic>"});
        colors.put("strikethrough", new String[]{"§m", "<strikethrough>"});
        colors.put("magic", new String[]{"§k", "<magic>"});
        colors.put("reset", new String[]{"§r", "<reset>"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("astrocore.color")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (!plugin.isEnabledCustom()) {
            sender.sendMessage(ChatColor.RED + "AstroCore plugin is currently disabled.");
            return true;
        }

        // If no arguments are provided, show available colors
        if (args.length == 0) {
            player.sendMessage(ChatColor.GREEN + "Available Colors and Usage:");
            for (Map.Entry<String, String[]> entry : colors.entrySet()) {
                String name = entry.getKey();
                String chatCode = entry.getValue()[0];

                player.sendMessage(chatCode + "This is a sample text in " + name + ChatColor.RESET +
                        ChatColor.YELLOW + " (Use '&" + chatCode.replace("§", "") + "' for this color)");
            }

            player.sendMessage(ChatColor.AQUA + "Example usage: " + ChatColor.GREEN + "&cThis text will be red!");
            return true;
        }

        // Check if a valid color is specified
        String colorName = args[0].toLowerCase(); // Convert the input to lowercase
        String[] color = colors.get(colorName); // Use the lowercase name directly
        if (color != null) {
            // Store the selected color in player metadata
            player.setMetadata("chatColor", new FixedMetadataValue(plugin, color[0]));
            player.sendMessage(ChatColor.GREEN + "Chat color changed to " + color[0] + colorName + ChatColor.RESET + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Invalid color. Please choose a valid color.");
        }

        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // Check if the player has a color set
        if (player.hasMetadata("chatColor")) {
            String color = player.getMetadata("chatColor").get(0).asString();
            String message = color + event.getMessage(); // Prepend the color to the message
            event.setMessage(message);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++) {
            lines[i] = convertColorCodes(lines[i]);
            event.setLine(i, lines[i]); // Set each line individually
        }
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        // Get the new book meta; it should never be null in a valid event
        BookMeta bookMeta = event.getNewBookMeta(); 
        
        // Color the title safely; no need to check for null
        String title = bookMeta.getTitle(); // Get the title
        if (!title.isEmpty()) {
            bookMeta.setTitle(ChatColor.translateAlternateColorCodes('&', title)); // Translate title
        }

        // Color the pages directly
        for (int i = 0; i < bookMeta.getPageCount(); i++) {
            String page = bookMeta.getPage(i + 1); // Pages are 1-indexed
            // Only translate and set the page if it's not empty
            if (!page.isEmpty()) {
                bookMeta.setPage(i + 1, ChatColor.translateAlternateColorCodes('&', page)); 
            }
        }

        // Set the updated BookMeta back to the event
        event.setNewBookMeta(bookMeta); 
    }
    
    // Add the convertColorCodes method
    private String convertColorCodes(String input) {
        if (input == null) {
            return ""; // Handle null input gracefully
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
