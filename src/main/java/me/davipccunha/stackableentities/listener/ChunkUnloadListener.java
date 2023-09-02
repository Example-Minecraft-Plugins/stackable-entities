package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

// RequiredArgsConstructor is a Lombok annotation that generates a constructor for all final fields.
// AllArgsConstructor is a Lombok annotation that generates a constructor for all fields.
@RequiredArgsConstructor
public class ChunkUnloadListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    // Use EventPriority.MONITOR to monitor without updating the event
    // Priority order: Lowest -> Low -> Normal -> High -> Highest -> Monitor
    // See https://helpch.at/docs/1.8/index.html?org/bukkit/event/EventPriority.html for more information.
    @EventHandler(priority = EventPriority.MONITOR)
    private void onChunkUnload(ChunkUnloadEvent event) { // Listener methods should be private
        EntityStackCache cache = plugin.getEntityStackCache();
        Chunk chunk = event.getChunk();

        // Generally single-letter variable names are bad for readability and understanding.
        // I'd recommend using a more descriptive name.
        for (Entity entity : chunk.getEntities()) {
            if (cache.has(entity)) cache.remove(entity);
        }
    }
}
