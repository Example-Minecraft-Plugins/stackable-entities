package me.davipccunha.stackableentities.listener;

import lombok.AllArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class PlayerPickupItemListener implements Listener {
    private final StackableEntitiesPlugin plugin;

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem() == null) return;

        final Item item = event.getItem();
        final int droppedItemMaxStackSize = item.getItemStack().getMaxStackSize();
        final Player player = event.getPlayer();
        final Inventory inventory = player.getInventory();
        final EntityStackCache cache = plugin.getEntityStackCache();

        if (!cache.has(item)) return;

        final EntityStack stack = cache.get(item);

        if (stack.getAmount() <= 0) {
            cache.remove(item);
            return;
        }

        final List<Integer> emptySlots = getEmptySlots(inventory);

        ItemStack itemStackCopy = item.getItemStack().clone();

        int maxAmount = Arrays.stream(inventory.getContents())
                .filter(itemStack -> itemStack != null
                        && itemStack.isSimilar(itemStackCopy)
                        && itemStack.getAmount() < droppedItemMaxStackSize)
                .reduce(0, (acc, itemStack) -> acc + (droppedItemMaxStackSize - itemStack.getAmount()), Integer::sum);

        maxAmount += emptySlots.size() * droppedItemMaxStackSize;

        int amountToAdd = (int) Math.min(maxAmount, stack.getAmount());

        itemStackCopy.setAmount(amountToAdd); // Not working as expected when maxStackSize is less than 64

        inventory.addItem(itemStackCopy);

        stack.removeAmount(cache, amountToAdd);

        event.setCancelled(true);
    }

    private List<Integer> getEmptySlots(Inventory inventory) {
        List<Integer> emptySlots = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) emptySlots.add(i);
        }

        return emptySlots;
    }
}
