package dev.isnow.pluginbase.module;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.BaseConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ModuleConfig extends BaseConfig {
    public ModuleConfig(String moduleName, String configFileName) {
        super(configFileName, buildPath(moduleName, configFileName));
    }

    private static Path buildPath(String moduleName, String configFileName) {
        return Paths.get(
                PluginBase.getInstance().getDataFolder().getAbsolutePath(),
                "modules",
                moduleName,
                configFileName + ".yml"
        );
    }
}
