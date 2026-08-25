package xyz.srnyx.personalphantoms.stats;

import dev.faststats.Metrics;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.stats.loader.FastStatsLoader;
import xyz.srnyx.personalphantoms.PersonalPhantoms;


public class FastStats extends FastStatsLoader {
    @NotNull private final PersonalPhantoms plugin;

    public FastStats(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public PersonalPhantoms getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getId() {
        return "229ba0422c7634824c437f4297643e54";
    }

    @Override
    public void mutateMetricsFactory(@NotNull Metrics.Factory factory) {
        factory.addMetric(config("config", () -> plugin.config));
    }
}
