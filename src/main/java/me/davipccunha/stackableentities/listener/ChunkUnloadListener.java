package me.davipccunha.stackableentities.listener;

import lombok.AllArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

@AllArgsConstructor
public class ChunkUnloadListener implements Listener {

    private final StackableEntitiesPlugin plugin;

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        EntityStackCache cache = plugin.getEntityStackCache();

        Chunk chunk = event.getChunk();

        for (Entity e : chunk.getEntities()) {
            if (cache.has(e)) cache.remove(e);
        }
    }
}
