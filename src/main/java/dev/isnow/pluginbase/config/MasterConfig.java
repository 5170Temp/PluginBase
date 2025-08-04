package dev.isnow.pluginbase.config;

import dev.isnow.pluginbase.PluginBase;

import java.io.File;
import java.nio.file.Paths;

public abstract class MasterConfig extends BaseConfig {
    public MasterConfig(final String name) {
        super(name, Paths.get(PluginBase.getInstance().getDataFolder() + File.separator + name + ".yml"));
    }
}
