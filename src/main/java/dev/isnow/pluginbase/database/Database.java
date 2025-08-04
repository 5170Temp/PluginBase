package dev.isnow.pluginbase.database;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.impl.GeneralConfig;
import dev.isnow.pluginbase.config.impl.database.DatabaseConfig;
import dev.isnow.pluginbase.config.impl.database.DatabaseTypeConfig;
import dev.isnow.pluginbase.util.BaseLogger;
import dev.isnow.pluginbase.util.ExpiringSession;
import dev.isnow.pluginbase.util.ReflectionUtil;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.stat.Statistics;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Data
public class Database {
    private final PluginBase plugin;
    private final SessionFactory sessionFactory;

    public Database(final PluginBase plugin, final GeneralConfig mainConfig, final DatabaseConfig authConfig, final Collection<Class<?>> moduleEntities) {
        this.plugin = plugin;
        this.sessionFactory = initializeSessionFactory(mainConfig, authConfig, moduleEntities);
    }

    private SessionFactory initializeSessionFactory(final GeneralConfig mainConfig, final DatabaseConfig authConfig, final Collection<Class<?>> moduleEntities) {
        try {
            return configureHibernate(mainConfig, authConfig, moduleEntities);
        } catch (Exception e) {
            BaseLogger.error("Failed to initialize Hibernate session factory: " + e.getMessage());
            return null;
        }
    }

    private SessionFactory configureHibernate(final GeneralConfig mainConfig, final DatabaseConfig authConfig, final Collection<Class<?>> moduleEntities) {
        final Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.dialect", getHibernateDialect(authConfig.getDatabaseType()));
        configuration.setProperty("hibernate.connection.driver_class", getDriverClass(authConfig.getDatabaseType()));
        configuration.setProperty("hibernate.connection.url", getUrl(authConfig));
        configuration.setProperty("hibernate.connection.username", authConfig.getUsername());
        configuration.setProperty("hibernate.connection.password", authConfig.getPassword());

        configuration.setProperty("hibernate.cache.use_second_level_cache", "true");
        configuration.setProperty("hibernate.cache.use_query_cache", "true");
        configuration.setProperty("hibernate.cache.region.factory_class", "jcache");
        configuration.setProperty("hibernate.javax.cache.provider", "com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider");

        if (mainConfig.isDebugMode()) {
            enableHibernateDebug(configuration);
        }

        configuration.setProperty("hibernate.hbm2ddl.auto", "update");

        try {
            for(final Class<?> clazz : ReflectionUtil.getClasses(PluginBase.class.getPackage().getName() + ".data")) {
                configuration.addAnnotatedClass(clazz);
            }
        } catch (final Exception e) {
            BaseLogger.error("Failed to load data classes: " + e.getMessage());
        }

        BaseLogger.debug("Registering " + moduleEntities.size() + " database entities from modules...");
        for (Class<?> entityClass : moduleEntities) {
            configuration.addAnnotatedClass(entityClass);
            BaseLogger.debug("Registered " + entityClass.getName());
        }

        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        Thread.currentThread().setContextClassLoader(PluginBase.class.getClassLoader());
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private void enableHibernateDebug(Configuration configuration) {
        BaseLogger.debug("Enabling Hibernate debug mode.");

        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.use_sql_comments", "true");
        configuration.setProperty("hibernate.generate_statistics", "true");
    }

    private String getUrl(final DatabaseConfig authConfig) {
        return switch (authConfig.getDatabaseType()) {
            case MYSQL, MARIADB -> authConfig.getDatabaseType().getPrefix() + authConfig.getHost() + "/" + authConfig.getDatabase();
            case H2 -> authConfig.getDatabaseType().getPrefix() + new File(plugin.getDataFolder(), authConfig.getDatabase()).getAbsolutePath();
        };
    }

    private String getHibernateDialect(final DatabaseTypeConfig type) {
        return switch (type) {
            case MYSQL -> "org.hibernate.dialect.MySQLDialect";
            case MARIADB -> "org.hibernate.dialect.MariaDBDialect";
            case H2 -> "org.hibernate.dialect.H2Dialect";
        };
    }

    private String getDriverClass(final DatabaseTypeConfig type) {
        return switch (type) {
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case MARIADB -> "org.mariadb.jdbc.Driver";
            case H2 -> "org.h2.Driver";
        };
    }

    public void executeTransaction(final BiConsumer<Session, Transaction> action, final ExpiringSession expiringSession) {
        if (expiringSession.isOpen()) {
            Transaction tx = null;
            try {
                tx = expiringSession.getSession().beginTransaction();
                action.accept(expiringSession.getSession(), tx);
                tx.commit();
            } catch (final Exception e) {
                if (tx != null) tx.rollback();
                BaseLogger.error("Transaction failed: " + e.getMessage());
            } finally {
                expiringSession.closeSession();
            }
        }
    }

    public void shutdown() {
        BaseLogger.info("Shutting down the database connection.");

        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public boolean isConnected() {
        return sessionFactory != null && sessionFactory.isOpen();
    }

    public ExpiringSession openSession() {
        return new ExpiringSession(plugin, sessionFactory.openSession());
    }

    public Statistics getStatistics() {
        return sessionFactory.getStatistics();
    }

    public <T> void fetchEntityAsync(final String hql, final Map<String, Object> params, final Class<T> clazz, final BiConsumer<ExpiringSession, T> callback) {
        CompletableFuture.runAsync(() -> {
            try (ExpiringSession session = openSession()) {
                final Query<T> query = session.getSession().createQuery(hql, clazz);
                params.forEach(query::setParameter);

                T entity = query.setCacheable(true).uniqueResult();
                callback.accept(session, entity);
            } catch (final Exception e) {
                BaseLogger.error("Failed to fetch entity: " + e.getMessage());
                callback.accept(null, null);
            }
        }, plugin.getThreadPool());
    }

    public <T> void fetchEntityAsync(final String hql, final String paramName, final Object paramValue, final Class<T> clazz, final BiConsumer<ExpiringSession, T> callback) {
        fetchEntityAsync(hql, Map.of(paramName, paramValue), clazz, callback);
    }

    public <T> void fetchAllAsync(final String hql, final Class<T> clazz, final BiConsumer<ExpiringSession, List<T>> callback) {
        CompletableFuture.runAsync(() -> {
            try (ExpiringSession session = openSession()) {
                final List<T> entities = session.getSession().createQuery(hql, clazz).setCacheable(true).list();

                callback.accept(session, entities);
            } catch (Exception e) {
                BaseLogger.error("Failed to fetch entities: " + e.getMessage());
                callback.accept(null, null);
            }
        }, plugin.getThreadPool());
    }

}
