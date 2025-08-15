package dev.isnow.pluginbase.util;

import dev.isnow.pluginbase.PluginBase;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

@UtilityClass
public class PDCUtil {
    public <T, Z> Z getData(final ItemStack item, final String keyString, final PersistentDataType<T, Z> dataType) {
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        final NamespacedKey key = new NamespacedKey(PluginBase.getInstance().getPluginMeta().getName().toLowerCase(Locale.ROOT), keyString.toLowerCase());
        return dataContainer.get(key, dataType);
    }

    public <T, Z> void setData(final ItemStack item, final String keyString, final PersistentDataType<T, Z> dataType, final Z data) {
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        final NamespacedKey key = new NamespacedKey(PluginBase.getInstance().getPluginMeta().getName().toLowerCase(Locale.ROOT), keyString.toLowerCase());
        dataContainer.set(key, dataType, data);

        item.setItemMeta(meta);
    }
}
