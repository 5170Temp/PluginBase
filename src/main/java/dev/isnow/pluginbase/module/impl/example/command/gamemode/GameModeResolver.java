package dev.isnow.pluginbase.module.impl.example.command.gamemode;

import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.command.parameters.CommandParameter;
import studio.mevera.imperat.context.SuggestionContext;
import studio.mevera.imperat.resolvers.SuggestionResolver;

import java.util.List;

public class GameModeResolver implements SuggestionResolver<BukkitSource> {

    @Override
    public List<String> autoComplete(SuggestionContext<BukkitSource> context,
            CommandParameter<BukkitSource> parameter) {
        return GamemodeCommand.GAME_MODES.keySet().stream().toList();
    }
}
