package dev.isnow.pluginbase.module.impl.example.command.home;

import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.entity.Player;

@Command({"delhome", "usundom"})
@Description("Command to delete a home")
@Permission("mcrekus.delhome")
@SuppressWarnings("unused")
public class DelHomeCommand extends ModuleCommand<ExampleModule> {
    public DelHomeCommand(ExampleModule module) {
        super(module);
    }

    @Async
    @Usage
    public void executeDefault(final BukkitSource source) {
        final ExampleModuleConfig config = module.getConfig();

        source.reply(ComponentUtil.deserialize(config.getDelHomeUsageMessage()));
    }

    @Async
    @Usage
    public void execute(final BukkitSource source, @Named("name") @SuggestionProvider("home") @Greedy final String name) {
        final ExampleModuleConfig config = module.getConfig();
        final Player player = source.asPlayer();

        PlayerData.findByOfflinePlayerAsync(player, (session, data) -> {
            if(data == null) {
                player.sendMessage(ComponentUtil.deserialize("&cWystąpił błąd podczas ładowania danych gracza. Spróbuj ponownie później."));
                return;
            }

            final HomeData home = data.getHomeLocations().get(name);

            if(home == null) {
                player.sendMessage(ComponentUtil.deserialize(config.getDelHomeNotFoundMessage(), null, "%home%", name));
                return;
            }

            data.getHomeLocations().remove(name);
            player.sendMessage(ComponentUtil.deserialize(config.getDelHomeMessage(), null, "%home%", name));

            data.save(session);
        });
    }
}
