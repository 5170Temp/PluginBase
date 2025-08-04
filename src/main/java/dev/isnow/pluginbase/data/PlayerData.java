package dev.isnow.pluginbase.data;

import dev.isnow.pluginbase.data.base.BaseData;
import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.isnow.pluginbase.util.ExpiringSession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@Entity
@Table(name = "players")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter@Setter
public class PlayerData extends BaseData {

    @Column(unique = true, name = "playeruuid", nullable = false)
    private UUID uuid;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true , mappedBy = "playerData")
    @MapKey(name = "name")
    private Map<String, HomeData> homeLocations;

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

    public PlayerData() {}

    public static void findByUuidAsync(final UUID uuid, final BiConsumer<ExpiringSession, PlayerData> callback) {
        getDatabase().fetchEntityAsync("FROM PlayerData WHERE uuid = :uuid", "uuid", uuid, PlayerData.class, callback);
    }

    public static void findByOfflinePlayerAsync(final OfflinePlayer player, final BiConsumer<ExpiringSession, PlayerData> callback) {
        findByUuidAsync(player.getUniqueId(), callback);
    }

    public static void findByNameAsync(final String name, final BiConsumer<ExpiringSession, PlayerData> callback) {
        getDatabase().fetchEntityAsync("FROM PlayerData WHERE name = :player_name", "player_name", name, PlayerData.class, callback);
    }
}
