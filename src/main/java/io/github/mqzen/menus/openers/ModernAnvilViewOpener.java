package io.github.mqzen.menus.openers;

import io.github.mqzen.menus.Lotus;
import io.github.mqzen.menus.base.MenuView;
import io.github.mqzen.menus.base.ViewOpener;
import io.github.mqzen.menus.misc.ViewData;
import io.github.mqzen.menus.titles.ModernTitle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;

public class ModernAnvilViewOpener implements ViewOpener {

    /**
     * Creates an anvil inventory , opens it for the player using the dynamic data
     * of the menu that is cached within 'ViewData'
     * This is a primitive way of creating an anvil view as the third item will
     * get deleted after typing in the anvil's 'name' textbox
     *
     * @param manager  the manager
     * @param player   the player opening this menu
     * @param menu     the menu to open
     * @param viewData the data of the menu to open
     * @return the menu inventory opened for this player
     */
    @Override
    public @NotNull Inventory openMenu(Lotus manager, Player player, MenuView<?> menu, ViewData viewData) {
        // Anvil inventories always have 3 slots
        int size = 3;

        ModernTitle modernTitle = (ModernTitle) viewData.title();

        AnvilView inv = MenuType.ANVIL.builder().title(modernTitle.getComponent()).build(player);

        viewData.content().forEachItem((slot, button) -> {
            int anvilSlot = slot.getSlot();
            if (anvilSlot >= 0 && anvilSlot < size) {
                inv.setItem(anvilSlot, button.getItem());
            }
        });

        player.openInventory(inv);
        return (Inventory) inv;
    }
}
