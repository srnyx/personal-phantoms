package xyz.srnyx.personalphantoms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
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
     * Called when a creature is spawned into a world.
     * If a Creature Spawn event is cancelled, the creature will not spawn.
     */
    @EventHandler
    public void onCreatureSpawn(@NotNull CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();
        // Check if Phantom
        if (entity.getType() == EntityType.PHANTOM
                // Check if natural spawn
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                // Check if plugin enabled in world
                && (plugin.worldsBlacklist == null || plugin.worldsBlacklist.contains(entity.getWorld().getName()) != plugin.treatBlacklistAsWhitelist)
                // Check if any nearby players have phantoms disabled TODO: Find a better way to do this
                && entity.getNearbyEntities(10, 35, 10).stream().anyMatch(nearby -> nearby instanceof Player && new EntityData(plugin, nearby).has(PersonalPhantoms.KEY))) event.setCancelled(true);
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
     *
     * @deprecated  Used for old data conversion
     */
    @EventHandler @Deprecated
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (!player.getScoreboardTags().contains(PersonalPhantoms.KEY)) return;
        new EntityData(plugin, player).set(PersonalPhantoms.KEY, true);
        player.removeScoreboardTag(PersonalPhantoms.KEY);
    }
}
