package dev.isnow.pluginbase.module.impl.example.command;

import dev.isnow.pluginbase.module.ModuleCommand;
import dev.isnow.pluginbase.module.impl.example.ExampleModule;
import dev.isnow.pluginbase.module.impl.example.config.ExampleModuleConfig;
import dev.isnow.pluginbase.module.impl.example.menu.BinMenu;
import dev.isnow.pluginbase.util.ComponentUtil;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.annotations.Command;
import studio.mevera.imperat.annotations.Description;
import studio.mevera.imperat.annotations.Permission;
import studio.mevera.imperat.annotations.Usage;
import org.bukkit.entity.Player;

@Command({"bin", "smietnik", "kosz"})
@Description("Command to open bin")
@Permission("mcrekus.bin")
@SuppressWarnings("unused")
public class BinCommand extends ModuleCommand<ExampleModule> {
    private final BinMenu binMenu;

    public BinCommand(ExampleModule module) {
        super(module);

        binMenu = new BinMenu(module);
    }

    @Usage
    //@Async
    public void openBin(final BukkitSource source) {
        final ExampleModuleConfig config = module.getConfig();

        final Player player = source.asPlayer();
        module.getPlugin().getHookManager().getMenuAPI().openMenu(player, binMenu);

        source.reply(ComponentUtil.deserialize(config.getBinOpenedMessage()));
    }
}
