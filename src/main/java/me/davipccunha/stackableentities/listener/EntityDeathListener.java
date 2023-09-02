package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

// RequiredArgsConstructor is a Lombok annotation that generates a constructor for all final fields.
// AllArgsConstructor is a Lombok annotation that generates a constructor for all fields.
@RequiredArgsConstructor
public class EntityDeathListener implements Listener {

    // do not use public fields use private final fields with a constructor instead
    private final StackableEntitiesPlugin plugin;

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) { // Listener methods should be private
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
