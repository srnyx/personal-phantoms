package xyz.srnyx.personalphantoms.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.cooldown.AnnoyingCooldown;
import xyz.srnyx.annoyingapi.data.StringData;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;
import xyz.srnyx.personalphantoms.PersonalPhantoms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NoPhantomsCmd extends xyz.srnyx.personalphantoms.commands.generated.NophantomsCmdGen {
    @NotNull private static final String COOLDOWN_KEY = "NoPhantomsCmd";

    public NoPhantomsCmd(@NotNull PersonalPhantoms plugin) {
        super(plugin);
    }

    public void onCommand(@NotNull AnnoyingSender sender) {
        // reload
        if (sender.argEquals(0, "reload")) {
            if (!sender.checkPermission("pp.reload")) return;
            plugin.reloadPlugin();
            plugin.getMessages().get().reload.newMessage().send(sender);
            return;
        }

        // This command handles both "/nophantoms" AND "/phantoms" (for convenience)
        // But, commands such as "/phantoms disable" and "/nophantoms disable" mean different things to the player
        final boolean inverse = sender.label != null && sender.label.equalsIgnoreCase("nophantoms");

        // No args: toggle
        if (sender.args.length == 0) {
            cooldownEditSelf(sender, null);
            return;
        }

        if (sender.args.length == 1) {
            // get
            if (sender.argEquals(0, "get")) {
                if (sender.checkPlayer()) plugin.getMessages().get().get.self.newMessage()
                        .replace("%status%", !plugin.hasPhantomsEnabled(sender.getPlayer()), DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // <toggle|enable|disable>
            if (sender.argEquals(0, "toggle", "enable", "disable")) {
                cooldownEditSelf(sender, sender.argEquals(0, "toggle") ? null : sender.argEquals(0, "enable") ^ inverse);
                return;
            }

            sender.invalidArguments();
            return;
        }

        // Check args and permission
        if (sender.args.length != 2) {
            sender.invalidArguments();
            return;
        }
        if (!sender.checkPermission("pp.nophantoms.others")) return;

        // Get targets
        final List<OfflinePlayer> targets = sender
                .getSelector(1, OfflinePlayer.class)
                .orElseFlatSingle(BukkitUtility::getOfflinePlayer);
        if (targets == null) return;

        // get [<player>]
        if (sender.argEquals(0, "get")) {
            for (final OfflinePlayer target : targets) plugin.getMessages().get().get.other.newMessage()
                    .replace("%target%", target.getName())
                    .replace("%status%", !plugin.hasPhantomsEnabled(target), DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }

        // <toggle|enable|disable> [<player>]
        if (sender.argEquals(0, "toggle", "enable", "disable")) {
            for (final OfflinePlayer target : targets) {
                final boolean newStatus = editKey(target, sender.argEquals(0, "toggle") ? null : sender.argEquals(0, "enable") ^ inverse);
                plugin.getMessages().get().nophantoms.toggler.newMessage()
                        .replace("%target%", target.getName())
                        .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                        .send(sender);
                final Player targetOnline = target.getPlayer();
                if (targetOnline != null) plugin.getMessages().get().nophantoms.other.newMessage()
                        .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                        .replace("%toggler%", sender.cmdSender.getName())
                        .send(targetOnline);
            }
            return;
        }

        sender.invalidArguments();
    }

    @Override @Nullable
    public Collection<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final CommandSender cmdSender = sender.cmdSender;

        // <reload|get|toggle|enable|disable>
        if (sender.args.length == 1) {
            final List<String> list = new ArrayList<>();
            if (cmdSender.hasPermission("pp.reload")) list.add("reload");
            if (cmdSender.hasPermission("pp.nophantoms")) {
                list.add("get");
                list.add("toggle");
                list.add("enable");
                list.add("disable");
            }
            return list;
        }

        // <get|toggle|enable|disable> [<player>]
        if (!sender.argEquals(0, "reload") && cmdSender.hasPermission("pp.nophantoms.others")) return sender.withSelectorKeys(BukkitUtility.getOnlinePlayerNames(), OfflinePlayer.class);

        return null;
    }

    private void cooldownEditSelf(@NotNull AnnoyingSender sender, @Nullable Boolean enablePhantoms) {
        // Get player
        if (!sender.checkPlayer()) return;
        final Player player = sender.getPlayer();

        // Check if on cooldown
        if (!player.hasPermission("pp.nophantoms.bypass")) {
            final AnnoyingCooldown cooldown = plugin.cooldownManager.getCooldownElseNew(COOLDOWN_KEY, player.getUniqueId().toString());
            final long duration = BukkitUtility.getPermissionValue(player, "pp.nophantoms.cooldown.")
                    .map(value -> value * 1000) // Convert to milliseconds
                    .orElse(plugin.config.command_cooldown.toMillis());
            if (cooldown.isOnCooldownStart(duration)) return;
        }

        // Edit
        final boolean newStatus = editKey(player, enablePhantoms);
        plugin.getMessages().get().nophantoms.self.newMessage()
                .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                .send(sender);
    }

    /**
     * Edit the key status for a player
     *
     * @param   offline         the player to edit the key status for
     *
     * @param   enablePhantoms  whether to enable or disable phantoms for the player. {@code null} = toggle
     *
     * @return                  the new status of the key (true if phantoms enabled, false if disabled)
     */
    private boolean editKey(@NotNull OfflinePlayer offline, @Nullable Boolean enablePhantoms) {
        // Update key status
        final StringData data = new StringData(plugin, offline);
        if (enablePhantoms == null) enablePhantoms = !plugin.hasPhantomsEnabled(data); // toggle
        data.set(PersonalPhantoms.KEY, !enablePhantoms);

        // Update statistic
        final Player online = offline.getPlayer();
        if (online != null && plugin.isWhitelistedWorld(online.getWorld())) {
            if (enablePhantoms) {
                // Set statistic to 1 hour (so phantoms will attack)
                online.setStatistic(Statistic.TIME_SINCE_REST, 72000);
            } else {
                // Reset statistic (so phantoms won't attack)
                PersonalPhantoms.resetStatistic(online);
            }
        }

        return enablePhantoms;
    }
}
