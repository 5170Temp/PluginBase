package dev.isnow.pluginbase.config.impl.inventory;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import dev.isnow.pluginbase.util.ComponentUtil;
import io.github.mqzen.menus.misc.Slot;
import io.github.mqzen.menus.misc.itembuilder.ComponentItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Configuration
@AllArgsConstructor
public class ItemConfig implements MenuItemConfig {

    @Comment({"", "Item material"})
    Material material = Material.STONE;

    @Comment({"", "Slot number"})
    int slot = 0;

    @Comment({"", "Item name"})
    String name = "&fDomyślny przedmiot";

    @Comment({"", "Item lore"})
    List<String> lore = new ArrayList<>();

    @Comment({"", "Item amount"})
    int amount = 1;

    public ItemConfig() {}

    public static Builder builder() {
        return new Builder();
    }

    public ItemStack toItemStack() {
        final List<Component> serializedLore = lore.stream()
                .map(ComponentUtil::deserialize)
                .toList();

        final Component displayName = ComponentUtil.deserialize(getName());

        return ComponentItemBuilder.modern(material, amount)
                .setDisplay(displayName)
                .setLore(serializedLore)
                .build();
    }

    public static class Builder {
        private Material material = Material.STONE;
        private Slot slot = Slot.of(0);
        private String name = "PLACEHOLDER";
        private List<String> lore = new ArrayList<>();
        private int amount = 1;

        public Builder material(final Material material) {
            this.material = material;
            return this;
        }

        public Builder slot(final int slot) {
            this.slot = Slot.of(slot);
            return this;
        }

        public Builder slot(final Slot slot) {
            this.slot = slot;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder lore(final List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        public ItemConfig build() {
            return new ItemConfig(material, slot.getSlot(), name, lore, amount);
        }
    }
}