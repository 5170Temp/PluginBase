package dev.isnow.pluginbase.database;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.ConfigManager;
import dev.isnow.pluginbase.data.impl.PlayerData;
import dev.isnow.pluginbase.database.impl.Database;
import dev.isnow.pluginbase.event.impl.PreloadingFinishedEvent;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Getter
public final class DatabaseManager {

    private final PluginBase plugin;
    private Database database;

    public DatabaseManager(final PluginBase plugin) {
        this.plugin = plugin;
    }

    public void initialize(Collection<Class<?>> moduleEntities) {
        if (this.database != null && this.database.isConnected()) {
            BaseLogger.warn("Database is already initialized. Skipping.");
            return;
        }

        ConfigManager configManager = plugin.getConfigManager();
        this.database = new Database(plugin, configManager.getGeneralConfig(), configManager.getDatabaseConfig(), moduleEntities);
    }

    public void shutdown() {
        if (database != null) {
            database.shutdown();
        }
    }

    public void preloadPlayer(final Player player) {
        PlayerData.findByOfflinePlayerAsync(player).whenCompleteAsync(((playerData, throwable) -> {
            if (playerData == null) {
                BaseLogger.debug("Player data not found for " + player.getName() + ", creating new entry.");

                playerData = new PlayerData(player);
                playerData.save();
            } else {
                BaseLogger.debug("Player data found for " + player.getName() + ", loading from cache.");

                playerData.setName(player.getName());
                playerData.save();
            }

            Bukkit.getPluginManager().callEvent(new PreloadingFinishedEvent(player, playerData));
        }));
    }

}
