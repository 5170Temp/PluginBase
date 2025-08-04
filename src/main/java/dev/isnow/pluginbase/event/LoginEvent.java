package dev.isnow.pluginbase.event;

import dev.isnow.pluginbase.PluginBase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginEvent implements Listener {

    private final PluginBase plugin;

    public LoginEvent(PluginBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        plugin.getDatabaseManager().preloadPlayer(player);
    }
}
