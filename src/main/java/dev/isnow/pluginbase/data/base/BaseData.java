package dev.isnow.pluginbase.data.base;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.database.Database;
import dev.isnow.pluginbase.database.DatabaseManager;
import dev.isnow.pluginbase.util.ExpiringSession;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

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

    public void delete() {
        delete(getDatabase().openSession());
    }

    public void delete(final ExpiringSession expiringSession) {
        getDatabase().executeTransaction((session, transaction) -> session.remove(this), expiringSession);
    }
}
