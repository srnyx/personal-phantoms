package xyz.srnyx.personalphantoms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.data.EntityData;


public class MobListener extends AnnoyingListener {
    @NotNull private final PersonalPhantoms plugin;

    public MobListener(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public PersonalPhantoms getAnnoyingPlugin() {
        return plugin;
    }

    /**
     * Called when a creature targets or untargets another entity
     */
    @EventHandler
    public void onEntityTarget(@NotNull EntityTargetEvent event) {
        final Entity target = event.getTarget();
        if (event.getEntity().getType() == EntityType.PHANTOM && target instanceof Player && new EntityData(plugin, target).has(PersonalPhantoms.KEY)) event.setCancelled(true);
    }

    /**
     * Called when an entity is damaged by an entity
     */
    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        // Player attacking Phantom
        if (damager instanceof Player && entity.getType() == EntityType.PHANTOM && new EntityData(plugin, damager).has(PersonalPhantoms.KEY)) event.setCancelled(true);
        // Phantom attacking Player
        if (damager.getType() == EntityType.PHANTOM && entity instanceof Player && new EntityData(plugin, entity).has(PersonalPhantoms.KEY)) event.setCancelled(true);
    }

    /**
     * Called when a player joins a server
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final EntityData data = new EntityData(plugin, player);
        //TODO Old data conversion
        data.convertOldData(PersonalPhantoms.KEY);
        // Reset statistic
        if (plugin.shouldResetStatistic(player)) PersonalPhantoms.resetStatistic(player);
    }
}
