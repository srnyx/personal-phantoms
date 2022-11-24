package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.srnyx.personalphantoms.commands.NoPhantomsCommand;
import xyz.srnyx.personalphantoms.listeners.MobListener;
import xyz.srnyx.personalphantoms.listeners.PlayerListener;

import java.io.File;
import java.util.logging.Logger;


public class PersonalPhantoms extends JavaPlugin {
    public static YamlConfiguration oldData;

    /**
     * Called when this plugin is enabled
     */
    @Override
    public void onEnable() {
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

        // Events
        final PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new MobListener(), this);
        manager.registerEvents(new PlayerListener(this), this);

        // Command
        final PluginCommand command = Bukkit.getPluginCommand("nophantoms");
        if (command != null) command.setExecutor(new NoPhantomsCommand());

        getOldData();
    }

    /**
     * Gets the plugin's old data {@link YamlConfiguration}
     *
     * @deprecated  this listener is only used for old data conversion
     */
    @Deprecated(forRemoval = true, since = "1.0.1")
    private void getOldData() {
        // Get old data
        final File oldFile = new File(getDataFolder(), "data.yml");
        if (oldFile.exists()) {
            oldData = YamlConfiguration.loadConfiguration(oldFile);
        } else {
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().delete();
        }
    }
}