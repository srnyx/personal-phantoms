package xyz.srnyx.personalphantoms.managers;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class ListManager {
    public static final Set<UUID> list = new HashSet<>();
    public static final Map<UUID, Long> cooldowns = new HashMap<>();

    private final Player player;
    private final UUID uuid;

    @Contract(pure = true)
    public ListManager(@NotNull Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    /**
     * Toggles player's phantom status
     *
     * @return  true if player enabled phantoms, false if player disabled phantoms
     */
    public boolean togglePhantoms() {
        if (list.contains(uuid)) {
            list.remove(uuid);
        } else {
            list.add(uuid);
        }
        cooldowns.put(uuid, System.currentTimeMillis() + 30000);
        return list.contains(uuid);
    }

    /**
     * @return  milliseconds until player can toggle phantoms again
     */
    public long getCooldownLeft() {
        return cooldowns.get(uuid) - System.currentTimeMillis();
    }

    /**
     * @return  whether the player is on cooldown or not
     */
    public boolean onCooldown() {
        if (!cooldowns.containsKey(uuid)) return false;
        if (getCooldownLeft() <= 0 || player.hasPermission("personalphantoms.nophantoms.bypass")) {
            cooldowns.remove(uuid);
            return false;
        }
        return true;
    }
}
