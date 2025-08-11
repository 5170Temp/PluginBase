package dev.isnow.pluginbase.database;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.ConfigManager;
import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;

@Getter
public final class DatabaseManager {

    private final PluginBase plugin;
    private Database database; // Now initialized later

    public DatabaseManager(PluginBase plugin) {
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
        PlayerData.findByOfflinePlayerAsync(player, (session, foundPlayerData) -> {
            if (foundPlayerData == null) {
                BaseLogger.debug("Player data not found for " + player.getName() + ", creating new entry.");

                final PlayerData playerData = new PlayerData(player);
                playerData.save(session);
            } else {
                BaseLogger.debug("Player data found for " + player.getName() + ", loading from cache.");

                foundPlayerData.setName(player.getName());
                foundPlayerData.save(session);
            }
        });
    }

}
