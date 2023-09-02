package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

@RequiredArgsConstructor
public class ItemSpawnListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        if (entity == null) return;

        final int initialAmount = entity.getItemStack().getAmount();

        final EntityStackCache cache = plugin.getEntityStackCache();

        final int radius = plugin.getConfig().getInt("stacking-radius.drops");

        EntityStack[] nearbyStacks = event.getEntity().getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .filter(i -> ((Item) i).getItemStack().isSimilar(entity.getItemStack()))
                .map(cache::get)
                .filter(stack -> stack != null && stack.getAmount() > 0)
                .toArray(EntityStack[]::new);

        final boolean isThereNearbyStack = nearbyStacks.length > 0;

        if (!isThereNearbyStack) {
            cache.add(entity, new EntityStack(plugin, entity, initialAmount));
            return;
        }

        final EntityStack stack = nearbyStacks[0];

        assert stack != null;

        stack.addAmount(initialAmount);
        event.setCancelled(true);
    }
}
