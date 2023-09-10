package me.davipccunha.stackableentities.listener;

import lombok.AllArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

@AllArgsConstructor
public class ItemDespawnListener implements Listener {
    private final StackableEntitiesPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)

    private void onItemDespawn(ItemDespawnEvent event) {
        if (event.getEntity() == null) return;

        final EntityStackCache cache = plugin.getEntityStackCache();
        final int entityID = event.getEntity().getEntityId();
        if (cache.has(entityID)) cache.remove(entityID);
    }
}
