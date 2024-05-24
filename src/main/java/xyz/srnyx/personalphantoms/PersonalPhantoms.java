package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;
import xyz.srnyx.annoyingapi.data.EntityData;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PersonalPhantoms extends AnnoyingPlugin {
    @NotNull public static final String KEY = "pp_no-phantoms";

    @Nullable public BukkitTask task;
    @Nullable public Set<String> worldsBlacklist;
    public boolean treatBlacklistAsWhitelist;

    public PersonalPhantoms() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(
                        PluginPlatform.modrinth("personal-phantoms"),
                        PluginPlatform.hangar(this, "srnyx"),
                        PluginPlatform.spigot("106381")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(18328))
                .dataOptions(dataOptions -> dataOptions
                        .enabled(true)
                        .entityDataColumns(KEY))
                .registrationOptions
                .toRegister(this, NoPhantomsCmd.class, MobListener.class)
                .papiExpansionToRegister(() -> new PersonalPlaceholders(this));
    }

    @Override
    public void enable() {
        reload();
    }

    @Override
    public void reload() {
        // Config
        final AnnoyingResource config = new AnnoyingResource(this, "config.yml");
        final ConfigurationSection worldsBlacklistedSection = config.getConfigurationSection("worlds-blacklist");
        if (worldsBlacklistedSection != null) {
            final List<String> list = worldsBlacklistedSection.getStringList("list");
            treatBlacklistAsWhitelist = worldsBlacklistedSection.getBoolean("treat-as-whitelist");
            worldsBlacklist = list.isEmpty() && !treatBlacklistAsWhitelist ? null : new HashSet<>(list);
        } else {
            worldsBlacklist = null;
        }

        // Runnable task
        final long checkInterval = config.getInt("check-interval", 600) * 20L;
        if (task != null) task.cancel();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) if (shouldResetStatistic(player)) resetStatistic(player);
            }
        }.runTaskTimer(this, checkInterval, checkInterval);
    }

    public boolean inWhitelistedWorld(@NotNull Player player) {
        return worldsBlacklist == null || (worldsBlacklist.contains(player.getWorld().getName()) != treatBlacklistAsWhitelist);
    }

    public boolean shouldResetStatistic(@NotNull Player player) {
        return inWhitelistedWorld(player) && new EntityData(this, player).has(KEY);
    }

    public static void resetStatistic(@NotNull Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
    }
}
