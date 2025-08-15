package dev.isnow.pluginbase.menu;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.hook.HookManager;
import io.github.mqzen.menus.misc.DataRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuManager {

    public void openMenuAsync(final Player player, final GenericMenu menu) {
        openMenuAsync(player, menu, DataRegistry.empty());
    }

    public void openMenuAsync(final Player player, final GenericMenu menu, final DataRegistry providedRegistry) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openMenu(player, menu, providedRegistry);
            }
        }.runTaskAsynchronously(PluginBase.getInstance());
    }

    public void openMenu(final Player player, final GenericMenu menu) {
        openMenu(player, menu, DataRegistry.empty());
    }

    private void openMenu(final Player player, final GenericMenu menu, final DataRegistry providedRegistry) {
        final DataRegistry data = menu.getDataRegistry(player, providedRegistry);

        final HookManager hookManager = PluginBase.getInstance().getHookManager();

        if (hookManager.isUniverseSpigot() || Bukkit.isPrimaryThread()) {
            PluginBase.getInstance().getHookManager().getMenuAPI().openMenu(player, menu, data);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PluginBase.getInstance().getHookManager().getMenuAPI().openMenu(player, menu, data);
                }
            }.runTask(PluginBase.getInstance());
        }
    }
}
