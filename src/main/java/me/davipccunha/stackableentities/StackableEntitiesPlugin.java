package me.davipccunha.stackableentities;

import lombok.Getter;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.listener.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StackableEntitiesPlugin extends JavaPlugin {

    private final EntityStackCache entityStackCache = new EntityStackCache();

    @Override
    public void onEnable() {
        this.init();
        getLogger().info("Stackable Entities plugin loaded!");
    }

    public void onDisable() {
        entityStackCache.clear();
        getLogger().info("Stackable Entities plugin unloaded!");
    }

    private void init() {
        saveDefaultConfig();
        registerListeners(
                new ChunkUnloadListener(this),
                new CreatureSpawnListener(this),
                new EntityDeathListener(this),
                new ItemSpawnListener(this),
                new PlayerPickupItemListener(this),
                new ItemMergeListener()
        );
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) pluginManager.registerEvents(listener, this);
    }
}
