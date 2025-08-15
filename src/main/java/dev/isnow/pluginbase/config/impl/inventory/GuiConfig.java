package dev.isnow.pluginbase.config.impl.inventory;

import de.exlll.configlib.Configuration;
import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.BaseConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

@Getter @Setter
@Configuration
public abstract class GuiConfig extends BaseConfig {
    int size;
    String title = "";
    HashMap<String, MenuItemConfig> items = new HashMap<>();

    public GuiConfig(final String name, final String moduleName) {
        super(name, Paths.get(PluginBase.getInstance().getDataFolder() + File.separator + "modules" + File.separator + moduleName + File.separator + "inventories" + File.separator + name + ".yml"));
    }

    public void addItem(final String item, final MenuItemConfig itemConfig) {
        items.put(item, itemConfig);
    }

}