package me.davipccunha.stackableentities.cache;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EntityStackCache {
    private final Map<Entity, EntityStack> entityStacks = new HashMap<>();

    public void add(Entity entity, EntityStack entityStack) {
        entityStacks.putIfAbsent(entity, entityStack);
    }

    public void remove(Entity entity) {
        if (!entityStacks.containsKey(entity)) return;

        entityStacks.remove(entity);
    }

    public EntityStack get(Entity entity) {
        return entityStacks.get(entity);
    }

    public boolean has(Entity entity) {
        return entityStacks.containsKey(entity);
    }
}
