package xyz.srnyx.personalphantoms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.personalphantoms.managers.ListManager;

import java.util.concurrent.TimeUnit;


public class NoPhantomsCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission("personalphantoms.nophantoms")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length == 1 && sender.hasPermission("personalphantoms.nophantoms.others")) {
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4" + args[0] + "&c is an invalid player!"));
                return true;
            }

            if (new ListManager(target).togglePhantoms()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dPhantoms &5disabled &dfor &5" + target.getName()));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dPhantoms &5disabled &dby &5" + sender.getName()));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dPhantoms &5enabled &dfor &5" + target.getName()));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dPhantoms &5enabled &dby &5" + sender.getName()));
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }
        final ListManager listManager = new ListManager(player);

        // Check if on cooldown
        if (listManager.onCooldown()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must wait &4"
                    + TimeUnit.MILLISECONDS.toSeconds(listManager.getCooldownLeft())
                    + "&c seconds before using this command again!"));
            return true;
        }

        if (listManager.togglePhantoms()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dPhantoms &5disabled"));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dPhantoms &5enabled"));
        }
        return true;
    }
}
