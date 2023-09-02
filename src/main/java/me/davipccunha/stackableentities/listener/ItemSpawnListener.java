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

// RequiredArgsConstructor is a Lombok annotation that generates a constructor for all final fields.
// AllArgsConstructor is a Lombok annotation that generates a constructor for all fields.
@RequiredArgsConstructor
public class ItemSpawnListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler
    private void onItemSpawn(ItemSpawnEvent event) { // Listener methods should be private
        Item entity = event.getEntity();
        if (entity == null) return;

        final int initialAmount = entity.getItemStack().getAmount();

        final EntityStackCache cache = plugin.getEntityStackCache();

        // Recommended to limit the radius to 16 blocks (avoid crashes and lag)
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

        // Use if instead of assert.
        // Asserts aren't recommended for production code, feel free to use it for tests.
        // See https://docs.oracle.com/javase/8/docs/technotes/guides/language/assert.html for more information.
        // "An assertion is a statement in the Java programming language
        // that enables you to test your assumptions about your program."
        // Assertion throws an AssertionError if the condition is false.
        if (stack == null) return;

        stack.addAmount(initialAmount);
        event.setCancelled(true);
    }
}
