package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

@RequiredArgsConstructor
public class CreatureSpawnListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        final EntityStackCache cache = plugin.getEntityStackCache();

        final int radius = plugin.getConfig().getInt("stacking-radius.creatures");

        EntityStack[] nearbyStacks = entity.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e.getType() == entity.getType())
                .map(cache::get)
                .filter(stack -> stack != null && stack.getAmount() > 0)
                .toArray(EntityStack[]::new);

        final boolean isThereNearbyStack = nearbyStacks.length > 0;

        if (!isThereNearbyStack) {
            cache.add(entity, new EntityStack(plugin, entity, 1));
            return;
        }

        final EntityStack stack = nearbyStacks[0];

        assert stack != null;

        stack.addAmount(1);
        event.setCancelled(true);
    }
}