package dev.isnow.pluginbase.module.impl.example.command.gamemode;

import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.command.parameters.CommandParameter;
import dev.velix.imperat.context.SuggestionContext;
import dev.velix.imperat.resolvers.SuggestionResolver;

import java.util.List;

public class GameModeResolver implements SuggestionResolver<BukkitSource> {

    @Override
    public List<String> autoComplete(SuggestionContext<BukkitSource> context,
            CommandParameter<BukkitSource> parameter) {
        return GamemodeCommand.GAME_MODES.keySet().stream().toList();
    }
}
