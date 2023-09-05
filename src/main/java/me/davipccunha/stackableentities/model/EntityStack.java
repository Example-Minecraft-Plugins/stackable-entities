package me.davipccunha.stackableentities.model;

import lombok.Getter;
import lombok.Setter;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.util.EntityName;
import me.davipccunha.stackableentities.util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

@Getter
@Setter
// TODO: Use NBT to save the amount of items in the stack so that it can be retrieved on plugin restarts
// Entities can't have custom NBT tags. Another possible solution?
public class EntityStack {
    private final int baseEntityID;
    private int amount;

    public EntityStack(EntityStackCache cache, Entity baseEntity, int initialAmount) {
        baseEntity.setCustomNameVisible(true);

        int amount = Math.max(1, initialAmount);

        this.baseEntityID = baseEntity.getEntityId();
        this.setAmount(cache, baseEntity, amount);
    }

    public void setAmount(EntityStackCache cache, Entity entity, int amount) {
        if (amount <= 0) {
            if (cache.has(this.baseEntityID)) cache.remove(this.baseEntityID);
            entity.remove();

            return;
        }

        this.amount = amount;
        this.updateName(entity);

    }

    public void addAmount(Entity entity, int amount) {
        if (this.amount <= 0) return;

        this.amount += amount;
        this.updateName(entity);
    }

    public void removeAmount(EntityStackCache cache, Entity entity, int amount) {
        if (amount <= 0 || this.amount <= amount) {
            if (cache.has(entity.getEntityId())) cache.remove(entity.getEntityId());
            entity.remove();

            return;
        }

        this.amount -= amount;
        this.updateName(entity);
    }

    public String getDisplayName(Entity entity) {
        String name = entity instanceof Item ?
                ItemName.valueOf(((Item) entity).getItemStack()).toString() :
                EntityName.valueOf(entity.getType()).toString();

        return ChatColor.WHITE + name + "§a - §f" + this.amount;
    }

    private void updateName(Entity entity) {
        entity.setCustomName(this.getDisplayName(entity));
    }
}
