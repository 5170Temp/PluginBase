package dev.isnow.pluginbase.module;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.util.ReflectionUtil;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import dev.velix.imperat.annotations.Command;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;

@Getter
public abstract class Module<T extends ModuleConfig> {
    protected final PluginBase plugin;
    private final Set<Listener> registeredListeners = new HashSet<>();
    private final Set<Object> registeredCommands = new HashSet<>();

    protected T config;

    public Module() {
        this.plugin = null;
    }

    public Module(PluginBase plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public final void initializeConfig() {
        try {
            final Class<T> configClass = ReflectionUtil.getGenericTypeClass(this.getClass(), Module.class, 0);

            if (configClass == null) {
                BaseLogger.debug("Module " + getClass().getSimpleName() + " config is missing a default constructor.");
                return;
            }

            T configInstance = configClass.getDeclaredConstructor().newInstance();
            this.config = (T) configInstance.load();
        } catch (final Exception e) {
            BaseLogger.error("Failed to automatically initialize config for module " + getClass().getSimpleName(), e);
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public Collection<Class<?>> getDatabaseEntities() {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public final void registerCommands(String packageName) {
        try {
            final List<Class<?>> commandClasses = ReflectionUtil.getClasses(getClass().getPackageName() + "." + packageName);
            for (final Class<?> clazz : commandClasses) {
                if (clazz.isAnnotationPresent(Command.class)) {
                    registerCommand(clazz);
                }
            }
        } catch (final Exception e) {
            BaseLogger.error("Failed to register commands for module " + getClass().getSimpleName(), e);
        }
    }

    private void registerCommand(final Class<?> commandClass) {
        BaseLogger.debug("Registering command " + commandClass.getSimpleName());
        try {
            Object commandInstance;
            try {
                java.lang.reflect.Constructor<?> diConstructor = commandClass.getConstructor(this.getClass());
                commandInstance = diConstructor.newInstance(this);
                BaseLogger.debug("Injected module dependency into " + commandClass.getSimpleName());
            } catch (NoSuchMethodException e) {
                commandInstance = commandClass.getConstructor().newInstance();
                BaseLogger.debug("Inited " + commandClass.getSimpleName() + " with no-arg constructor.");
            }

            plugin.getCommandManager().registerCommand(commandInstance);
            registeredCommands.add(commandInstance);

        } catch (Exception e) {
            BaseLogger.error("Failed to register command " + commandClass.getSimpleName() + " for module " + getClass().getSimpleName(), e);
        }
    }

    public final void unRegisterCommands() {
        registeredCommands.forEach(command -> plugin.getCommandManager().unRegisterCommand(command.getClass().getSimpleName().replaceAll("Command", "")));
        registeredCommands.clear();
    }

    @SuppressWarnings("unchecked")
    public final void registerListeners(String packageName) {
        try {
            final List<Class<?>> listenerClasses = ReflectionUtil.getClasses(getClass().getPackageName() + "." + packageName);
            for (final Class<?> clazz : listenerClasses) {
                if (Listener.class.isAssignableFrom(clazz)) {
                    registerListener((Class<? extends Listener>) clazz);
                }
            }
        } catch (final Exception e) {
            BaseLogger.error("Failed to register listeners for module " + getClass().getSimpleName(), e);
        }
    }

    private void registerListener(final Class<? extends Listener> listenerClass) {
        BaseLogger.debug("Registering listener " + listenerClass.getSimpleName());
        try {
            Listener listenerInstance;
            try {
                java.lang.reflect.Constructor<? extends Listener> diConstructor = listenerClass.getConstructor(this.getClass());
                listenerInstance = diConstructor.newInstance(this);
                BaseLogger.debug("Injected module dependency into " + listenerClass.getSimpleName());
            } catch (NoSuchMethodException e) {
                listenerInstance = listenerClass.getConstructor().newInstance();
                BaseLogger.debug("Instantiated " + listenerClass.getSimpleName() + " with no-arg constructor.");
            }

            Bukkit.getPluginManager().registerEvents(listenerInstance, plugin);
            registeredListeners.add(listenerInstance);

        } catch (Exception e) {
            BaseLogger.error("Failed to register listener " + listenerClass.getSimpleName() + " for module " + getClass().getSimpleName(), e);
        }
    }

    public final void unRegisterListeners() {
        registeredListeners.forEach(HandlerList::unregisterAll);
    }
}