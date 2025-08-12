package dev.isnow.pluginbase.module.impl.example.data;

import dev.isnow.pluginbase.data.PlayerData;
import dev.isnow.pluginbase.data.base.BaseData;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import dev.isnow.pluginbase.util.serializer.database.BaseLocationSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "homes")
@Getter
@Setter
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HomeData extends BaseData {
    @Column(name = "name")
    private String name;

    @Column(name = "location")
    @Convert(converter = BaseLocationSerializer.class)
    private BaseLocation location;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerData playerData;

    public HomeData(final String name, final BaseLocation location, final PlayerData playerData) {
        this.name = name;
        this.location = location;

        this.playerData = playerData;
    }

    public HomeData() {}
}
