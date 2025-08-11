package dev.isnow.pluginbase.config;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.impl.GeneralConfig;
import dev.isnow.pluginbase.config.impl.database.DatabaseConfig;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Getter;

import java.io.File;
import java.util.logging.Level;

@Getter
public class ConfigManager {
    private final PluginBase plugin;

    private GeneralConfig generalConfig;
    private DatabaseConfig databaseConfig;

    public ConfigManager(final PluginBase plugin) {
        this.plugin = plugin;
    }

    private void loadAll() {
        final File modulesPath = new File(plugin.getDataFolder() + File.separator + "modules");

        if(!modulesPath.exists()) {
            modulesPath.mkdir();
        }

        generalConfig = (GeneralConfig) new GeneralConfig().load();
        databaseConfig = (DatabaseConfig) new DatabaseConfig().load();
    }

    public void saveConfigs() {
        generalConfig.save();
        databaseConfig.save();
    }

    public void reloadConfigs() {
        loadAll();
    }

}