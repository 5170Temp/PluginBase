package dev.isnow.pluginbase.module.impl.example.data;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.cache.DataCache;
import dev.isnow.pluginbase.data.BaseData;
import dev.isnow.pluginbase.data.impl.PlayerData;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import dev.isnow.pluginbase.util.serializer.database.BaseLocationSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Entity
@Table(name = "homes")
@Getter
@Setter
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HomeData extends BaseData {
    private static final DataCache<UUID, HomeData> CACHE = new DataCache<>(
            HomeData.class,
            "FROM HomeData hd WHERE hd.playerData.uuid = :key"
    );

    @MapKeyColumn(name = "home_name")
    @Column(name = "homes")
    @Convert(converter = BaseLocationSerializer.class)
    private HashMap<String, BaseLocation> homes;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerData playerData;

    public HomeData(final PlayerData playerData) {
        this.playerData = playerData;
    }

    public HomeData() {}

    public void addHome(final String name, final BaseLocation baseLocation) {
        homes.put(name, baseLocation);
    }

    public void removeHome(final String name) {
        homes.remove(name);
    }

    public static CompletableFuture<HomeData> findByUuidAsync(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findByUuid(uuid), PluginBase.getInstance().getThreadPool());
    }

    public static HomeData findByUuid(final UUID uuid) {
        return CACHE.get(uuid);
    }

    public static void saveAllCached() {
        CACHE.saveAll();
    }
}
