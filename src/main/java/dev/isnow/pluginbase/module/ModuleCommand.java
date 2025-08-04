package dev.isnow.pluginbase.module;

public abstract class ModuleCommand<M extends Module<?>> {
    protected final M module;

    public ModuleCommand(final M module) {
        this.module = module;
    }
}
