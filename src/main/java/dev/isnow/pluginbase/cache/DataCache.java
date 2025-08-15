package dev.isnow.pluginbase.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import dev.isnow.pluginbase.data.BaseData;

import java.util.concurrent.TimeUnit;

public class DataCache<K, V extends BaseData> {
    private final LoadingCache<K, V> cache;
    private final String hqlTemplate;

    public DataCache(final Class<V> clazz, final String hqlTemplate) {
        this.hqlTemplate = hqlTemplate;
        cache = Caffeine.newBuilder()
                .removalListener((final K key, final V value, final RemovalCause cause) -> {
                    if (cause == RemovalCause.EXPIRED && value != null) {
                        value.save();
                    }
                })
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build(key -> BaseData.findBy(clazz, hqlTemplate, "key", key));
    }

    public V get(final K key) {
        return cache.get(key);
    }

    public void saveAll() {
        cache.asMap().values().forEach(BaseData::save);
    }
}
