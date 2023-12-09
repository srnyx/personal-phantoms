package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingCooldown;
import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.data.EntityData;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NoPhantomsCmd extends AnnoyingCommand {
    @NotNull private static final AnnoyingCooldown.CooldownType COOLDOWN_TYPE = () -> 30000;

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
        final String[] args = sender.args;

        // reload
        if (sender.argEquals(0, "reload")) {
            if (!sender.checkPermission("pp.reload")) return;
            plugin.reloadPlugin();
            new AnnoyingMessage(plugin, "reload").send(sender);
            return;
        }

        if (args.length == 1) {
            // get
            if (sender.argEquals(0, "get")) {
                if (!sender.checkPlayer()) return;
                new AnnoyingMessage(plugin, "get.self")
                        .replace("%status%", new EntityData(plugin, sender.getPlayer()).has(PersonalPhantoms.KEY), DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // <toggle|enable|disable>
            if (sender.argEquals(0, "toggle", "enable", "disable")) {
                if (!sender.checkPlayer() || !sender.checkPermission("pp.nophantoms")) return;

                // Check if on cooldown
                final AnnoyingCooldown cooldown = new AnnoyingCooldown(plugin, sender.getPlayer().getUniqueId(), COOLDOWN_TYPE);
                if (!cmdSender.hasPermission("pp.nophantoms.bypass") && cooldown.isOnCooldown()) {
                    new AnnoyingMessage(plugin, "nophantoms.cooldown")
                            .replace("%cooldown%", cooldown.getRemaining(), DefaultReplaceType.TIME)
                            .send(sender);
                    return;
                }

                // Edit
                new AnnoyingMessage(plugin, "nophantoms.self")
                        .replace("%status%", editKey(sender.getPlayer(), sender.argEquals(0, "toggle") ? null : sender.argEquals(0, "enable")), DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            sender.invalidArguments();
            return;
        }

        // Check args and permission
        if (args.length != 2) {
            sender.invalidArguments();
            return;
        }
        if (!sender.checkPermission("pp.nophantoms.others")) return;

        // Get target
        final Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.invalidArgument(args[1]);
            return;
        }
        final String targetName = target.getName();

        // get [<player>]
        if (sender.argEquals(0, "get")) {
            new AnnoyingMessage(plugin, "get.other")
                    .replace("%target%", targetName)
                    .replace("%status%", new EntityData(plugin, target).has(PersonalPhantoms.KEY), DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }

        // <toggle|enable|disable> [<player>]
        if (sender.argEquals(0, "toggle", "enable", "disable")) {
            final Player player = target.getPlayer();
            final boolean newStatus = editKey(player, sender.argEquals(0, "toggle") ? null : sender.argEquals(0, "enable"));
            new AnnoyingMessage(plugin, "nophantoms.toggler")
                    .replace("%target%", player.getName())
                    .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                    .send(sender);
            new AnnoyingMessage(plugin, "nophantoms.other")
                    .replace("%status%", newStatus, DefaultReplaceType.BOOLEAN)
                    .replace("%toggler%", targetName)
                    .send(target);
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

    private boolean editKey(@NotNull Player player, @Nullable Boolean newStatus) {
        final EntityData data = new EntityData(plugin, player);
        if (newStatus == null) newStatus = !data.has(PersonalPhantoms.KEY); // toggle
        if (!newStatus) {
            data.remove(PersonalPhantoms.KEY);
        } else {
            data.set(PersonalPhantoms.KEY, true);
        }
        return newStatus;
    }
}
