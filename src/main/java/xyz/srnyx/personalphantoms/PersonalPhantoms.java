package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;
import xyz.srnyx.annoyingapi.data.EntityData;

import java.util.HashMap;
import java.util.Map;


public class PersonalPhantoms extends AnnoyingPlugin {
    @NotNull public static final String KEY = "pp_no-phantoms";

    public ConfigYml config;
    @NotNull private final Map<String, BukkitTask> tasks = new HashMap<>();

    public PersonalPhantoms() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(
                        PluginPlatform.modrinth("lzjYdd5h"),
                        PluginPlatform.hangar(this),
                        PluginPlatform.spigot("106381")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(18328))
                .dataOptions(dataOptions -> dataOptions
                        .enabled(true)
                        .entityDataColumns(KEY))
                .registrationOptions
                .automaticRegistration(automaticRegistration -> automaticRegistration.packages(
                        "xyz.srnyx.personalphantoms.commands",
                        "xyz.srnyx.personalphantoms.listeners"))
                .papiExpansionToRegister(() -> new PersonalPlaceholders(this));
    }

    @Override
    public void enable() {
        reload();
    }

    @Override
    public void reload() {
        config = new ConfigYml(this);

        // Start tasks
        final Long delay = config.statisticTask.delay;
        final long period = config.statisticTask.period;
        for (final World world : Bukkit.getWorlds()) {
            final String name = world.getName();
            if (!isWhitelistedWorld(world)) continue;

            // Cancel previous task
            final BukkitTask previousTask = tasks.get(name);
            if (previousTask != null) previousTask.cancel();

            // Get time & isNight
            // Daytime: 0-12000
            // Nighttime: 12000-24000
            final long time = world.getTime();
            final boolean isNight = time >= 12000;

            // Run immediately if nighttime
            if (isNight) resetAllStatistics(world);

            // Get delay
            Long worldDelay = delay; // Configured specific delay
            if (worldDelay == null) worldDelay = isNight ? 36000 - time : 12000 - time; // Automatic calculation

            // Start periodic task
            tasks.put(name, new BukkitRunnable() {
                @Override
                public void run() {
                    resetAllStatistics(world);
                }
            }.runTaskTimer(this, worldDelay, period));
        }
    }

    private void resetAllStatistics(@NotNull World world) {
        for (final Player player : world.getPlayers()) if (new EntityData(this, player).has(KEY)) resetStatistic(player);
    }

    public boolean isWhitelistedWorld(@NotNull World world) {
        return config.worldsBlacklist.list == null || config.worldsBlacklist.list.contains(world.getName()) == config.worldsBlacklist.treatAsWhitelist;
    }

    public static void resetStatistic(@NotNull Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
    }
}
