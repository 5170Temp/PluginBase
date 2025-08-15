package dev.isnow.pluginbase.data;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.database.impl.Database;
import dev.isnow.pluginbase.database.impl.result.EntityResult;
import dev.isnow.pluginbase.util.ExpiringSession;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@MappedSuperclass
@Getter
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class BaseData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    protected static Database getDatabase() {
        return PluginBase.getInstance().getDatabaseManager().getDatabase();
    }

    public void save() {
        save(getDatabase().openSession());
    }

    public void save(final ExpiringSession expiringSession) {
        getDatabase().executeTransaction((session, transaction) -> session.merge(this), expiringSession);
    }

    public void saveAsync() {
        CompletableFuture.runAsync(() -> {
            save(getDatabase().openSession());
        }, getDatabase().getPlugin().getThreadPool());
    }

    public void saveAsync(final ExpiringSession expiringSession) {
        CompletableFuture.runAsync(() -> {
            save(expiringSession);
        }, getDatabase().getPlugin().getThreadPool());
    }

    public void delete() {
        delete(getDatabase().openSession());
    }

    public void delete(final ExpiringSession expiringSession) {
        getDatabase().executeTransaction((session, transaction) -> session.remove(this), expiringSession);
    }

    public static <T extends BaseData> T findBy(final Class<T> clazz, final String hql, final String paramName, final Object value) {
        BaseLogger.debug("Executing HQL: " + hql + " with param: " + paramName + "=" + value);
        EntityResult<T> result = getDatabase().fetchEntity(hql, paramName, value, clazz);
        return result != null ? result.getEntity() : null;
    }

    public static <T extends BaseData> T findBy(final Class<T> clazz, final String hql, final Map<String, Object> params) {
        BaseLogger.debug("Executing HQL: " + hql + " with params: " + params);
        ExpiringSession session = null;
        try {
            session = getDatabase().openSession();
            var query = session.getSession().createQuery(hql, clazz).setCacheable(true);
            for (var entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.uniqueResult();
        } catch (Exception e) {
            BaseLogger.error("Failed to fetch entity: ", e);
            if (session != null) session.closeSession();
            return null;
        }
    }
}
