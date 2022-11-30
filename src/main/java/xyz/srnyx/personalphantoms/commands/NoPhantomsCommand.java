package xyz.srnyx.personalphantoms.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.personalphantoms.PersonalPhantoms;
import xyz.srnyx.personalphantoms.managers.NoPhantomsManager;

import java.util.concurrent.TimeUnit;


public class NoPhantomsCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("pp.nophantoms")) {
            PersonalPhantoms.sendMessage(sender, "&cYou do not have permission to use this command!");
            return true;
        }

        // nophantoms <player>
        if (args.length == 1 && sender.hasPermission("pp.nophantoms.others")) {
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                PersonalPhantoms.sendMessage(sender, "&4" + args[0] + "&c is an invalid player!");
                return true;
            }

            new NoPhantomsManager(target).togglePhantoms(sender);
            return true;
        }

        // Check if player
        if (!(sender instanceof Player)) {
            PersonalPhantoms.sendMessage(sender, "&cYou must be a player to use this command!");
            return true;
        }
        final NoPhantomsManager manager = new NoPhantomsManager((Player) sender);

        // Check if on cooldown
        if (manager.onCooldown()) {
            PersonalPhantoms.sendMessage(sender,
                    "&cYou must wait &4" + TimeUnit.MILLISECONDS.toSeconds(manager.getCooldownLeft()) + "&c seconds before using this command again!");
            return true;
        }

        manager.togglePhantoms(null);
        return true;
    }
}
