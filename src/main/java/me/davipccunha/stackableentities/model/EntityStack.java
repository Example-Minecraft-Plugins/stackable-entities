package me.davipccunha.stackableentities.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.util.EntityName;
import me.davipccunha.stackableentities.util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

@Getter
@Setter
// TODO: Use NBT to save the amount of items in the stack so that it can be retrieved on server restarts
// TODO: Change amount from long to int
public class EntityStack {

    // TODO: Use the entity id instead of an item entity (memory usage/references)
    private final Entity baseEntity;
    private long amount;

    public EntityStack(StackableEntitiesPlugin plugin, Entity baseEntity, long initialAmount) {
        baseEntity.setCustomNameVisible(true);

        long amount = Math.max(1, initialAmount);

        this.baseEntity = baseEntity;
        this.setAmount(plugin.getEntityStackCache(), amount);
    }

    public void setAmount(EntityStackCache cache, long amount) {
        if (amount <= 0) {
            if (cache.has(this.baseEntity)) cache.remove(this.baseEntity);
            this.baseEntity.remove();

            return;
        }

        this.amount = amount;
        this.updateName();
    }

    public void addAmount(long amount) {
        if (this.amount <= 0) return;

        this.amount += amount;
        this.updateName();
    }

    public void removeAmount(EntityStackCache cache, long amount) {
        if (amount <= 0 || this.amount <= amount) {
            if (cache.has(this.baseEntity)) cache.remove(this.baseEntity);
            this.baseEntity.remove();

            return;
        }

        this.amount -= amount;
        this.updateName();
    }

    public String getDisplayName() {
        String name = this.baseEntity instanceof Item
                ? ItemName.valueOf(((Item) this.baseEntity).getItemStack()).toString()
                : EntityName.valueOf(this.baseEntity.getType()).toString();

        return ChatColor.WHITE + name + "§a - §f" + this.amount;
    }

    // Don't use toString() for this, use a method instead
    // See https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#toString-- for more information
    // Returns a string representation of the object.
    // "In general, the toString method returns a string that "textually represents" this object.
    // The result should be a concise but informative representation that is easy for a person to read."
    // Avoid using toString() for this, it's not a good practice.
    /* @Override
    public String toString() {
        String name = this.baseEntity instanceof Item
                ?
                ItemName.valueOf(((Item) this.baseEntity).getItemStack()).toString()
                : EntityName.valueOf(this.baseEntity.getType()).toString();

        return ChatColor.WHITE + name + "§a - §f" + this.amount;
    }*/

    private void updateName() {
        this.baseEntity.setCustomName(this.getDisplayName());
    }
}
