package xyz.srnyx.personalphantoms;

import org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.personalphantoms.commands.NoPhantomsCommand;
import xyz.srnyx.personalphantoms.listeners.MobListener;

import java.util.logging.Logger;


public class PersonalPhantoms extends JavaPlugin {
    /**
     * Called when this plugin is enabled
     */
    @Override
    public void onEnable() {
        // Start messages
        final Logger logger = getLogger();
        final String name = getName() + " v" + getDescription().getVersion();
        final String authors = "By " + String.join(", ", getDescription().getAuthors());
        final String line = StringUtils.repeat("-", Math.max(name.length(), authors.length()));
        logger.info(ChatColor.DARK_PURPLE + line);
        logger.info(ChatColor.LIGHT_PURPLE + name);
        logger.info(ChatColor.LIGHT_PURPLE + authors);
        logger.info(ChatColor.DARK_PURPLE + line);

        // Event
        Bukkit.getPluginManager().registerEvents(new MobListener(), this);

        // Command
        final PluginCommand command = getCommand("nophantoms");
        if (command != null) command.setExecutor(new NoPhantomsCommand());
    }

    /**
     * Send a message to a {@link CommandSender} with color codes
     *
     * @param   sender  the {@link CommandSender} to send the message to
     * @param   message the message to send
     */
    public static void sendMessage(@NotNull CommandSender sender, @Nullable String message) {
        if (message == null) message = "null";
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5&lPP&8] &d" + message));
    }
}