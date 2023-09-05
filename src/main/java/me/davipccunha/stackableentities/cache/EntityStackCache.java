package me.davipccunha.stackableentities.cache;

import me.davipccunha.stackableentities.model.EntityStack;

import java.util.HashMap;
import java.util.Map;

public class EntityStackCache {
    private final Map<Integer, EntityStack> entityStacks = new HashMap<>();

    public void add(int entityID, EntityStack entityStack) {
        entityStacks.putIfAbsent(entityID, entityStack);
    }

    public void remove(int entityID) {
        if (!entityStacks.containsKey(entityID)) return;

        entityStacks.remove(entityID);
    }

    public EntityStack get(int entityID) {
        return entityStacks.get(entityID);
    }

    public boolean has(int entityID) {
        return entityStacks.containsKey(entityID);
    }

    public void clear() {
        entityStacks.clear();
    }
}
