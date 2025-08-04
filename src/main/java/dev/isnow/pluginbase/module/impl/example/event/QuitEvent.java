package dev.isnow.pluginbase.module.impl.example.event;

import dev.isnow.pluginbase.module.ModuleListener;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent extends ModuleListener<ExampleModule> {
    public QuitEvent(final ExampleModule module) {
        super(module);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        event.quitMessage(null);
    }

}
