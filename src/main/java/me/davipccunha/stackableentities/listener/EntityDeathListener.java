package me.davipccunha.stackableentities.listener;

import lombok.AllArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@AllArgsConstructor
public class EntityDeathListener implements Listener {
    StackableEntitiesPlugin plugin;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        final EntityStackCache cache = plugin.getEntityStackCache();

        if (!cache.has(entity)) return;

        final EntityStack stack = cache.get(entity);

        if (stack.getAmount() <= 1) {
            cache.remove(entity);
            return;
        }

        cache.remove(entity);

        Entity newBaseEntity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
        EntityStack newStack = new EntityStack(plugin, newBaseEntity, stack.getAmount() - 1);

        // New stack might have been created on world#spawnEntity()
        if (!cache.has(newBaseEntity)) {
            cache.add(newBaseEntity, newStack);
        } else {
            cache.get(newBaseEntity).setAmount(newStack.getAmount());
        }
    }
}
