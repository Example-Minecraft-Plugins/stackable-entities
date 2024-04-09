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
    private void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem() == null) return;

        final Item item = event.getItem();
        final int droppedItemMaxStackSize = item.getItemStack().getMaxStackSize();
        final Player player = event.getPlayer();
        final Inventory inventory = player.getInventory();
        final EntityStackCache cache = plugin.getEntityStackCache();

        final int entityID = item.getEntityId();

        if (!cache.has(entityID)) return;

        final EntityStack stack = cache.get(entityID);

        if (stack.getAmount() <= 0) {
            cache.remove(entityID);
            return;
        }

        final List<Integer> emptySlots = this.getEmptySlots(inventory);

        ItemStack itemStackCopy = item.getItemStack().clone();

        int maxAmount = Arrays.stream(inventory.getContents())
                .filter(itemStack -> itemStack != null
                        && itemStack.isSimilar(itemStackCopy)
                        && itemStack.getAmount() < droppedItemMaxStackSize)
                .mapToInt(itemStack -> droppedItemMaxStackSize - itemStack.getAmount())
                .sum();

        maxAmount += emptySlots.size() * droppedItemMaxStackSize;

        long amountToAdd = Math.min(maxAmount, stack.getAmount());

        ItemStack[] stacksToAdd = this.getStacksToAdd(itemStackCopy, amountToAdd);

        inventory.addItem(stacksToAdd);

        stack.removeAmount(cache, item, amountToAdd);

        event.setCancelled(true);
    }

    private List<Integer> getEmptySlots(Inventory inventory) {
        List<Integer> emptySlots = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) emptySlots.add(i);
        }

        return emptySlots;
    }

    private ItemStack[] getStacksToAdd(ItemStack itemStack, long amount) {
        final List<ItemStack> stacksToAdd = new ArrayList<>();
        final int maxStackSize = itemStack.getMaxStackSize();

        final long fullStacks = Math.floorDiv(amount, maxStackSize);

        ItemStack fullStack = itemStack.clone();
        fullStack.setAmount(maxStackSize);

        for (int i = 0; i < fullStacks; i++)
            stacksToAdd.add(fullStack);

        final long remainingAmount = amount % maxStackSize;

        if (remainingAmount > 0) {
            ItemStack itemStackCopy = itemStack.clone();
            itemStackCopy.setAmount((int) remainingAmount);
            stacksToAdd.add(itemStackCopy);
        }

        return stacksToAdd.toArray(new ItemStack[0]);
    }
}
