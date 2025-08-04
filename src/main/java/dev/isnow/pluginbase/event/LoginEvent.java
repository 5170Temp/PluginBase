package dev.isnow.pluginbase.event;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        final DatabaseManager databaseManager = PluginBase.getInstance().getDatabaseManager();

        final Player player = event.getPlayer();

        databaseManager.preloadPlayer(player);
    }
}
