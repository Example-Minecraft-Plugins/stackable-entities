package me.davipccunha.stackableentities;

import lombok.Getter;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.listener.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StackableEntitiesPlugin extends JavaPlugin {

    // Avoid loading classes before loading the plugin

    private final EntityStackCache entityStackCache = new EntityStackCache();

    @Override
    public void onEnable() {
        this.init();

        getLogger().info("Stackable Entities plugin loaded!"); // Fixed typo (don't use pt-en)
    }

    @Override
    public void onDisable() {
        entityStackCache.clear(); // Clean up the cache

        getLogger().info("Stackable Entities plugin unloaded!"); // Fixed typo (don't use pt-en)
    }

    // Better readability and code organization
    private void init() {
        // Load the default config if it doesn't exist,
        // Load the config first so that it can be used in the listeners (if needed)
        saveDefaultConfig();

        registerListeners();
        // registerCommands(); - removed
    }

    private void registerListeners() {
        registerListener(
                new PlayerPickupItemListener(this),
                new EntityDeathListener(this),
                new ChunkUnloadListener(this),
                new CreatureSpawnListener(this),
                new ItemSpawnListener(this));
    }

    // Better readability
    private void registerListener(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) pluginManager.registerEvents(listener, this);
    }
}
