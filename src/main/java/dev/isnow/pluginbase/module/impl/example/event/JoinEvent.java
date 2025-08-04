package dev.isnow.pluginbase.module.impl.example.event;

import dev.isnow.pluginbase.module.ModuleListener;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent extends ModuleListener<ExampleModule> {
    public JoinEvent(final ExampleModule module) {
        super(module);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        event.joinMessage(null);
    }

}
