package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Objects;

@RequiredArgsConstructor
public class CreatureSpawnListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler
    private void onCreatureSpawn(CreatureSpawnEvent event) { // Listener methods should be private
        Entity entity = event.getEntity();
        if (entity == null) return;

        final EntityStackCache cache = plugin.getEntityStackCache();

        // Recommended to limit the radius to 16 blocks (avoid crashes and lag)
        final int radius = plugin.getConfig().getInt("stacking-radius.creatures");

        // Internally, the getNearbyEntities method uses some loopings;
        // Nearby looping is based in cuboid (max/min) and not in sphere (radius).
        EntityStack[] nearbyStacks = entity.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e.getType() == entity.getType())
                .map(cache::get)
                .filter(Objects::nonNull) // simplify the null validation
                .filter(stack -> stack.getAmount() > 0)
                .toArray(EntityStack[]::new);

        // Better readability
        if (nearbyStacks.length == 0) {
            cache.add(entity, new EntityStack(plugin, entity, 1));
        } else {
            final EntityStack stack = nearbyStacks[0];

            // Use if instead of assert.
            // Asserts aren't recommended for production code, feel free to use it for tests.
            // See https://docs.oracle.com/javase/8/docs/technotes/guides/language/assert.html for more information.
            // "An assertion is a statement in the Java programming language
            // that enables you to test your assumptions about your program."
            // Assertion throws an AssertionError if the condition is false.
            if (stack == null) return;

            stack.addAmount(1);
            event.setCancelled(true);
        }
    }
}