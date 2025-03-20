package xyz.srnyx.personalphantoms.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.data.EntityData;

import xyz.srnyx.personalphantoms.PersonalPhantoms;


public class MobListener extends AnnoyingListener {
    @NotNull private final PersonalPhantoms plugin;

    public MobListener(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public PersonalPhantoms getAnnoyingPlugin() {
        return plugin;
    }

    @EventHandler
    public void onEntityTargetLivingEntity(@NotNull EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PHANTOM) return;
        final LivingEntity target = event.getTarget();
        if (target instanceof Player && plugin.isWhitelistedWorld(target.getWorld()) && new EntityData(plugin, target).has(PersonalPhantoms.KEY)) event.setCancelled(true);
    }

    /**
     * Called when an entity is damaged by an entity
     */
    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        if (!plugin.isWhitelistedWorld(damager.getWorld())) return;
        final Entity target = event.getEntity();
        // Player attacking Phantom
        if (damager instanceof Player && target.getType() == EntityType.PHANTOM && new EntityData(plugin, damager).has(PersonalPhantoms.KEY)) {
            event.setCancelled(true);
            return;
        }
        // Phantom attacking Player
        if (damager.getType() == EntityType.PHANTOM && target instanceof Player && new EntityData(plugin, target).has(PersonalPhantoms.KEY)) event.setCancelled(true);
    }

    /**
     * Called when a player joins a server
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final EntityData data = new EntityData(plugin, player);
        data.convertOldData(PersonalPhantoms.KEY); //TODO remove in future
        // Reset statistic
        if (plugin.isWhitelistedWorld(player.getWorld()) && new EntityData(plugin, player).has(PersonalPhantoms.KEY)) PersonalPhantoms.resetStatistic(player);
    }
}
