package me.davipccunha.stackableentities.listener;

import lombok.AllArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@AllArgsConstructor
public class EntityDeathListener implements Listener {
    StackableEntitiesPlugin plugin;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;

        // Can be removed => Big stacks will take too long to get removed
        if (event.getEntity().getKiller() == null) return;

        int entityID = entity.getEntityId();

        final EntityStackCache cache = plugin.getEntityStackCache();

        if (!cache.has(entityID)) return;

        final EntityStack stack = cache.get(entityID);

        if (stack.getAmount() <= 1) {
            cache.remove(entityID);
            return;
        }

        cache.remove(entityID);

        Entity newBaseEntity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());

        if (newBaseEntity instanceof Ageable) ((Ageable) newBaseEntity).setAdult();
        if (newBaseEntity instanceof Zombie) ((Zombie) newBaseEntity).setBaby(false);

        // Size must be set to 1 otherwise the stack amount will increase infinitely
        if (newBaseEntity instanceof Slime) ((Slime) newBaseEntity).setSize(1);

        EntityStack newStack = new EntityStack(cache, newBaseEntity, stack.getAmount() - 1);

        // New stack might have been created on world#spawnEntity()
        if (!cache.has(newBaseEntity.getEntityId())) {
            cache.add(newBaseEntity.getEntityId(), newStack);
        } else {
            cache.get(newBaseEntity.getEntityId()).setAmount(cache, newBaseEntity, newStack.getAmount());
        }
    }
}
