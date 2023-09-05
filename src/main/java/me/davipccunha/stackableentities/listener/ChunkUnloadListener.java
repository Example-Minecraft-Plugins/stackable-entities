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

@RequiredArgsConstructor
public class ChunkUnloadListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChunkUnload(ChunkUnloadEvent event) {
        EntityStackCache cache = plugin.getEntityStackCache();

        Chunk chunk = event.getChunk();

        for (Entity entity : chunk.getEntities()) {
            if (cache.has(entity.getEntityId())) cache.remove(entity.getEntityId());
        }
    }
}
