package dev.isnow.pluginbase.module.impl.example.command.gamemode;

import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Map;

@Command({"gm", "gamemode"})
@Description("Gamemode command")
@Permission("mcrekus.gamemode")
@SuppressWarnings("unused")
public class GamemodeCommand extends ModuleCommand<ExampleModule> {
    public static final Map<String, GameMode> GAME_MODES = Map.ofEntries(
            Map.entry("0", GameMode.SURVIVAL),
            Map.entry("survival", GameMode.SURVIVAL),
            Map.entry("przetrwanie", GameMode.SURVIVAL),
            Map.entry("1", GameMode.CREATIVE),
            Map.entry("creative", GameMode.CREATIVE),
            Map.entry("kreatywny", GameMode.CREATIVE),
            Map.entry("2", GameMode.ADVENTURE),
            Map.entry("adventure", GameMode.ADVENTURE),
            Map.entry("przygodowy", GameMode.ADVENTURE),
            Map.entry("3", GameMode.SPECTATOR),
            Map.entry("obserwator", GameMode.SPECTATOR),
            Map.entry("spectator", GameMode.SPECTATOR)
    );

    public GamemodeCommand(ExampleModule module) {
        super(module);
    }

    @Usage
    @Async
    public void usage(final BukkitSource source) {
        final ExampleModuleConfig config = module.getConfig();

        source.reply(ComponentUtil.deserialize(config.getGamemodeNoArgsMessage()));
    }

    @Usage
    @Async
    public void execute(final BukkitSource source, @Named("gamemode") @SuggestionProvider("gamemode") final String gamemode, @Optional @Named("player") final Player target) {
        final ExampleModuleConfig config = module.getConfig();

        final GameMode foundGameMode = GAME_MODES.get(gamemode.toLowerCase());

        final Player player = source.asPlayer();

        BaseLogger.debug("1");
        if (foundGameMode != null && player.hasPermission("mcrekus.gamemode." + foundGameMode.name().toLowerCase())) {
            BaseLogger.debug("23");
            if(target != null) {
                BaseLogger.debug("69");
                target.setGameMode(foundGameMode);
                target.setAllowFlight(foundGameMode == GameMode.CREATIVE
                        || foundGameMode == GameMode.SPECTATOR);
                source.reply(
                        ComponentUtil.deserialize(config.getGamemodeChangedOtherMessage(), null,
                                "%gamemode%", getTranslation(foundGameMode), "%player%",
                                target.getName()));
            } else {
                BaseLogger.debug("2");
                player.setGameMode(foundGameMode);
                player.setAllowFlight(foundGameMode == GameMode.CREATIVE || foundGameMode == GameMode.SPECTATOR);
                source.reply(ComponentUtil.deserialize(config.getGamemodeChangedMessage(), null, "%gamemode%", getTranslation(foundGameMode)));
            }
            BaseLogger.debug("3");
            if(config.getGamemodeChangedSound() != null) {
                player.playSound(player.getLocation(), config.getGamemodeChangedSound(), 1.0F, 1.0F);
            }
        } else {
            source.reply(ComponentUtil.deserialize(config.getGamemodeInvalidMessage()));
        }

    }


    private String getTranslation(final GameMode input) {
        return switch (input) {
            case SURVIVAL -> module.getConfig().getGamemodeSurvival();
            case CREATIVE -> module.getConfig().getGamemodeCreative();
            case ADVENTURE -> module.getConfig().getGamemodeAdventure();
            case SPECTATOR -> module.getConfig().getGamemodeSpectator();
            default -> "Unknown";
        };
    }
}

