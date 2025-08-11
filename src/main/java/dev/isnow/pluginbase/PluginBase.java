package dev.isnow.pluginbase;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.isnow.pluginbase.command.CommandManager;
import dev.isnow.pluginbase.config.ConfigManager;
import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.database.DatabaseManager;
import dev.isnow.pluginbase.event.LoginEvent;
import dev.isnow.pluginbase.event.QuitEvent;
import dev.isnow.pluginbase.hook.HookManager;
import dev.isnow.pluginbase.module.ModuleManager;
import dev.isnow.pluginbase.util.DateUtil;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter@Setter
public final class PluginBase extends JavaPlugin {
    @Getter
    private static PluginBase instance;

    private final HookManager hookManager = new HookManager(this);
    private final DatabaseManager databaseManager = new DatabaseManager(this);
    private final ConfigManager configManager = new ConfigManager(this);
    private final ModuleManager moduleManager = new ModuleManager(this);
    private final CommandManager commandManager = new CommandManager(this);

    private ExecutorService threadPool;
    private ScheduledExecutorService scheduler;

    @Override
    public void onLoad() {
        instance = this;
        //BukkitLoggerFactory.provideBukkitLogger(this.getLogger());

        BaseLogger.info("Loading pre-hooks");
        hookManager.onLoad();
    }

    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();
        BaseLogger.watermark();

        BaseLogger.info("Initializing config");
        configManager.reloadConfigs();

        final String pluginName = getPluginMeta().getName();
        threadPool = Executors.newFixedThreadPool(configManager.getGeneralConfig().getThreadAmount(), new ThreadFactoryBuilder().setNameFormat(pluginName + "-worker-thread-%d").build());
        scheduler = Executors.newScheduledThreadPool(configManager.getGeneralConfig().getThreadAmount(), new ThreadFactoryBuilder().setNameFormat(pluginName + "-scheduler-thread-%d").build());

        BaseLogger.info("Loading hooks");
        hookManager.onEnable();

        BaseLogger.info("Loading modules");
        moduleManager.loadAndEnableModules(getClass().getPackage().getName() + ".module.impl");

        Bukkit.getPluginManager().registerEvents(new LoginEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);

        final String time = DateUtil.formatElapsedTime(System.currentTimeMillis() - startTime);
        BaseLogger.info("Finished enabling in " + time + " seconds.");
    }

    @Override
    public void onDisable() {
        final long startTime = System.currentTimeMillis();
        BaseLogger.watermark();

        BaseLogger.info("Saving player data");
        for(final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerData.findByOfflinePlayerAsync(onlinePlayer, (session, data) -> data.save(session));
        }

        BaseLogger.info("Shutting down thread pool");
        threadPool.shutdown();
        try {
            final boolean shutdown = threadPool.awaitTermination(15000, java.util.concurrent.TimeUnit.MILLISECONDS);

            if (!shutdown) {
                BaseLogger.error("Failed to shut down thread pool, expect data loss");
            }
        } catch (final InterruptedException ignored) {}

        BaseLogger.info("Disabling modules");
        moduleManager.disableModules();

        BaseLogger.info("Shutting down database");
        databaseManager.getDatabase().shutdown();

        BaseLogger.info("Shutting down hooks");
        hookManager.onDisable();

        final String time = DateUtil.formatElapsedTime(System.currentTimeMillis() - startTime);
        BaseLogger.info("Finished disabling in " + time + " seconds.");
    }
}
