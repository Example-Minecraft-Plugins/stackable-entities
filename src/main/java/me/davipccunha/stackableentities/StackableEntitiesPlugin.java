package me.davipccunha.stackableentities;

import lombok.Getter;
import me.davipccunha.stackableentities.cache.EntityStackCache;
import me.davipccunha.stackableentities.listener.*;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StackableEntitiesPlugin extends JavaPlugin {

    private final EntityStackCache entityStackCache = new EntityStackCache();

    @Override
    public void onEnable() {
        this.init();
        getLogger().info("Stackable Entities plugin carregado!");
    }

    public void onDisable() {
        getLogger().info("Stackable Entities plugin descarregado!");
    }

    private void init() {
        registerListeners();
        registerCommands();

        saveDefaultConfig();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ItemSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new ChunkUnloadListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(this), this);
    }

    private void registerCommands() {
//        this.getCommand("terrain").setExecutor(new TerrainCommand(this));
    }
}
