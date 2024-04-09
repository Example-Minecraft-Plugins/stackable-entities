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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;

import java.util.List;

@RequiredArgsConstructor
public class ItemMergeListener implements Listener {
    private final StackableEntitiesPlugin plugin;

    @EventHandler(priority = EventPriority.LOW)
    private void onItemMerge(ItemMergeEvent event) {
        // Prevents item merge which bugs the plugin since an EntityStack is represented by an Item with a set amount of 1
        event.setCancelled(true);

        // When drops get close to each other, they are merged into a single stack
        /* OBSERVATION: Since there is no way to know an Entity got close to another one, for drops
         * we can use the ItemMergeEvent to merge them. Therefore, the radius used to activate the stacks merging is not the one
         * defined in the config, but the one defined in the server properties */

        final Item entity = event.getEntity();
        final Item target = event.getTarget();
        if (entity == null || target == null) return;

        final EntityStackCache cache = plugin.getEntityStackCache();

        final FileConfiguration config = plugin.getConfig();

        final int configRadius = config.getInt("stacking-radius.drops");
        final long configMaxStackSize = config.getLong("max-stack-size.drops");

        final int radius = configRadius >= 1 ? Math.min(configRadius, 16) : 1;
        final long maxEntityStackSize = Math.max(configMaxStackSize, 64);

        final List<Entity> nearbyEntities = entity.getNearbyEntities(radius, radius, radius);

        final EntityStack[] nearbyStacks = nearbyEntities.stream()
                .filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .filter(i -> ((Item) i).getItemStack().isSimilar(entity.getItemStack()))
                .map(Entity::getEntityId)
                .map(cache::get)
                .filter(stack_ -> stack_ != null && (stack_.getAmount() > 0 && stack_.getAmount() < maxEntityStackSize))
                .toArray(EntityStack[]::new);

        final boolean isThereNearbyStack = nearbyStacks.length > 0;

        if (!isThereNearbyStack) return;

        final EntityStack stack = cache.get(entity.getEntityId());
        if (stack == null) return;

        final long stackAmount = stack.getAmount();

        final EntityStack targetStack = nearbyStacks[0];
        if (targetStack == null) return;

        final Item targetEntity = (Item) nearbyEntities.stream()
                .filter(e -> e.getEntityId() == targetStack.getBaseEntityID())
                .findFirst().orElse(null);

        if (targetEntity == null) return;

        final long missingAmount = maxEntityStackSize - targetStack.getAmount();
        final long amountToAdd = Math.min(missingAmount, stackAmount);

        targetStack.addAmount(targetEntity, amountToAdd);
        cache.remove(entity.getEntityId());
        entity.remove();

        // TODO: Implement overflow handling

//        final long totalAmount = stackAmount + amountToAdd;
//        final long overflowAmount = totalAmount <= maxEntityStackSize ? 0 : totalAmount - maxEntityStackSize;
//
//        if (overflowAmount > 0) {
//            final Item newBaseEntity = entity.getWorld().dropItem(target.getLocation(), entity.getItemStack());
//            EntityStack newStack = new EntityStack(cache, newBaseEntity, overflowAmount);
//
//            cache.add(newBaseEntity.getEntityId(), newStack);
//        }
    }
}
