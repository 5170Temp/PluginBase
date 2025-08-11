package dev.isnow.pluginbase.module;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.util.ReflectionUtil;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ModuleManager {
    private final PluginBase plugin;

    private final Map<Class<? extends Module<?>>, Module<?>> modules = new HashMap<>();
    private final List<Module<?>> enabledModules = new ArrayList<>();

    public ModuleManager(final PluginBase plugin) {
        this.plugin = plugin;
    }

    public void loadAndEnableModules(final String basePackage) {
        BaseLogger.info("Starting module loading process...");

        final Set<Class<? extends Module<?>>> discoveredModuleClasses = discoverModuleClasses(basePackage);
        if (discoveredModuleClasses.isEmpty()) {
            BaseLogger.warn("No modules found in package: " + basePackage);
            return;
        }

        final List<Class<? extends Module<?>>> sortedModuleClasses;
        try {
            sortedModuleClasses = sortModulesByDependencies(discoveredModuleClasses);
        } catch (final Exception e) {
            BaseLogger.error("Failed to sort module dependencies ", e);
            return;
        }

        final Set<Class<?>> allDatabaseEntities = collectDatabaseEntities(sortedModuleClasses);

        initManagers(allDatabaseEntities);

        for (final Class<? extends Module<?>> moduleClass : sortedModuleClasses) {
            if (!isModuleEnabledInConfig(moduleClass.getSimpleName())) {
                BaseLogger.info("Skipping disabled module: " + moduleClass.getSimpleName());
                continue;
            }

            try {
                BaseLogger.info("Enabling module " + moduleClass.getSimpleName() + "...");
                final Module<?> moduleInstance = initModule(moduleClass);
                modules.put(moduleClass, moduleInstance);

                moduleInstance.initializeConfig();

                moduleInstance.onEnable();
                enabledModules.add(moduleInstance);

                BaseLogger.info("Successfully enabled module " + moduleClass.getSimpleName());
            } catch (final Exception e) {
                BaseLogger.error("Failed to enable module " + moduleClass.getSimpleName() + " ", e);
                modules.remove(moduleClass);
            }
        }
        BaseLogger.info("Module loading finished. " + enabledModules.size() + " modules enabled.");
    }

    public void disableModules() {
        BaseLogger.info("Disabling all modules...");

        Collections.reverse(enabledModules);
        for (final Module<?> module : enabledModules) {
            try {
                BaseLogger.info("Disabling " + module.getClass().getSimpleName() + "...");
                module.onDisable();
            } catch (final Exception e) {
                BaseLogger.error("An error occurred while disabling module " + module.getClass().getSimpleName(), e);
            }
        }

        enabledModules.clear();
        modules.clear();
        BaseLogger.info("All modules have been disabled.");
    }

    private void initManagers(final Set<Class<?>> allDatabaseEntities) {
        BaseLogger.info("Initializing Command Manager...");
        plugin.getCommandManager().initCommandManager();

        BaseLogger.info("Initializing Database Manager with " + allDatabaseEntities.size() + " module entities...");

        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        plugin.getDatabaseManager().initialize(allDatabaseEntities);
        Thread.currentThread().setContextClassLoader(originalClassLoader);

        if (!plugin.getDatabaseManager().getDatabase().isConnected()) {
            BaseLogger.info("Failed to connect to the database! This plugin won't work without an database.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        } else {
            BaseLogger.info("Connected successfully.");

            for(final Player player : Bukkit.getOnlinePlayers()) {
                plugin.getDatabaseManager().preloadPlayer(player);
            }
        }
    }

    private Set<Class<?>> collectDatabaseEntities(final List<Class<? extends Module<?>>> moduleClasses) {
        return moduleClasses.stream()
                .map(this::createDummyInstance)
                .filter(Objects::nonNull)
                .flatMap(module -> module.getDatabaseEntities().stream())
                .collect(Collectors.toSet());
    }

    private Module<?> createDummyInstance(final Class<? extends Module<?>> moduleClass) {
        try {
            return moduleClass.getDeclaredConstructor().newInstance();
        } catch (final NoSuchMethodException e) {
            BaseLogger.debug("Module " + moduleClass.getSimpleName() + " has no default constructor. Cannot pre-scan for entities without full init.");
            return null;
        } catch (final Exception e) {
            BaseLogger.warn("Could not create dummy instance of " + moduleClass.getSimpleName() + " to scan entities. ", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Module<?> initModule(final Class<? extends Module<?>> moduleClass) throws Exception {
        final Constructor<?> constructor = getGreediestConstructor(moduleClass);
        final Class<?>[] paramTypes = constructor.getParameterTypes();
        final Object[] paramInstances = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            final Class<?> paramType = paramTypes[i];
            if (Module.class.isAssignableFrom(paramType)) {
                paramInstances[i] = getModule((Class<? extends Module<?>>) paramType);
            } else if (PluginBase.class.isAssignableFrom(paramType)) {
                paramInstances[i] = plugin;
            } else {
                throw new IllegalStateException("Unknown dependency type in constructor for " + moduleClass.getSimpleName() + ": " + paramType.getSimpleName());
            }
        }

        return (Module<?>) constructor.newInstance(paramInstances);
    }

    @SuppressWarnings({"unchecked"})
    private List<Class<? extends Module<?>>> sortModulesByDependencies(final Set<Class<? extends Module<?>>> moduleClasses) {
        final MutableGraph<Class<? extends Module<?>>> graph = GraphBuilder.directed().build();
        moduleClasses.forEach(graph::addNode);

        for (Class<? extends Module<?>> moduleClass : moduleClasses) {
            final Constructor<?> constructor = getGreediestConstructor(moduleClass);
            for (Class<?> paramType : constructor.getParameterTypes()) {
                if (Module.class.isAssignableFrom(paramType) && moduleClasses.contains(paramType)) {
                    graph.putEdge((Class<? extends Module<?>>) paramType, moduleClass);
                }
            }
        }

        try {
            final Traverser<Class<? extends Module<?>>> traverser = Traverser.forGraph(graph);
            final List<Class<? extends Module<?>>> rootNodes = graph.nodes().stream()
                    .filter(n -> graph.inDegree(n) == 0)
                    .collect(Collectors.toList());

            final Iterable<Class<? extends Module<?>>> postOrder = traverser.depthFirstPostOrder(rootNodes);

            final List<Class<? extends Module<?>>> sortedList = StreamSupport.stream(postOrder.spliterator(), false)
                    .collect(Collectors.toList());

            Collections.reverse(sortedList);

            if (sortedList.size() != moduleClasses.size()) {
                throw new IllegalStateException("Module dependency issue detected. Possible cycle or missing module.");
            }
            return sortedList;
        } catch (final Exception e) {
            throw new IllegalStateException("A cycle was detected in the module dependencies!", e);
        }
    }

    private Constructor<?> getGreediestConstructor(final Class<? extends Module<?>> moduleClass) {
        return Arrays.stream(moduleClass.getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new IllegalStateException("No public constructor found for module: " + moduleClass.getSimpleName()));
    }

    @SuppressWarnings("unchecked")
    private Set<Class<? extends Module<?>>> discoverModuleClasses(final String packageName) {
        try {
            return ReflectionUtil.getClasses(packageName).stream()
                    .filter(Module.class::isAssignableFrom)
                    .filter(c -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
                    .map(c -> (Class<? extends Module<?>>) c)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            BaseLogger.error("Failed to scan for modules in package: " + packageName, e);
            return new HashSet<>();
        }
    }

    private boolean isModuleEnabledInConfig(final String moduleName) {
        final String configName = moduleName.endsWith("Module") ? moduleName.substring(0, moduleName.length() - 6) : moduleName;
        return plugin.getConfigManager().getGeneralConfig().getEnabledModules().contains(configName);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module<?>> T getModule(final Class<T> moduleClass) {
        return (T) modules.get(moduleClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module<?>> Optional<T> getModuleByName(final String name) {
        return modules.values().stream()
                .filter(module -> {
                    final String className = module.getClass().getSimpleName();
                    final boolean exactMatch = className.equalsIgnoreCase(name);
                    final boolean suffixMatch = className.toLowerCase().endsWith("module") &&
                            className.substring(0, className.length() - 6).equalsIgnoreCase(name);
                    return exactMatch || suffixMatch;
                })
                .map(module -> (T) module)
                .findFirst();
    }
}