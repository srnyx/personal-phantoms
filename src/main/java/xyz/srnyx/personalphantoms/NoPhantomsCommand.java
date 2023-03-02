package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingCooldown;
import xyz.srnyx.annoyingapi.AnnoyingMessage;
import xyz.srnyx.annoyingapi.AnnoyingUtility;
import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;

import java.util.Collection;


public class NoPhantomsCommand implements AnnoyingCommand {
    @NotNull private final PersonalPhantoms plugin;

    @Contract(pure = true)
    public NoPhantomsCommand(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public PersonalPhantoms getPlugin() {
        return plugin;
    }

    @Override
    public String getPermission() {
        return "pp.nophantoms";
    }

    public void onCommand(@NotNull AnnoyingSender sender) {
        final CommandSender cmdSender = sender.cmdSender;
        final String[] args = sender.args;

        // nophantoms <player>
        if (args.length == 1 && cmdSender.hasPermission("pp.nophantoms.others")) {
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new AnnoyingMessage(plugin, "error.invalid-argument")
                        .replace("%argument%", args[0])
                        .send(sender);
                return;
            }

            togglePhantoms(new AnnoyingSender(plugin, target), sender);
            return;
        }

        // Check if player
        if (!(cmdSender instanceof Player)) {
            new AnnoyingMessage(plugin, plugin.options.playerOnly).send(sender);
            return;
        }

        // Check if on cooldown
        final AnnoyingCooldown cooldown = new AnnoyingCooldown(plugin, sender.getPlayer().getUniqueId(), CooldownType.NO_PHANTOMS);
        if (!cmdSender.hasPermission("pp.nophantoms.bypass") && cooldown.isOnCooldown()) {
            new AnnoyingMessage(plugin, "nophantoms.cooldown")
                    .replace("%cooldown%", cooldown.getRemaining(), AnnoyingMessage.DefaultReplaceType.TIME)
                    .send(sender);
            return;
        }

        togglePhantoms(sender, null);
    }

    @Override @Nullable
    public Collection<String> onTabComplete(@NotNull AnnoyingSender sender) {
        return AnnoyingUtility.getOnlinePlayerNames();
    }

    /**
     * Toggles the player's phantom status
     *
     * @param   plugin  the plugin instance
     * @param   sender  the player to toggle phantom status for
     * @param   toggler the {@link AnnoyingSender} toggling the player's phantom status
     */
    private void togglePhantoms(@NotNull AnnoyingSender sender, @Nullable AnnoyingSender toggler) {
        final Player player = sender.getPlayer();

        // Cooldown
        new AnnoyingCooldown(plugin, player.getUniqueId(), CooldownType.NO_PHANTOMS).start();

        // Get status
        final String status;
        if (player.getScoreboardTags().contains("pp_no-phantoms")) {
            player.removeScoreboardTag("pp_no-phantoms");
            status = "enabled";
        } else {
            player.addScoreboardTag("pp_no-phantoms");
            status = "disabled";
        }

        // Messages
        if (toggler != null) {
            new AnnoyingMessage(plugin, "nophantoms.other")
                    .replace("%status%", status)
                    .replace("%toggler%", toggler.cmdSender.getName())
                    .send(sender);
            new AnnoyingMessage(plugin, "nophantoms.toggler")
                    .replace("%target%", player.getName())
                    .replace("%status%", status)
                    .send(toggler);
            return;
        }
        new AnnoyingMessage(plugin, "nophantoms.self")
                .replace("%status%", status)
                .send(sender);
    }
}
