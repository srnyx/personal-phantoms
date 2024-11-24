package xyz.srnyx.personalphantoms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ConfigYml {
    @NotNull private final AnnoyingResource config;

    public final long commandCooldown;
    @NotNull public StatisticTask statisticTask;
    @NotNull public WorldsBlacklist worldsBlacklist;

    public ConfigYml(@NotNull AnnoyingPlugin plugin) {
        config = new AnnoyingResource(plugin, "config.yml");
        commandCooldown = config.getLong("command-cooldown", 600) * 1000; // default: 10 minutes
        statisticTask = new StatisticTask();
        worldsBlacklist = new WorldsBlacklist();
    }

    public class StatisticTask {
        @Nullable public final Long delay = config.getString("statistic-task.delay", "automatic").equals("automatic") ? null : config.getLong("statistic-task.delay"); // default: automatic
        public final long period = config.getLong("statistic-task.period", 24000); // default: 20 minutes (1 in-game day)
    }

    public class WorldsBlacklist {
        @Nullable public final Set<String> list;
        public final boolean treatAsWhitelist = config.getBoolean("worlds-blacklist.treat-as-whitelist");

        public WorldsBlacklist() {
            final List<String> stringList = config.getStringList("worlds-blacklist.list");
            this.list = stringList.isEmpty() && !treatAsWhitelist ? null : new HashSet<>(stringList);
        }
    }
}
