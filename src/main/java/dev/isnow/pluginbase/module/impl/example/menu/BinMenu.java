package dev.isnow.pluginbase.module.impl.example.menu;

import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.util.ComponentUtil;
import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BinMenu implements Menu {
    final ExampleModule module;

    public BinMenu(ExampleModule module) {
        this.module = module;
    }

    @Override
    public String getName() {
        return "bin";
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern(ComponentUtil.deserialize(module.getConfig().getBinGuiName()));
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.ofRows(5);
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player,
            Capacity capacity) {
        return Content.builder(capacity).build();
    }
}
