package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.srnyx.personalphantoms.managers.FileManager;

import java.util.logging.Logger;


public class Main extends JavaPlugin {
    /**
     * Called when this plugin is enabled
     */
    @Override
    public void onEnable() {
        new FileManager(this).loadData();

        // Start messages
        final StringBuilder authors = new StringBuilder();
        boolean first = true;
        for (final String author : getDescription().getAuthors()) {
            if (first) {
                authors.append(author);
                first = false;
                continue;
            }
            authors.append(", ").append(author);
        }
        final Logger logger = getLogger();
        logger.info(ChatColor.DARK_PURPLE + "-------------------------");
        logger.info(ChatColor.LIGHT_PURPLE + " " + getName() + " v" + getDescription().getVersion());
        logger.info(ChatColor.LIGHT_PURPLE + "    Created by " + authors);
        logger.info(ChatColor.DARK_PURPLE + "-------------------------");

        // Event
        Bukkit.getPluginManager().registerEvents(new MobListener(), this);

        // Command
        final PluginCommand command = Bukkit.getPluginCommand("nophantoms");
        if (command != null) command.setExecutor(new NoPhantomsCommand());
    }

    /**
     * Called when this plugin is disabled
     */
    @Override
    public void onDisable() {
        new FileManager(this).saveData();
    }
}