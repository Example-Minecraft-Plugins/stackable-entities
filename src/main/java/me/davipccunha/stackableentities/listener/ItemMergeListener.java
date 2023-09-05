package me.davipccunha.stackableentities.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;

public class ItemMergeListener implements Listener {
    @EventHandler
    private void onItemMerge(ItemMergeEvent event) {
        // Prevents item merge which bugs the plugin since an EntityStack is represented by an Item with a set amount of 1
        event.setCancelled(true);
    }
}
