package dev.isnow.pluginbase.module;

import org.bukkit.event.Listener;

public abstract class ModuleListener<M extends Module<?>> implements Listener {
    protected final M module;

    public ModuleListener(final M module) {
        this.module = module;
    }
}
