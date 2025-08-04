package dev.isnow.pluginbase.util;


import dev.isnow.pluginbase.PluginBase;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class BaseLogger {
    public static final String bigPrefix = "PLACEHOLDER";
    private final String prefix = "&7[" + PluginBase.getInstance().getPluginMeta().getName() + "] >>";

    public void error(final String log) {
        Bukkit.getConsoleSender().sendMessage(ComponentUtil.deserialize(prefix + " &c[ERROR] " + log));
    }

    public void info(final String log) {
        Bukkit.getConsoleSender().sendMessage(ComponentUtil.deserialize(prefix + " &f[INFO] " + log));
    }

    public void warn(final String log) {
        Bukkit.getConsoleSender().sendMessage(ComponentUtil.deserialize(prefix + " &e[WARN] " + log));
    }

    public void debug(final String log) {
        debug(log, false);
    }

    public void debug(final String log, final boolean force) {
        if(!force && !PluginBase.getInstance().getConfigManager().getGeneralConfig().isDebugMode()) return;

        Bukkit.getConsoleSender().sendMessage(ComponentUtil.deserialize(prefix + " &a[DEBUG] " + log));
    }

    public void big(final String log) {
        Bukkit.getConsoleSender().sendMessage(ComponentUtil.deserialize(bigPrefix + "\n" + log));
    }

    public void watermark() {
        big("© 5170 ↝ 2025");
    }
}