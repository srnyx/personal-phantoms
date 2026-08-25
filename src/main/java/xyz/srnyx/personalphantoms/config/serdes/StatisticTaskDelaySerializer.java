package xyz.srnyx.personalphantoms.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.file.okaeri.serdes.duration.DurationSerializer;
import xyz.srnyx.personalphantoms.config.ConfigYml;
import xyz.srnyx.personalphantoms.config.StatisticTaskDelay;


/**
 * @see ConfigYml.StatisticTask#delay
 */
public class StatisticTaskDelaySerializer implements ObjectSerializer<StatisticTaskDelay> {
    @NotNull private final DurationSerializer durationSerializer = new DurationSerializer();

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return StatisticTaskDelay.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull StatisticTaskDelay delay, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        // Automatic
        if (delay.isAutomatic()) {
            data.setValue("automatic");
            return;
        }

        // Specific delay
        data.setValue(delay.delay());
    }

    @Override @NotNull
    public StatisticTaskDelay deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        // Automatic
        final String value = data.getValue(String.class);
        if (value == null || value.equals("automatic")) return new StatisticTaskDelay();

        // Specific delay (with tick fallback)
        return new StatisticTaskDelay(durationSerializer.leftToRight(data.getValue(String.class), data.getReaderContext(ObjectSerializer.VALUE)));
    }
}
