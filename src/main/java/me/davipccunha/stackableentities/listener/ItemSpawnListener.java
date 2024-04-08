package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.util.List;

@RequiredArgsConstructor
public class ItemSpawnListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler
    private void onItemSpawn(ItemSpawnEvent event) {
        final Item entity = event.getEntity();
        if (entity == null) return;

        final int initialAmount = entity.getItemStack().getAmount();

        final EntityStackCache cache = plugin.getEntityStackCache();
        final FileConfiguration config = plugin.getConfig();

        final int configRadius = config.getInt("stacking-radius.drops");
        final int configMaxStackSize = config.getInt("max-stack-size.drops");

        final int radius = configRadius >= 1 ? Math.min(configRadius, 16) : 1;
        final int maxEntityStackSize = Math.max(configMaxStackSize, 16);

        final List<Entity> nearbyEntities = entity.getNearbyEntities(radius, radius, radius);

        final EntityStack[] nearbyStacks = nearbyEntities.stream()
                .filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .filter(i -> ((Item) i).getItemStack().isSimilar(entity.getItemStack()))
                .map(Entity::getEntityId)
                .map(cache::get)
                .filter(stack -> stack != null && (stack.getAmount() > 0 && stack.getAmount() < maxEntityStackSize))
                .toArray(EntityStack[]::new);

        final boolean isThereNearbyStack = nearbyStacks.length > 0;

        final int entityID = entity.getEntityId();

        // Stack limit is not respected when the stack amount + the item dropped amount is greater than the stack limit
        if (!isThereNearbyStack) {
            entity.getItemStack().setAmount(1);
            cache.add(entityID, new EntityStack(cache, entity, initialAmount));
            return;
        }

        final EntityStack stack = nearbyStacks[0];
        if (stack == null) return;

        // This is a solution to find an entity by its ID since at this point the spawned entity is surely in range of the base entity
        // This allows us to define an EntityStack with an int entityID instead of a whole Entity object
        final Entity baseEntity = nearbyEntities.stream()
                .filter(e -> e.getEntityId() == stack.getBaseEntityID())
                .filter(e -> cache.get(e.getEntityId()).getAmount() < maxEntityStackSize)
                .findFirst().orElse(null);

        if (baseEntity == null) return;

        stack.addAmount(baseEntity, initialAmount);
        event.setCancelled(true);
    }
}
