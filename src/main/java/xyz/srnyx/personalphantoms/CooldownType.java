package xyz.srnyx.personalphantoms;

import org.jetbrains.annotations.Contract;

import xyz.srnyx.annoyingapi.AnnoyingCooldown;


public enum CooldownType implements AnnoyingCooldown.CooldownType {
    NO_PHANTOMS(30000);

    private final long duration;

    @Contract(pure = true)
    CooldownType(long duration) {
        this.duration = duration;
    }

    @Override @Contract(pure = true)
    public long getDuration() {
        return duration;
    }
}
