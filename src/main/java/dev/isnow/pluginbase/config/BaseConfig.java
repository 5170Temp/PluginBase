package dev.isnow.pluginbase.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import dev.isnow.pluginbase.util.Range;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import dev.isnow.pluginbase.util.serializer.config.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;

import java.math.BigDecimal;
import java.nio.file.Path;

@Getter
public abstract class BaseConfig {
    private static final YamlConfigurationProperties PROPERTIES = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .addSerializer(BaseLocation.class, new BaseLocationSerializer())
            .addSerializer(Component.class, new ComponentSerializer())
            .addSerializer(Range.class, new RangeSerializer())
            .addSerializer(Sound.class, new SoundSerializer())
            //.addSerializer(BigDecimal.class, new BigDecimalSerializer())
            //.addSerializer(ItemStack.class, new BetterItemStackSerializer())
            .build();

    private final String name;
    private final Path path;

    public BaseConfig(final String name, final Path path) {
        this.name = name;
        this.path = path;
    }

    public BaseConfig load() {
        if(!path.toFile().exists()) {
            BaseLogger.debug("Config file " + path + " does not exist, creating...");
            save();
        }

        return YamlConfigurations.load(path, getClass(), PROPERTIES);
    }

    @SuppressWarnings("unchecked")
    public <T> void save() {
        YamlConfigurations.save(path, (Class<T>) this.getClass(), (T) this, PROPERTIES);
    }

    public void delete() {
        final boolean deleted = path.toFile().delete();
        BaseLogger.debug("Deleted config file " + path.getFileName() + " (" + deleted + ")");
    }

}