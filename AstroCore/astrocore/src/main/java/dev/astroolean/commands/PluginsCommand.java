package dev.astroolean.commands;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginsCommand implements CommandExecutor {
    @SuppressWarnings("*")
    private final JavaPlugin plugin;

    public PluginsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is not null
        if (sender == null) {
            return true; // No action needed for null sender
        }

        // Check if the sender has permission for the "p" command
        if (!sender.hasPermission("astrocore.plugins")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
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

        // Check if the sender is a player
        if (sender instanceof Player player) {
            if (player.isOp()) {
                // Header with &9 color
                player.sendMessage("§9Installed Plugins:");

                // List each plugin name with the correct color based on its status
                for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                    TextComponent pluginName = new TextComponent();

                    // Determine the color based on whether the plugin is enabled
                    if (pl.isEnabled()) {
                        pluginName.setText("§b" + pl.getName()); // Light blue for enabled
                    } else {
                        pluginName.setText("§1" + pl.getName()); // Dark blue for disabled
                    }

                    // Hover text with plugin information
                    String pluginInfo = String.format("§b%s §7v%s - §7%s",
                            pl.getDescription().getName(),
                            pl.getDescription().getVersion(),
                            pl.getDescription().getDescription());

                    // Use new Text content for HoverEvent
                    pluginName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(pluginInfo)));

                    // Send message to the player with hover info but no click action
                    player.spigot().sendMessage(pluginName);
                }
            } else {
                player.sendMessage("§cPlugins are not available to non-opped players.");
            }
        } else {
            // If the command is run from the console
            sender.sendMessage("This command can only be run by a player.");
        }
        return true;
    }
}
