package dev.isnow.pluginbase.config.impl.inventory;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Configuration
public class StatefulItemConfig implements MenuItemConfig {
    Map<String, ItemConfig> states = new HashMap<>();

    private transient String currentState;

    public StatefulItemConfig() {}

    public void addState(final String name, final ItemConfig config) {
        states.put(name, config);
        if (currentState == null) {
            currentState = name;
        }
    }

    public void setCurrentState(final String state) {
        if (states.containsKey(state)) {
            currentState = state;
        }
    }

    public ItemConfig getCurrentItemConfig() {
        return states.get(currentState);
    }

    @Override
    public Material getMaterial() {
        return getCurrentItemConfig().getMaterial();
    }

    @Override
    public int getSlot() {
        return getCurrentItemConfig().getSlot();
    }

    @Override
    public String getName() {
        return getCurrentItemConfig().getName();
    }


    @Override
    public List<String> getLore() {
        return getCurrentItemConfig().getLore();
    }

    @Override
    public int getAmount() {
        return getCurrentItemConfig().getAmount();
    }
}
