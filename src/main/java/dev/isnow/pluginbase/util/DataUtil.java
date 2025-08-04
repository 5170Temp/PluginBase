package dev.isnow.pluginbase.util;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.data.PlayerData;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class DataUtil {

    public void saveData(final Player player) {
        PlayerData.findByOfflinePlayerAsync(player, (session, data) -> {
            data.save(session);
        });
    }
}
