package dev.isnow.pluginbase.event.impl;

import dev.isnow.pluginbase.data.impl.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PreloadingFinishedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final PlayerData playerData;

    public PreloadingFinishedEvent(final Player player, final PlayerData playerData) {
        super(true);
        this.player = player;
        this.playerData = playerData;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
