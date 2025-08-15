package dev.isnow.pluginbase.module.impl.example.event;

import dev.isnow.pluginbase.data.impl.PlayerData;
import dev.isnow.pluginbase.event.impl.PreloadingFinishedEvent;
import dev.isnow.pluginbase.module.ModuleListener;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LoadEvent extends ModuleListener<ExampleModule> {
    public LoadEvent(final ExampleModule module) {
        super(module);
    }

    @EventHandler
    public void onPlayerLogin(final PreloadingFinishedEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = event.getPlayerData();

        final HomeData result = HomeData.findByUuid(player.getUniqueId());

        if (result == null) {
            new HomeData(data).save();
        }
    }
}
