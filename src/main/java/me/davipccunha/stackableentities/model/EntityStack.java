package me.davipccunha.stackableentities.model;

import lombok.Getter;
import lombok.Setter;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.utils.entity.EntityName;
import me.davipccunha.utils.item.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

@Getter
@Setter
public class EntityStack {
    private final int baseEntityID;
    private long amount;

    public EntityStack(EntityStackCache cache, Entity baseEntity, long initialAmount) {
        baseEntity.setCustomNameVisible(true);

        long amount = Math.max(1, initialAmount);

        this.baseEntityID = baseEntity.getEntityId();
        this.setAmount(cache, baseEntity, amount);
    }

    public void setAmount(EntityStackCache cache, Entity entity, long amount) {
        if (amount <= 0) {
            if (cache.has(this.baseEntityID)) cache.remove(this.baseEntityID);
            entity.remove();

            return;
        }

        this.amount = amount;
        this.updateName(entity);

    }

    public void addAmount(Entity entity, long amount) {
        if (this.amount <= 0) return;

        this.amount += amount;
        this.updateName(entity);
    }

    public void removeAmount(EntityStackCache cache, Entity entity, long amount) {
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

    @Override
    public String toString() {
        return String.format("%s - %d", this.baseEntityID, this.amount);
    }
}
