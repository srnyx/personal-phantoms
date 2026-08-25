package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.data.StringData;
import xyz.srnyx.annoyingapi.scheduler.TaskWrapper;
import xyz.srnyx.annoyingapi.utility.DurationUtility;
import xyz.srnyx.personalphantoms.config.ConfigYml;
import xyz.srnyx.personalphantoms.config.serdes.StatisticTaskDelaySerializer;
import xyz.srnyx.personalphantoms.messages.PersonalMessagesProvider;
import xyz.srnyx.personalphantoms.stats.FastStats;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


public class PersonalPhantoms extends AnnoyingPlugin {
    @NotNull public static final String KEY = "pp_no-phantoms";

    public ConfigYml config;
    @NotNull private final Map<String, TaskWrapper> tasks = new HashMap<>();

    public PersonalPhantoms() {
        options
                .statsOptions(statsOptions -> statsOptions
                        .bStats(bStatsOptions -> bStatsOptions.id(18328))
                        .fastStats(fastStatsOptions -> fastStatsOptions.loader(FastStats.class)))
                .dataOptions(dataOptions -> dataOptions
                        .entityDataColumns(KEY))
                .registrationOptions.papiExpansionToRegister(() -> new PersonalPlaceholders(this));
    }

    @Override @NotNull
    public PersonalMessagesProvider getMessages() {
        return (PersonalMessagesProvider) super.getMessages();
    }

    @Override
    public void load() {
        config = configLoader.build(builder -> builder
                .config(new ConfigYml())
                .configure(configure -> configure.serdes(new StatisticTaskDelaySerializer())));
    }

    @Override
    public void enable() {
        startTasks();
    }

    @Override
    public void reload() {
        config.reload();
        startTasks();
    }

    private void startTasks() {
        // Cancel previous tasks
        for (final TaskWrapper task : tasks.values()) task.cancel();

        // Start tasks
        final Duration delay = config.statistic_task.delay.delay();
        final Duration period = config.statistic_task.period;
        for (final World world : Bukkit.getWorlds()) {
            final String name = world.getName();
            if (!isWhitelistedWorld(world)) continue;

            // Cancel previous task
            final TaskWrapper previousTask = tasks.get(name);
            if (previousTask != null) previousTask.cancel();

            // Get time & isNight
            // Daytime: 0-12000
            // Nighttime: 12000-24000
            final long time = world.getTime();
            final boolean isNight = time >= 12000;

            // Run immediately if nighttime
            if (isNight) resetAllStatistics(world);

            // Get delay
            Duration worldDelay = delay; // Configured specific delay
            if (worldDelay == null) worldDelay = DurationUtility.ticksToDuration(isNight ? 36000 - time : 12000 - time); // Automatic calculation

            // Start periodic task
            tasks.put(name, scheduler.runGlobalTaskTimer(
                    task -> resetAllStatistics(world),
                    worldDelay.toSeconds() / 50,
                    period.toMillis() / 50));
        }
    }

    public boolean hasPhantomsEnabled(@NotNull StringData data) {
        return data.getOptional(KEY)
                .map(value -> !value.equals("true"))
                .orElse(config.def);
    }

    public boolean hasPhantomsEnabled(@NotNull OfflinePlayer player) {
        return hasPhantomsEnabled(new StringData(this, player));
    }

    private void resetAllStatistics(@NotNull World world) {
        for (final Player player : world.getPlayers()) if (!hasPhantomsEnabled(player)) resetStatistic(player);
    }

    public boolean isWhitelistedWorld(@NotNull World world) {
        return config.worlds_blacklist.list.contains(world.getName()) == config.worlds_blacklist.treat_as_whitelist;
    }

    public static void resetStatistic(@NotNull Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
    }
}
