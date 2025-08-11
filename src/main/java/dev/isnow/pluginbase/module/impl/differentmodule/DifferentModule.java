package dev.isnow.pluginbase.module.impl.differentmodule;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.module.Module;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Getter;

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
