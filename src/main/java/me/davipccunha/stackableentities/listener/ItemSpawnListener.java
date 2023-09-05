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
        Item entity = event.getEntity();
        if (entity == null) return;

        final int initialAmount = entity.getItemStack().getAmount();

        final EntityStackCache cache = plugin.getEntityStackCache();
        final FileConfiguration config = plugin.getConfig();

        final int radius = Math.min(plugin.getConfig().getInt("stacking-radius.drops"), 16);
        // Max stack size not working for drops because Minecraft automatically stacks drops.
        // Possible solution -> Make it so the ItemStack of a drop is not stackable or set its amount to 64
        final int maxEntityStackSize = Math.min(config.getInt("max-stack-size.drops"), Integer.MAX_VALUE);

        List<Entity> nearbyEntities = entity.getNearbyEntities(radius, radius, radius);

        EntityStack[] nearbyStacks = nearbyEntities.stream()
                .filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .filter(i -> ((Item) i).getItemStack().isSimilar(entity.getItemStack()))
                .map(Entity::getEntityId)
                .map(cache::get)
                .filter(stack -> stack != null && (stack.getAmount() > 0 && stack.getAmount() < maxEntityStackSize))
                .toArray(EntityStack[]::new);

        final boolean isThereNearbyStack = nearbyStacks.length > 0;

        final int entityID = entity.getEntityId();

        if (!isThereNearbyStack) {
            entity.getItemStack().setAmount(1);
            cache.add(entityID, new EntityStack(cache, entity, initialAmount));
            return;
        }

        final EntityStack stack = nearbyStacks[0];

        if (stack == null) return;

        // This is the solution to find an entity by its ID since the spawned entity is surely in range of the base entity
        // This allows us to define an EntityStack with an entityID instead of a whole Entity object
        Entity baseEntity = nearbyEntities.stream()
                .filter(e -> e.getEntityId() == stack.getBaseEntityID())
                .filter(e -> cache.get(e.getEntityId()).getAmount() < maxEntityStackSize)
                .findFirst().orElse(null);

        if (baseEntity == null) return;

        stack.addAmount(baseEntity, initialAmount);
        event.setCancelled(true);
    }
}
