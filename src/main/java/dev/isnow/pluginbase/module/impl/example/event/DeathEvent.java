package dev.isnow.pluginbase.module.impl.example.event;

import dev.isnow.pluginbase.module.ModuleListener;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent extends ModuleListener<ExampleModule> {
    public DeathEvent(final ExampleModule module) {
        super(module);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        event.deathMessage(null);
    }
}
