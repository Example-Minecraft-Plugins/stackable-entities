package me.davipccunha.stackableentities.cache;

import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

// No reason to use a required args constructor here
public class EntityStackCache {

    // Change this to a Map<Integer, EntityStack> if you want to use the entity id instead of the entity object
    private final Map<Integer, EntityStack> entityStacks = new HashMap<>();

    public void add(Entity entity, EntityStack entityStack) {
        entityStacks.putIfAbsent(entity.getEntityId(), entityStack);
    }

    public void remove(Entity entity) {
        if (!entityStacks.containsKey(entity.getEntityId())) return;

        entityStacks.remove(entity.getEntityId());
    }

    public EntityStack get(Entity entity) {
        return entityStacks.get(entity.getEntityId());
    }

    /**
     * Returns the EntityStack associated with the given entity id (spigot/nms).
     * @param entityId The nms entity id.
     * @return The EntityStack associated with the given entity id.
     */
    public EntityStack get(int entityId) {
        return entityStacks.get(entityId);
    }

    public boolean has(Entity entity) {
        return entityStacks.containsKey(entity.getEntityId());
    }

    public void clear() {
        entityStacks.clear();
    }
}
