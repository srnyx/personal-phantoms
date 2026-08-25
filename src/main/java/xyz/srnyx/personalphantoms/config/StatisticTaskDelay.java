package xyz.srnyx.personalphantoms.config;

import com.google.gson.JsonPrimitive;
import eu.okaeri.validator.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.stats.Statable;

import java.time.Duration;


public record StatisticTaskDelay(@Nullable Duration delay) implements Statable {
    public StatisticTaskDelay() {
        this(null);
    }

    public boolean isAutomatic() {
        return this.delay == null;
    }

    @Override @NotNull
    public JsonPrimitive toStat() {
        return isAutomatic() ? new JsonPrimitive("automatic") : new JsonPrimitive(this.delay.toMillis());
    }
}
