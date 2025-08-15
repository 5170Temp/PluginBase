package dev.isnow.pluginbase.module.impl.example.command.home;

import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.PermissionUtil;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.annotations.*;
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

        final HomeData homeData = HomeData.findByUuid(player.getUniqueId());
        if(homeData == null) {
            source.reply(ComponentUtil.deserialize("&cWystąpił błąd podczas ładowania danych gracza. Spróbuj ponownie później."));
            return;
        }

        final BaseLocation home = homeData.getHomes().get(name);

        if (home != null) {
            homeData.addHome(name, new BaseLocation(player.getLocation()));

            source.reply(ComponentUtil.deserialize(config.getSetHomeUpdatedMessage(), null, "%home%", name));
            player.playSound(player.getLocation(), config.getSetHomeSound(), 1.0F, 1.0F);
        } else {
            final int maxHomes = PermissionUtil.getMaxAllowedHomes(module.getConfig().getMaxAllowedHomesByDefault(), player);
            if (homeData.getHomes().size() >= maxHomes) {
                source.reply(ComponentUtil.deserialize(config.getSetHomeAtLimitMessage(), null, "%max%", String.valueOf(maxHomes)));
                return;
            }

            homeData.addHome(name, new BaseLocation(player.getLocation()));

            source.reply(ComponentUtil.deserialize(config.getSetHomeCreatedMessage(), null, "%home%", name));
            player.playSound(player.getLocation(), config.getSetHomeSound(), 1.0F, 1.0F);
        }
    }
}
