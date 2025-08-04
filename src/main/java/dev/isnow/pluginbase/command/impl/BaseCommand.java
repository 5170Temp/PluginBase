package dev.isnow.pluginbase.command.impl;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.database.DatabaseManager;
import dev.isnow.pluginbase.module.Module;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.DateUtil;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.stat.Statistics;

// TODO: USE NEW IMPERAT STUFF
@Command({"base", "basecmd"})
@Description("Master Base command")
@Permission("mcrekus.admin")
@SuppressWarnings("unused")
public class BaseCommand {
    private final PluginBase plugin;

    public BaseCommand(PluginBase plugin) {
        this.plugin = plugin;
    }

    @Usage
    @Async
    public void executeDefault(final BukkitSource source) {
        final Player player = source.asPlayer();

        source.reply(ComponentUtil.deserialize("&aCommands:"));
        source.reply(ComponentUtil.deserialize("&a/base reload - Reloads the general config and modules"));
        source.reply(ComponentUtil.deserialize("&a/base manualsave - Saves all player data to the database manually"));
        source.reply(ComponentUtil.deserialize("&a/base dbstatistics - Shows database statistics [DEBUG MODE ONLY]"));
        source.reply(ComponentUtil.deserialize("&a/base resetconfig [moduleName] - Resets config to default values"));
    }
    
    @Usage
    @Async
    public void execute(final BukkitSource source, @Named("action") @Suggest({"reload", "manualsave", "dbstatistics", "resetconfig"}) final String action, @Named("module") @Optional final String moduleName) {
        
        if (action.equalsIgnoreCase("reload")) {
            final long startTime = System.currentTimeMillis();

            source.reply(ComponentUtil.deserialize("&aReloading general config..."));
            plugin.getConfigManager().reloadConfigs();
            source.reply(ComponentUtil.deserialize("&aReloaded general config successfully!"));

            source.reply(ComponentUtil.deserialize("&aReloading modules..."));
            plugin.getModuleManager().disableModules();
            plugin.getModuleManager().loadAndEnableModules("dev.isnow.pluginbase.module.impl");
            source.reply(ComponentUtil.deserialize("&aReloaded modules successfully!"));

            final String date = DateUtil.formatElapsedTime((System.currentTimeMillis() - startTime));

            source.reply(ComponentUtil.deserialize("&cFinished reloading in " + date + " seconds."));
            source.reply(ComponentUtil.deserialize("&aSome modules may require a server restart to take effect."));
            return;
        }

        final DatabaseManager databaseManager = plugin.getDatabaseManager();

        if (action.equalsIgnoreCase("manualsave")) {
            source.reply(ComponentUtil.deserialize("&aSaving player data..."));
            for(final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerData.findByOfflinePlayerAsync(onlinePlayer, (session, user) -> user.save(session));
            }
            source.reply(ComponentUtil.deserialize("&aSaved player data successfully!"));
            return;
        }

        if (action.equalsIgnoreCase("dbstatistics")) {
            final Statistics statistics = databaseManager.getDatabase().getStatistics();

            source.reply(ComponentUtil.deserialize("&aDatabase statistics:"));
            source.reply(ComponentUtil.deserialize("&aHit count: " + statistics.getSecondLevelCacheHitCount()));
            source.reply(ComponentUtil.deserialize("&aMiss count: " + statistics.getSecondLevelCacheMissCount()));
            source.reply(ComponentUtil.deserialize("&aPut count: " + statistics.getSecondLevelCachePutCount()));
        }

        if(action.equalsIgnoreCase("resetconfig")) {
            if(moduleName == null || moduleName.isEmpty()) {
                source.reply(ComponentUtil.deserialize("&cUsage: /mcrekus resetconfigs [moduleName]"));
                return;
            }

            final java.util.Optional<Module<?>> module = plugin.getModuleManager().getModuleByName(moduleName);

            if(module.isEmpty()) {
                source.reply(ComponentUtil.deserialize("&cModule not found!"));
                return;
            }

            final Module<?> moduleInstance = module.get();
            moduleInstance.getConfig().delete();
            moduleInstance.initializeConfig();

            source.reply(ComponentUtil.deserialize("&aConfig reset for module " + moduleName));
        }
    }
}
