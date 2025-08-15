package dev.isnow.pluginbase.config.impl.inventory;

import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import org.bukkit.Material;

import java.util.List;

@Polymorphic
@PolymorphicTypes({
        @PolymorphicTypes.Type(type = ItemConfig.class, alias = "Item"),
        @PolymorphicTypes.Type(type = StatefulItemConfig.class, alias = "StatefulItem")
})
public interface MenuItemConfig {
    Material getMaterial();
    int getSlot();
    String getName();
    List<String> getLore();
    int getAmount();
}