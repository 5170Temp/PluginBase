package dev.isnow.pluginbase.util.logger;

import dev.isnow.pluginbase.PluginBase;
import lombok.experimental.UtilityClass;

import java.util.logging.Level;
import java.util.logging.Logger;


@UtilityClass
public class BaseLogger {
    public static final String bigPrefix = "PLACEHOLDER";
    private final String prefix = "[%LEVEL%] » ";

    private static final Logger LOGGER = PluginBase.getInstance().getLogger();

    public static void error(final String message, final Throwable throwable) {
        LOGGER.log(Level.SEVERE, prefix.replace("%LEVEL%", "\033[31mERROR\033[0m") + message, throwable);
    }

    public static void error(final String message) {
        LOGGER.severe(prefix.replace("%LEVEL%", "\033[31mERROR\033[0m") + message);
    }

    public void warn(final String message) {
        LOGGER.warning(prefix.replace("%LEVEL%", "\033[33mWARN\033[0m") + message);
    }

    public void warn(final String message, final Throwable throwable) {
        LOGGER.log(Level.WARNING, prefix.replace("%LEVEL%", "\033[33mWARN\033[0m") + message, throwable);
    }

    public void info(final String message) {
        LOGGER.info(prefix.replace("%LEVEL%", "\u001b[96mINFO\u001b[0m") + message);
    }

    public void debug(final String message) {
        if (!PluginBase.getInstance().getConfigManager().getGeneralConfig().isDebugMode()) return;

        LOGGER.info(prefix.replace("%LEVEL%", "\u001b[35mDEBUG\u001b[0m") + message);
    }

    public void big(final String message) {
        LOGGER.info(bigPrefix + "\n" + message);
    }

    public void watermark() {
        big("© 5170 ↝ 2025");
    }
}
