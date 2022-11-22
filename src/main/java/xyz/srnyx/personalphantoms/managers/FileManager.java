package xyz.srnyx.personalphantoms.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import xyz.srnyx.personalphantoms.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileManager {
    private final Main plugin;
    @Contract(pure = true)
    public FileManager(@NotNull Main plugin) {
        this.plugin = plugin;
    }

    public static YamlConfiguration data = new YamlConfiguration();

    /**
     * Loads the data.yml file
     */
    public void loadData() {
        try {
            data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
            FileManager.data.getStringList("no-phantoms").forEach(uuid -> ListManager.list.add(UUID.fromString(uuid)));
        } catch (Exception ignored) {
            // ignored
        }
    }

    /**
     * Saves the data.yml file
     */
    public void saveData() {
        if (ListManager.list.isEmpty()) return;

        // Create plugin folder
        final File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) pluginFolder.mkdir();

        // Save list to data
        final List<String> list = new ArrayList<>();
        ListManager.list.forEach(uuid -> list.add(uuid.toString()));
        data.set("no-phantoms", list);

        // Save data to file
        final File dataFile = new File(plugin.getDataFolder(), "data.yml");
        try {
            dataFile.createNewFile();
            data.save(dataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
