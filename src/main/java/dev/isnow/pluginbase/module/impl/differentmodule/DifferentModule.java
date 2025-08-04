package dev.isnow.pluginbase.module.impl.differentmodule;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.module.Module;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.module.impl.example.teleport.TeleportManager;
import dev.isnow.pluginbase.util.BaseLogger;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class DifferentModule extends Module {
    private final String testString = "Hello World from DifferentModule!";

    public DifferentModule() {

    }

    public DifferentModule(final PluginBase plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        BaseLogger.debug("Test module loaded.");
    }
}
