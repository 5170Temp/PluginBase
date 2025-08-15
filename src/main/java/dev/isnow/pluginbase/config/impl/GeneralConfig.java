package dev.isnow.pluginbase.config.impl;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import dev.isnow.pluginbase.config.MasterConfig;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Configuration
public class GeneralConfig extends MasterConfig {
    @Comment({BaseLogger.bigPrefix, " ", "Base plugin configuration, ask 5170 for more info.", "", "List of enabled modules"})
    private ArrayList<String> enabledModules = new ArrayList<>() {{
        add("Core");
        add("Commands");
        add("Logging");
        add("Legacy");
    }};

    @Comment({"", "Debug mode (more information in console)"})
    private boolean debugMode = false;

    @Comment({"", "Messages Prefix"})
    private String prefix = "<b><gradient:#FF0000:#FFAC00>PluginBase</gradient></b> &8•";

    @Comment({"", "Increase this if your server is lagging with higher player count"})
    int threadAmount = 20;

    @Comment({"", "Not enough permissions command message"})
    String notEnoughPermissions = "[P] &cYou do not have permission to use this command!";


    public GeneralConfig() {
        super("config");
    }
}