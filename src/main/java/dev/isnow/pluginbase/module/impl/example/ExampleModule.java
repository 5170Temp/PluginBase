package dev.isnow.pluginbase.module.impl.example;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.module.Module;
import dev.isnow.pluginbase.module.impl.differentmodule.DifferentModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.module.impl.example.teleport.TeleportManager;
import dev.isnow.pluginbase.util.BaseLogger;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class ExampleModule extends Module<ExampleModuleConfig> {
    private final DifferentModule differentModule;
    private final TeleportManager teleportManager;

    public ExampleModule() {
        teleportManager = null;
        differentModule = null;
    }

    public ExampleModule(final PluginBase plugin, final DifferentModule differentModule) {
        super(plugin);

        this.teleportManager = new TeleportManager();
        this.differentModule = differentModule;
    }

    @Override
    public Collection<Class<?>> getDatabaseEntities() {
        return Collections.singleton(HomeData.class);
    }

    @Override
    public void onEnable() {
        registerListeners("event");
        registerCommands("command");

        BaseLogger.debug("Different module string: " + differentModule.getTestString());
    }

    @Override
    public void onDisable() {
        unRegisterListeners();
        unRegisterCommands();
    }
}
