package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

@RequiredArgsConstructor
public class CreatureSpawnListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler(priority = EventPriority.HIGH)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();
        if (entity == null) return;

        final EntityStackCache cache = plugin.getEntityStackCache();
        final FileConfiguration config = plugin.getConfig();

        final int radius = Math.min(config.getInt("stacking-radius.creatures"), 16);

        // Possibly make the max stack size configurable according to SpawnReason -> Spawner upgradable to increase max stack size
        final long maxEntityStackSize = Math.min(config.getLong("max-stack-size.creatures"), Long.MAX_VALUE);

        final List<Entity> nearbyEntities = entity.getNearbyEntities(radius, radius, radius);

        final EntityStack[] nearbyIncompleteStacks = nearbyEntities.stream()
                .filter(e -> e.getType() == entity.getType())
                .map(Entity::getEntityId)
                .map(cache::get)
                .filter(stack -> stack != null && (stack.getAmount() > 0 && stack.getAmount() < maxEntityStackSize))
                .toArray(EntityStack[]::new);

        final boolean isThereNearbyStack = nearbyIncompleteStacks.length > 0;

        if (!isThereNearbyStack) {
            cache.add(entity.getEntityId(), new EntityStack(cache, entity, 1));
            return;
        }

        final EntityStack stack = nearbyIncompleteStacks[0];

        if (stack == null) return;

        // This is a solution to find an entity by its ID since at this point the spawned entity is surely in range of the base entity
        // This allows us to define an EntityStack with an int (entityID) instead of a whole Entity object
        final Entity baseEntity = nearbyEntities.stream()
                .filter(e -> e.getEntityId() == stack.getBaseEntityID())
                .findFirst().orElse(null);

        if (baseEntity == null) return;

        stack.addAmount(baseEntity, 1);
        event.setCancelled(true);
    }
}