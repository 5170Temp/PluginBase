package dev.isnow.pluginbase.module.impl.example.event;

import dev.isnow.pluginbase.module.ModuleListener;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.teleport.TeleportManager;
import dev.isnow.pluginbase.util.ComponentUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class TeleportMoveEvent extends ModuleListener<ExampleModule> {
    public TeleportMoveEvent(final ExampleModule module) {
        super(module);
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            final TeleportManager teleportManager = module.getTeleportManager();
            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();

            if(!teleportManager.isPlayerTeleporting(uuid)) return;

            final int taskId = teleportManager.getTaskId(uuid);

            Bukkit.getScheduler().cancelTask(taskId);
            teleportManager.removePlayerTeleporting(uuid);

            player.sendMessage(ComponentUtil.deserialize(module.getConfig().getTpaMovedMessage()));
        }
    }
}
