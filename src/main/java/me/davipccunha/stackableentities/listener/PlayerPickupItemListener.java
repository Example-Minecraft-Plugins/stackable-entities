package me.davipccunha.stackableentities.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.stackableentities.StackableEntitiesPlugin;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.model.EntityStack;
import org.bukkit.Material;
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

// RequiredArgsConstructor is a Lombok annotation that generates a constructor for all final fields.
// AllArgsConstructor is a Lombok annotation that generates a constructor for all fields.
@RequiredArgsConstructor
public class PlayerPickupItemListener implements Listener {
    private final StackableEntitiesPlugin plugin;

    @EventHandler
    private void onPlayerPickupItem(PlayerPickupItemEvent event) { // Listener methods should be private
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

        // simplified using mapToInt and sum instead of a Stream#reduce
        int maxAmount = Arrays.stream(inventory.getContents())
                .filter(itemStack -> itemStack != null
                        && itemStack.isSimilar(itemStackCopy)
                        && itemStack.getAmount() < droppedItemMaxStackSize)
                .mapToInt(itemStack -> droppedItemMaxStackSize - itemStack.getAmount())
                .sum();

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
            ItemStack item = inventory.getItem(i);

            // safe check if any plugin set an item using "new ItemStack(Material.AIR)"
            if (item == null || item.getType() == Material.AIR) emptySlots.add(i);
        }

        return emptySlots;
    }
}
