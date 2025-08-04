package dev.isnow.pluginbase.util.serializer.config;

import de.exlll.configlib.Serializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class SoundSerializer implements Serializer<Sound, String> {
    @Override
    public String serialize(final Sound sound) {
        return Registry.SOUNDS.getKey(sound).getKey();
    }

    @Override
    public Sound deserialize(final String sound) {
        return Registry.SOUNDS.get(NamespacedKey.fromString(sound));
    }
}
