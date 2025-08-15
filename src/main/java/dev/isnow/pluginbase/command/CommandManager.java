package dev.isnow.pluginbase.command;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.command.impl.BaseCommand;
import dev.isnow.pluginbase.module.ModuleManager;
import dev.isnow.pluginbase.module.impl.example.command.gamemode.GameModeResolver;
import dev.isnow.pluginbase.module.impl.example.command.home.HomeResolver;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.suggestion.OfflinePlayerParameterType;
import org.bukkit.OfflinePlayer;
import studio.mevera.imperat.BukkitImperat;
import studio.mevera.imperat.BukkitImperat;
import studio.mevera.imperat.exception.PermissionDeniedException;

import java.math.BigDecimal;

public class CommandManager {
    private final PluginBase plugin;

    private BukkitImperat commandManager;

    public CommandManager(final PluginBase plugin) {
        this.plugin = plugin;
    }

    public void initCommandManager() {
        commandManager = studio.mevera.imperat.BukkitImperat.builder(plugin)
                .dependencyResolver(ModuleManager.class, plugin::getModuleManager)
                .parameterType(OfflinePlayer.class, new OfflinePlayerParameterType())
                .throwableResolver(PermissionDeniedException.class, ((exception, context) -> {
                    context.source().reply(ComponentUtil.deserialize(plugin.getConfigManager().getGeneralConfig().getNotEnoughPermissions()));
                }))
                .handleExecutionConsecutiveOptionalArguments(true)
                .overlapOptionalParameterSuggestions(true)
                .build();


        commandManager.registerCommand(new BaseCommand(plugin));
    }

    public void registerCommand(final Object command) {
        commandManager.registerCommand(command);
    }

    public void unRegisterCommand(final String command) {
        commandManager.unregisterCommand(command);
    }
}
