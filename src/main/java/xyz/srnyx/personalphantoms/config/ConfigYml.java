package xyz.srnyx.personalphantoms.config;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.NotNull;
import org.jspecify.annotations.NonNull;
import xyz.srnyx.annoyingapi.file.okaeri.RootConfig;
import xyz.srnyx.annoyingapi.file.okaeri.SubConfig;
import xyz.srnyx.annoyingapi.file.okaeri.serdes.duration.DurationTickFallback;
import xyz.srnyx.annoyingapi.stats.Stat;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;


public class ConfigYml extends RootConfig {
    @Comment("The default toggle state for phantoms (true = phantoms enabled, false = phantoms disabled)")
    @Stat
    @CustomKey("default") public boolean def = true;

    @Comment
    @Comment("How long until a player can toggle phantoms again")
    @Comment("You can give permission holders the 'pp.nophantoms.bypass' permission to ignore this cooldown")
    @Comment("Or, you can give the 'pp.nophantoms.cooldown.#' permission to set a custom cooldown (replace # with the cooldown time in seconds)")
    @DurationSpec(fallbackUnit = ChronoUnit.SECONDS) @Stat
    @NotNull public Duration command_cooldown = Duration.ofMinutes(10);

    @Comment
    @Comment("The plugin works by resetting player sleep statistics so that phantoms never spawn")
    @Comment("These options relate to how often the plugin should reset player sleep statistics")
    @NotNull public StatisticTask statistic_task = new StatisticTask(this);

    @Comment
    @Comment("A list of worlds where the plugin won't be enabled")
    @Stat(sizeOnly = true)
    @NotNull public WorldsBlacklist worlds_blacklist = new WorldsBlacklist(this);

    public static class StatisticTask extends SubConfig<ConfigYml, ConfigYml> {
        public StatisticTask(@NonNull ConfigYml root) {
            super(root);
        }

        @Comment
        @Comment("The delay before the first run of the task (since plugin load)")
        @Comment("automatic = the plugin will automatically calculate the best delay based on the current time of day in-game (per world)")
        @Stat
        @NotNull public StatisticTaskDelay delay = new StatisticTaskDelay();

        @Comment
        @Comment("The period between each run of the task")
        @Comment("Default: 20m = 20 minutes = 1 full Minecraft day")
        @DurationTickFallback @Stat
        @NotNull public Duration period = Duration.ofMinutes(20); // Default: 1 in-game day
    }

    public static class WorldsBlacklist extends SubConfig<ConfigYml, ConfigYml> {
        public WorldsBlacklist(@NonNull ConfigYml root) {
            super(root);
        }

        @Stat(sizeOnly = true)
        @NotNull public Set<String> list = Collections.emptySet();

        @Comment
        @Comment("If true, the list of worlds above will act as a whitelist, meaning only those worlds will have the plugin enabled")
        @Stat
        public boolean treat_as_whitelist;
    }
}
