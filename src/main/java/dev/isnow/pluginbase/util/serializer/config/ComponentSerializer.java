package dev.isnow.pluginbase.util.serializer.config;

import de.exlll.configlib.Serializer;
import dev.isnow.pluginbase.util.ComponentUtil;
import net.kyori.adventure.text.Component;

public class ComponentSerializer implements Serializer<Component, String> {
    @Override
    public String serialize(final Component component) {
        return ComponentUtil.serialize(component);
    }

    @Override
    public Component deserialize(final String s) {
        return ComponentUtil.deserialize(s);
    }
}
