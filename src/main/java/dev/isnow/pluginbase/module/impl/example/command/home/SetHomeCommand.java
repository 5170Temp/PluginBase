package dev.isnow.pluginbase.module.impl.example.command.home;

import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.PermissionUtil;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.entity.Player;

@Command({"sethome", "ustawdom"})
@Description("Command to set a home")
@Permission("mcrekus.sethome")
@SuppressWarnings("unused")
public class SetHomeCommand extends ModuleCommand<ExampleModule> {

    public SetHomeCommand(final ExampleModule module) {
        super(module);
    }

    @Usage
    @Async
    public void executeDefault(final BukkitSource source) {
        final ExampleModuleConfig config = module.getConfig();

        source.reply(ComponentUtil.deserialize(config.getSetHomeUsageMessage()));
    }

    @Usage
    @Async
    public void execute(final BukkitSource source, @Named("name") @SuggestionProvider("home") @Greedy final String name) {
        final ExampleModuleConfig config = module.getConfig();

        final Player player = source.asPlayer();

        PlayerData.findByOfflinePlayerAsync(player, (session, data) -> {
            if(data == null) {
                source.reply(ComponentUtil.deserialize("&cWystąpił błąd podczas ładowania danych gracza. Spróbuj ponownie później."));
                return;
            }

            final HomeData home = data.getHomeLocations().get(name);

            if (home != null) {
                home.setLocation(new BaseLocation(player.getLocation()));

                source.reply(ComponentUtil.deserialize(config.getSetHomeUpdatedMessage(), null, "%home%", name));
                player.playSound(player.getLocation(), config.getSetHomeSound(), 1.0F, 1.0F);
            } else {
                final int maxHomes = PermissionUtil.getMaxAllowedHomes(module.getConfig().getMaxAllowedHomesByDefault(), player);
                if (data.getHomeLocations().size() >= maxHomes) {
                    source.reply(ComponentUtil.deserialize(config.getSetHomeAtLimitMessage(), null, "%max%", String.valueOf(maxHomes)));
                    return;
                }

                final HomeData homeData = new HomeData(name, new BaseLocation(player.getLocation()), data);
                data.getHomeLocations().put(name, homeData);

                source.reply(ComponentUtil.deserialize(config.getSetHomeCreatedMessage(), null, "%home%", name));
                player.playSound(player.getLocation(), config.getSetHomeSound(), 1.0F, 1.0F);
            }

            data.save(session);
        });
    }
}
