package xyz.srnyx.personalphantoms.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.personalphantoms.PersonalPhantoms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class NoPhantomsManager {
    public static final Map<UUID, Long> cooldowns = new HashMap<>();

    private final Player player;
    @Contract(pure = true)
    public NoPhantomsManager(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Toggles the {@link #player}'s phantom status
     *
     * @param   toggler the {@link CommandSender} toggling the {@link #player}'s phantom status
     */
    public void togglePhantoms(@Nullable CommandSender toggler) {
        String toggle;
        if (player.getScoreboardTags().contains("pp_no-phantoms")) {
            player.removeScoreboardTag("pp_no-phantoms");
            toggle = "enabled";
        } else {
            player.addScoreboardTag("pp_no-phantoms");
            toggle = "disabled";
        }

        // Messages
        String playerMessage = "&dPhantoms &5" + toggle;
        if (toggler != null) {
            playerMessage += " &dby &5" + toggler.getName();
            PersonalPhantoms.sendMessage(toggler, "&dPhantoms &5" + toggle + " &dfor &5" + player.getName());
        }
        PersonalPhantoms.sendMessage(player, playerMessage);

        // Cooldown
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 30000);
    }

    /**
     * @return  milliseconds until {@link #player} can toggle phantoms again
     */
    public long getCooldownLeft() {
        return cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
    }

    /**
     * @return  whether the {@link #player} is on cooldown or not
     */
    public boolean onCooldown() {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;
        if (getCooldownLeft() <= 0 || player.hasPermission("pp.nophantoms.bypass")) {
            cooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
}
