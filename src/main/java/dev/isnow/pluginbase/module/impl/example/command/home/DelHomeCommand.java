package dev.isnow.pluginbase.module.impl.example.command.home;

import dev.isnow.pluginbase.data.impl.PlayerData;
import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.annotations.*;
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

        final HomeData data = HomeData.findByUuid(player.getUniqueId());
        if(data == null) {
            player.sendMessage(ComponentUtil.deserialize("&cWystąpił błąd podczas ładowania danych gracza. Spróbuj ponownie później."));
            return;
        }

        final BaseLocation home = data.getHomes().get(name);

        if(home == null) {
            player.sendMessage(ComponentUtil.deserialize(config.getDelHomeNotFoundMessage(), null, "%home%", name));
            return;
        }

        data.removeHome(name);
        player.sendMessage(ComponentUtil.deserialize(config.getDelHomeMessage(), null, "%home%", name));
    }
}
