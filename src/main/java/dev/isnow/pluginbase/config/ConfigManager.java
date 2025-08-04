package dev.isnow.pluginbase.config;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.impl.GeneralConfig;
import dev.isnow.pluginbase.config.impl.database.DatabaseConfig;
import lombok.Getter;

import java.io.File;

@Getter
public class ConfigManager {
    private final PluginBase plugin;

    private GeneralConfig generalConfig;
    private DatabaseConfig databaseConfig;

    public ConfigManager(final PluginBase plugin) {
        this.plugin = plugin;

        final File modulesPath = new File(plugin.getDataFolder() + File.separator + "modules");

        if(!modulesPath.exists()) {
            modulesPath.mkdir();
        }

        loadAll();
    }

    private void loadAll() {
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