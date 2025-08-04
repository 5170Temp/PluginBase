package dev.isnow.pluginbase.util.cuboid;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Data
@ToString
@EqualsAndHashCode
public class BaseLocation {
    @Setter
    public String world;

    public final double x, y, z;
    public final float pitch, yaw;

    public BaseLocation(final World world, final double x, final double y, final double z, final float pitch, final float yaw) {
        if(world == null) {
            this.world = "";
        } else {
            this.world = world.getName();
        }

        this.x = x;
        this.y = y;
        this.z = z;

        this.pitch = pitch;
        this.yaw = yaw;
    }

    public BaseLocation(final World world, final double x, final double y, final double z) {
        this(world, x, y, z, 0, 0);
    }

    public BaseLocation(final Location loc) {
        this(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public static BaseLocation fromBukkitLocation(final Location loc) {
        return new BaseLocation(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    public static BaseLocation fromBukkitLocationTrimmed(final Location loc) {
        return new BaseLocation(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0, 0);
    }
}
