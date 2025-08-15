package dev.isnow.pluginbase.data.impl;

import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.cache.DataCache;
import dev.isnow.pluginbase.data.BaseData;
import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.cache.DataCache;
import dev.isnow.pluginbase.data.BaseData;
import dev.isnow.pluginbase.database.impl.result.MultipleEntityResult;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Entity
@Table(name = "players")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
public class PlayerData extends BaseData {
    private static final DataCache<UUID, PlayerData> CACHE = new DataCache<>(PlayerData.class, "FROM PlayerData WHERE uuid = :key");


    @Column(unique = true, name = "playeruuid", nullable = false)
    private UUID uuid;

    @Column(name = "player_name", nullable = false)
    private String name;

    public PlayerData(final Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public PlayerData(final UUID uuid, final String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public static PlayerData findByUuid(final UUID uuid) {
        return CACHE.get(uuid);
    }

    public static CompletableFuture<PlayerData> findByUuidAsync(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findByUuid(uuid), PluginBase.getInstance().getThreadPool());
    }

    public static CompletableFuture<PlayerData> findByOfflinePlayerAsync(final OfflinePlayer player) {
        return findByUuidAsync(player.getUniqueId());
    }

    public static MultipleEntityResult<PlayerData> findByNameLike(final String name, final int maxAmount) {
        return getDatabase().fetchAllEntities("FROM PlayerData WHERE name LIKE CONCAT('%', :player_name, '%')", "player_name", name, PlayerData.class, maxAmount);
    }

    public static void saveAllCached() {
        CACHE.saveAll();
    }
}
