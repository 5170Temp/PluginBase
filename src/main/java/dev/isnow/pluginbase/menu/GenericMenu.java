package dev.isnow.pluginbase.menu;

import dev.isnow.pluginbase.config.impl.inventory.GuiConfig;
import dev.isnow.pluginbase.config.impl.inventory.MenuItemConfig;
import dev.isnow.pluginbase.config.impl.inventory.StatefulItemConfig;
import dev.isnow.pluginbase.util.ComponentUtil;
import dev.isnow.pluginbase.util.Placeholders;
import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.base.MenuView;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.button.Button;
import io.github.mqzen.menus.misc.itembuilder.ComponentItemBuilder;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class GenericMenu extends GuiConfig implements Menu {
    public GenericMenu(String name, String moduleName) {
        super(name, moduleName);
    }

    public abstract void buildDefaults();

    public abstract void handleClick(final Optional<Button> optionalButton, final MenuView<?> playerMenuView, final InventoryClickEvent event);

    public abstract DataRegistry getDataRegistry(final Player player, final DataRegistry providedRegistry);

    public void initialize() {
        if (!exists()) {
            buildDefaults();
            save();
        } else {
            final GenericMenu loaded = load();

            this.setTitle(loaded.getTitle());
            this.setSize(loaded.getSize());
            this.getItems().putAll(loaded.getItems());
        }
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern(ComponentUtil.deserialize(super.getTitle()));
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public @NotNull Capacity getCapacity(final DataRegistry dataRegistry, final Player player) {
        return Capacity.of(super.getSize());
    }

    @Override
    public @NotNull Content getContent(final DataRegistry dataRegistry, final Player player, final Capacity capacity) {
        final Content.Builder builder = Content.builder(capacity);

        final Placeholders placeholders = dataRegistry.getData("placeholders");

        for(final Map.Entry<String, MenuItemConfig> itemConfigEntry : getItems().entrySet()) {
            final MenuItemConfig itemConfig = itemConfigEntry.getValue();
            final String itemName = itemConfigEntry.getKey();

            if (itemConfig instanceof StatefulItemConfig statefulItemConfig) {
                final String state = dataRegistry.getData(itemName + "-state");

                statefulItemConfig.setCurrentState(state);
            }

            final List<Component> serializedLore = itemConfig.getLore().stream()
                    .map(loreLine -> ComponentUtil.deserialize(loreLine, player, placeholders))
                    .toList();

            final Component displayName = ComponentUtil.deserialize(itemConfig.getName(), player, placeholders);

            final ItemStack item = ComponentItemBuilder.modern(itemConfig.getMaterial(), itemConfig.getAmount())
                    .setDisplay(displayName)
                    .setLore(serializedLore)
                    .build();

            final Button button = Button.empty(item);
            button.getDataRegistry().setData("item", itemName);

            builder.setButton(itemConfig.getSlot(), button);
        }

        return builder.build();
    }

    @Override
    public void onPostClick(final MenuView<?> playerMenuView, final InventoryClickEvent event) {
        event.setCancelled(true);

        final int clickedSlot = event.getSlot();

        handleClick(playerMenuView.getContent().getButton(clickedSlot), playerMenuView, event);
    }
}
