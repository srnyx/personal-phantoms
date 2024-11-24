package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPAPIExpansion;
import xyz.srnyx.annoyingapi.data.EntityData;


public class PersonalPlaceholders extends AnnoyingPAPIExpansion {
    @NotNull private final PersonalPhantoms plugin;

    public PersonalPlaceholders(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public PersonalPhantoms getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getIdentifier() {
        return "phantoms";
    }

    @Override @Nullable
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String identifier) {
        // status
        if (player != null && identifier.equals("status")) return String.valueOf(new EntityData(plugin, player).has(PersonalPhantoms.KEY));

        // status_<player>
        if (identifier.startsWith("status_")) {
            final Player target = Bukkit.getPlayer(identifier.substring(7));
            return target == null ? "N/A" : String.valueOf(new EntityData(plugin, target).has(PersonalPhantoms.KEY));
        }

        return null;
    }
}
