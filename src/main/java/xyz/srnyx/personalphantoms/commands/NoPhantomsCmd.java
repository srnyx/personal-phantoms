package xyz.srnyx.personalphantoms.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.cooldown.AnnoyingCooldown;
import xyz.srnyx.annoyingapi.data.StringData;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;

import xyz.srnyx.personalphantoms.PersonalPhantoms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NoPhantomsCmd extends AnnoyingCommand {
    @NotNull private final PersonalPhantoms plugin;

    public NoPhantomsCmd(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public PersonalPhantoms getAnnoyingPlugin() {
        return plugin;
    }

    @Override
    public String getPermission() {
        return "pp.nophantoms";
    }

    public void onCommand(@NotNull AnnoyingSender sender) {
        final CommandSender cmdSender = sender.cmdSender;
        final int length = sender.args.length;

        // reload
        if (sender.argEquals(0, "reload")) {
            if (!sender.checkPermission("pp.reload")) return;
            plugin.reloadPlugin();
            new AnnoyingMessage(plugin, "reload").send(sender);
            return;
        }

        // This command handles both "/nophantoms" AND "/phantoms" (for convenience)
        // But, commands such as "/phantoms disable" and "/nophantoms disable" mean different things to the player
        final boolean inverse = sender.label != null && sender.label.equalsIgnoreCase("nophantoms");

        if (length == 1) {
            // get
            if (sender.argEquals(0, "get")) {
                if (sender.checkPlayer()) new AnnoyingMessage(plugin, "get.self")
                        .replace("%status%", !plugin.hasPhantomsEnabled(sender.getPlayer()), DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // <toggle|enable|disable>
            if (sender.argEquals(0, "toggle", "enable", "disable")) {
                if (!sender.checkPlayer()) return;
                final Player player = sender.getPlayer();

                // Check if on cooldown
                if (!cmdSender.hasPermission("pp.nophantoms.bypass")) {
                    final AnnoyingCooldown cooldown = plugin.cooldownManager.getCooldownElseNew("NoPhantomsCmd", player.getUniqueId().toString());
                    final long duration = BukkitUtility.getPermissionValue(player, "pp.nophantoms.cooldown.")
                            .map(value -> value * 1000L) // Convert to milliseconds
                            .orElse(plugin.config.commandCooldown);
                    if (cooldown.isOnCooldownStart(duration)) {
                        new AnnoyingMessage(plugin, "nophantoms.cooldown")
                                .replace("%cooldown%", cooldown.getRemaining(), DefaultReplaceType.TIME)
                                .send(sender);
                        return;
                    }
                }

                // Edit
                final boolean newStatus = editKey(player, sender.argEquals(0, "toggle") ? null : sender.argEquals(0, "enable") ^ inverse);
                new AnnoyingMessage(plugin, "nophantoms.self")
                        .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            sender.invalidArguments();
            return;
        }

        // Check args and permission
        if (length != 2) {
            sender.invalidArguments();
            return;
        }
        if (!sender.checkPermission("pp.nophantoms.others")) return;

        // Get target
        final OfflinePlayer target = sender.getArgumentOptionalFlat(1, BukkitUtility::getOfflinePlayer).orElse(null);
        if (target == null) return;
        final String targetName = target.getName();

        // get [<player>]
        if (sender.argEquals(0, "get")) {
            new AnnoyingMessage(plugin, "get.other")
                    .replace("%target%", targetName)
                    .replace("%status%", !plugin.hasPhantomsEnabled(target), DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }

        // <toggle|enable|disable> [<player>]
        if (sender.argEquals(0, "toggle", "enable", "disable")) {
            final boolean newStatus = editKey(target, sender.argEquals(0, "toggle") ? null : sender.argEquals(0, "enable") ^ inverse);
            new AnnoyingMessage(plugin, "nophantoms.toggler")
                    .replace("%target%", targetName)
                    .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                    .send(sender);
            final Player targetOnline = target.getPlayer();
            if (targetOnline != null) new AnnoyingMessage(plugin, "nophantoms.other")
                    .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                    .replace("%toggler%", cmdSender.getName())
                    .send(targetOnline);
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
        if (!sender.argEquals(0, "reload") && cmdSender.hasPermission("pp.nophantoms.others")) return BukkitUtility.getOnlinePlayerNames();

        return null;
    }

    /**
     * Edit the key status for a player
     *
     * @param   offline         the player to edit the key status for
     *
     * @param   enablePhantoms  whether to enable or disable phantoms for the player
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
