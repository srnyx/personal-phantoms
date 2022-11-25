package xyz.srnyx.personalphantoms;

import org.apache.commons.lang3.StringUtils;

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
import java.nio.file.Files;
import java.util.logging.Logger;


public class PersonalPhantoms extends JavaPlugin {
    public static YamlConfiguration oldData;

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
            try {
                Files.delete(getDataFolder().toPath());
            } catch (final Exception ignored) {
                //ignored
            }
        }
    }
}