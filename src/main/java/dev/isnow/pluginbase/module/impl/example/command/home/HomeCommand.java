package dev.isnow.pluginbase.module.impl.example.command.home;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.menu.HomeMenu;
import dev.isnow.pluginbase.module.impl.example.teleport.TeleportManager;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Command({"home", "dom"})
@Description("Command to open home menu")
@Permission("mcrekus.home")
@SuppressWarnings("unused")
public class HomeCommand extends ModuleCommand<ExampleModule> {
    public HomeCommand(final ExampleModule module) {
        super(module);
    }

    @Usage
    public void execute(final BukkitSource source) {
        final Player player = source.asPlayer();
        final ExampleModuleConfig config = module.getConfig();

        source.reply(ComponentUtil.deserialize(config.getOpenHomeMessage()));

        PlayerData.findByOfflinePlayerAsync(player, (session, data) -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PluginBase.getInstance().getHookManager().getMenuAPI().openMenu(player, new HomeMenu(module, data, session));
                }
            }.runTask(module.getPlugin());
        });
    }

    @Usage
    @Async
    public void execute(final BukkitSource source, @SuggestionProvider("home") @Greedy final String name) {
        final Player player = source.asPlayer();
        final ExampleModuleConfig config = module.getConfig();

        PlayerData.findByOfflinePlayerAsync(player, (session, data) -> {
            if (data == null) {
                player.sendMessage(ComponentUtil.deserialize("&cWystąpił błąd podczas ładowania danych gracza. Spróbuj ponownie później."));
                return;
            }

            final HomeData home = data.getHomeLocations().get(name);
            if (home == null) {
                player.sendMessage(
                        ComponentUtil.deserialize(module.getConfig().getDelHomeNotFoundMessage(),
                                null, "%home%", name));
                return;
            }

            final TeleportManager teleportManager = module.getTeleportManager();

            if(player.hasPermission("mcrekus.home.bypass")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HomeMenu.teleportPlayer(config, home, player);
                    }
                }.runTask(module.getPlugin());
            } else {
                if(teleportManager.isPlayerTeleporting(player.getUniqueId())) {
                    player.sendMessage(ComponentUtil.deserialize(config.getHomeTeleportingMessage()));
                    return;
                }

                final BukkitTask bukkitRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        teleportManager.removePlayerTeleporting(player.getUniqueId());
                        HomeMenu.teleportPlayer(config, home, player);
                    }
                }.runTaskLater(module.getPlugin(), config.getHomeDelayTime() * 20L);

                teleportManager.addPlayerTeleporting(player.getUniqueId(), bukkitRunnable);
                player.sendMessage(ComponentUtil.deserialize(config.getHomeTeleportingRightNowMessage(), null, "%time%", String.valueOf(config.getHomeDelayTime())));
            }
        });

    }
}
