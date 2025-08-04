package dev.isnow.pluginbase.command;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.command.impl.BaseCommand;
import dev.isnow.pluginbase.module.impl.example.command.gamemode.GameModeResolver;
import dev.isnow.pluginbase.module.impl.example.command.home.HomeResolver;
import dev.velix.imperat.BukkitImperat;

public class CommandManager {
    private final PluginBase plugin;

    private BukkitImperat commandManager;

    public CommandManager(final PluginBase plugin) {
        this.plugin = plugin;
    }

    public void initCommandManager() {
        commandManager = BukkitImperat.builder(plugin).applyBrigadier(true)
                .namedSuggestionResolver("gamemode", new GameModeResolver())
                .namedSuggestionResolver("home", new HomeResolver())
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
