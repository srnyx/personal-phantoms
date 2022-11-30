package xyz.srnyx.personalphantoms.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import org.jetbrains.annotations.NotNull;


public class MobListener implements Listener {
    /**
     * Called when a creature is spawned into a world.
     * If a Creature Spawn event is cancelled, the creature will not spawn.
     */
    @EventHandler
    public void onMobSpawn(@NotNull CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();
        // Check if Phantom and spawn is natural
        if (entity.getType() == EntityType.PHANTOM && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            // Check if any nearby players have phantoms disabled TODO: Find a better way to do this
            if (entity.getNearbyEntities(10, 35, 10).stream()
                    .anyMatch(nearby -> nearby instanceof Player && nearby.getScoreboardTags().contains("pp_no-phantoms"))) event.setCancelled(true);
        }
    }

    /**
     * Called when a creature targets or untargets another entity
     */
    @EventHandler
    public void onEntityTarget(@NotNull EntityTargetEvent event) {
        final Entity target = event.getTarget();
        if (event.getEntity().getType() == EntityType.PHANTOM && target instanceof Player && target.getScoreboardTags().contains("pp_no-phantoms")) event.setCancelled(true);
    }

    /**
     * Called when an entity is damaged by an entity
     */
    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        // Player attacking Phantom
        if (damager instanceof Player && entity.getType() == EntityType.PHANTOM && damager.getScoreboardTags().contains("pp_no-phantoms")) event.setCancelled(true);
        // Phantom attacking Player
        if (damager.getType() == EntityType.PHANTOM && entity instanceof Player && entity.getScoreboardTags().contains("pp_no-phantoms")) event.setCancelled(true);
    }
}
