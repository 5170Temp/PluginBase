package dev.isnow.pluginbase.database.impl;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.impl.GeneralConfig;
import dev.isnow.pluginbase.config.impl.database.DatabaseConfig;
import dev.isnow.pluginbase.config.impl.database.DatabaseTypeConfig;
import dev.isnow.pluginbase.database.impl.result.EntityResult;
import dev.isnow.pluginbase.database.impl.result.MultipleEntityResult;
import dev.isnow.pluginbase.util.ExpiringSession;
import dev.isnow.pluginbase.util.ReflectionUtil;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
            BaseLogger.debug("Initializing session factory with " + authConfig.getDatabaseType().name());
            if (authConfig.getDatabaseType() == DatabaseTypeConfig.MONGODB) {
                throw new NotImplementedException("MongoDB will be supported whenever mongo-hibernate releases");
            } else {
                return configureHibernate(mainConfig, authConfig, moduleEntities);
            }
        } catch (final Exception e) {
            BaseLogger.error("Failed to initialize Hibernate session factory: ", e);
            return null;
        }
    }

//    private SessionFactory configureOgm(final GeneralConfig mainConfig, final DatabaseConfig authConfig, final Collection<Class<?>> moduleEntities) {
//        final Configuration configuration = new OgmConfiguration();
//
//        configuration.setProperty("hibernate.ogm.datastore.provider", "mongodb");
//        configuration.setProperty("hibernate.ogm.datastore.host", authConfig.getHost());
//        configuration.setProperty("hibernate.ogm.datastore.port", String.valueOf(authConfig.getPort()));
//        configuration.setProperty("hibernate.ogm.datastore.database", authConfig.getDatabase());
//        configuration.setProperty("hibernate.ogm.datastore.username", authConfig.getUsername());
//        configuration.setProperty("hibernate.ogm.datastore.password", authConfig.getPassword());
//
//        configuration.setProperty("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform");
//
//        addCommonConfiguration(configuration, mainConfig, moduleEntities);
//
//        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
//
//        Thread.currentThread().setContextClassLoader(PluginBase.class.getClassLoader());
//        return configuration.buildSessionFactory(serviceRegistry);
//    }


    private SessionFactory configureHibernate(final GeneralConfig mainConfig, final DatabaseConfig authConfig, final Collection<Class<?>> moduleEntities) {
        final Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.dialect", getHibernateDialect(authConfig.getDatabaseType()));
        configuration.setProperty("hibernate.connection.driver_class", getDriverClass(authConfig.getDatabaseType()));
        configuration.setProperty("hibernate.connection.url", getUrl(authConfig));
        configuration.setProperty("hibernate.connection.username", authConfig.getUsername());
        configuration.setProperty("hibernate.connection.password", authConfig.getPassword());

        addCommonConfiguration(configuration, mainConfig, moduleEntities);

        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        Thread.currentThread().setContextClassLoader(PluginBase.class.getClassLoader());
        return configuration.buildSessionFactory(serviceRegistry);
    }


    private void addCommonConfiguration(Configuration configuration, final GeneralConfig mainConfig, final Collection<Class<?>> moduleEntities) {
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
            BaseLogger.error("Failed to load data classes: ", e);
        }

        BaseLogger.debug("Registering " + moduleEntities.size() + " database entities from modules...");
        for (Class<?> entityClass : moduleEntities) {
            configuration.addAnnotatedClass(entityClass);
            BaseLogger.debug("Registered " + entityClass.getName());
        }
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
            case MONGODB -> throw new IllegalArgumentException("getUrl should not be called for MongoDB");
        };
    }

    private String getHibernateDialect(final DatabaseTypeConfig type) {
        return switch (type) {
            case MYSQL -> "org.hibernate.dialect.MySQLDialect";
            case MARIADB -> "org.hibernate.dialect.MariaDBDialect";
            case H2 -> "org.hibernate.dialect.H2Dialect";
            case MONGODB -> throw new IllegalArgumentException("getHibernateDialect should not be called for MongoDB");
        };
    }

    private String getDriverClass(final DatabaseTypeConfig type) {
        return switch (type) {
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case MARIADB -> "org.mariadb.jdbc.Driver";
            case H2 -> "org.h2.Driver";
            case MONGODB -> throw new IllegalArgumentException("getDriverClass should not be called for MongoDB");
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
                BaseLogger.error("Transaction failed: ", e);
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

    public <T> EntityResult<T> fetchEntity(final String query, final String param, final Object value, final Class<T> clazz) {
        ExpiringSession session = null;
        try {
            session = openSession();
            T entity = session.getSession().createQuery(query, clazz)
                    .setParameter(param, value)
                    .setCacheable(true)
                    .uniqueResult();
            return new EntityResult<>(session, entity);
        } catch (Exception e) {
            if (session != null) {
                session.closeSession();
            }

            BaseLogger.error("Failed to fetch entity: ", e);
            return null;
        }
    }

    public <T> EntityResult<T> fetchEntity(final String query, final String param, final Object value, final Class<T> clazz, final ExpiringSession session) {
        try {
            T entity = session.getSession().createQuery(query, clazz)
                    .setParameter(param, value)
                    .setCacheable(true)
                    .uniqueResult();
            return new EntityResult<>(session, entity);
        } catch (Exception e) {
            BaseLogger.error("Failed to fetch entity: ", e);
            session.closeSession();

            return null;
        }
    }

    public <T> void fetchEntityAsync(final String hql, final Map<String, Object> params, final Class<T> clazz, final BiConsumer<ExpiringSession, T> callback) {
        CompletableFuture.runAsync(() -> {
            ExpiringSession session = null;
            try {
                session = openSession();
                final Query<T> query = session.getSession().createQuery(hql, clazz);
                params.forEach(query::setParameter);

                final T entity = query.setCacheable(true).uniqueResult();
                callback.accept(session, entity);
            } catch (final Exception e) {
                if (session != null) {
                    session.closeSession();
                }

                BaseLogger.error("Failed to fetch entity: ", e);
                callback.accept(null, null);
            }
        }, plugin.getThreadPool());
    }

    public <T> void fetchEntityAsync(final String hql, final String paramName, final Object paramValue, final Class<T> clazz, final BiConsumer<ExpiringSession, T> callback) {
        fetchEntityAsync(hql, Map.of(paramName, paramValue), clazz, callback);
    }

    public <T> void fetchAllAsync(final String hql, final Map<String, Object> params, final Class<T> clazz, final Consumer<MultipleEntityResult<T>> callback) {
        fetchAllAsync(hql, params, clazz, callback, Integer.MAX_VALUE);
    }

    public <T> void fetchAllAsync(final String hql, final Map<String, Object> params, final Class<T> clazz, final Consumer<MultipleEntityResult<T>> callback, final int maxAmount) {
        CompletableFuture.runAsync(() -> {
            callback.accept(fetchAllEntities(hql, params, clazz, maxAmount));
        }, plugin.getThreadPool());
    }

    public <T> MultipleEntityResult<T> fetchAllEntities(final String hql, final Map<String, Object> params, final Class<T> clazz, final int maxAmount) {
        ExpiringSession session = null;
        try {
            session = openSession();
            final Query<T> query = session.getSession().createQuery(hql, clazz);
            params.forEach(query::setParameter);

            return new MultipleEntityResult<>(session, query.setMaxResults(maxAmount).setCacheable(true).list());
        } catch (final Exception e) {
            if (session != null) {
                session.closeSession();
            }

            BaseLogger.error("Failed to fetch entities: ", e);
            return null;
        }
    }

    public <T> MultipleEntityResult<T> fetchAllEntities(final String hql, final String paramName, final Object paramValue, final Class<T> clazz) {
        return fetchAllEntities(hql, Map.of(paramName, paramValue), clazz, Integer.MAX_VALUE);
    }

    public <T> MultipleEntityResult<T> fetchAllEntities(final String hql, final String paramName, final Object paramValue, final Class<T> clazz, final int maxAmount) {
        return fetchAllEntities(hql, Map.of(paramName, paramValue), clazz, maxAmount);
    }

}
