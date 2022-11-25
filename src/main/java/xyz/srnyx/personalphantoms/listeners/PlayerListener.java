package xyz.srnyx.personalphantoms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import xyz.srnyx.personalphantoms.PersonalPhantoms;

import java.nio.file.Files;


/**
 * @deprecated  this listener is only used for old data conversion
 */
@Deprecated(forRemoval = true, since = "1.0.1")
public class PlayerListener implements Listener {
    private final PersonalPhantoms plugin;
    @Contract(pure = true)
    public PlayerListener(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a player joins a server
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        if (PersonalPhantoms.oldData == null) return;
        final Player player = event.getPlayer();
        final String uuid = player.getUniqueId().toString();

        // Convert old data to new data
        if (PersonalPhantoms.oldData.getStringList("no-phantoms").contains(uuid)) {
            player.addScoreboardTag("pp_no-phantoms");
            PersonalPhantoms.oldData.set("no-phantoms", PersonalPhantoms.oldData.getStringList("no-phantoms").remove(uuid));
        }

        // Delete data file if list is empty
        if (PersonalPhantoms.oldData.getStringList("no-phantoms").isEmpty()) {
            PersonalPhantoms.oldData = null;
            try {
                Files.delete(plugin.getDataFolder().toPath());
            } catch (final Exception ignored) {
                //ignored
            }
        }
    }
}
