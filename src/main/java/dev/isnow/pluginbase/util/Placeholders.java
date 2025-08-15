package dev.isnow.pluginbase.util;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Placeholders {

    private final Map<String, Object> placeholders = new LinkedHashMap<>();

    public Placeholders add(final String key, final Object value) {
        if (key.startsWith("%")) {
            placeholders.put(key, value);
        } else {
            placeholders.put("%" + key + "%", value);
        }

        return this;
    }

    public Map<String, Object> getAll() {
        return placeholders;
    }
}