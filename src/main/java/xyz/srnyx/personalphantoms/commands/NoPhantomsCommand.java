package xyz.srnyx.personalphantoms.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.personalphantoms.managers.NoPhantomsManager;

import java.util.concurrent.TimeUnit;


public class NoPhantomsCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("personalphantoms.nophantoms")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        // nophantoms <player>
        if (args.length == 1 && sender.hasPermission("personalphantoms.nophantoms.others")) {
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4" + args[0] + "&c is an invalid player!"));
                return true;
            }

            new NoPhantomsManager(target).togglePhantoms(sender);
            return true;
        }

        // Check if player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }
        final NoPhantomsManager noPhantomsManager = new NoPhantomsManager(player);

        // Check if on cooldown
        if (noPhantomsManager.onCooldown()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cYou must wait &4" + TimeUnit.MILLISECONDS.toSeconds(noPhantomsManager.getCooldownLeft()) + "&c seconds before using this command again!"));
            return true;
        }

        noPhantomsManager.togglePhantoms(null);
        return true;
    }
}
