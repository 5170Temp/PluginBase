package dev.isnow.pluginbase.util.serializer.config;

import de.exlll.configlib.Serializer;
import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class RekusLocationSerializer implements Serializer<BaseLocation, Map<String, String>> {
    @Override
    public Map<String, String> serialize(final BaseLocation baseLocation) {
        final Map<String, String> map = new HashMap<>();

        map.put("world", baseLocation.getWorld());

        map.put("x", String.valueOf(baseLocation.getX()));
        map.put("y", String.valueOf(baseLocation.getY()));
        map.put("z", String.valueOf(baseLocation.getZ()));

        map.put("pitch", String.valueOf(baseLocation.getPitch()));
        map.put("yaw", String.valueOf(baseLocation.getYaw()));

        return map;
    }

    @Override
    public BaseLocation deserialize(final Map<String, String> stringMap) {
        final World world = Bukkit.getWorld(stringMap.get("world"));

        final double x = Double.parseDouble(stringMap.get("x"));
        final double y = Double.parseDouble(stringMap.get("y"));
        final double z = Double.parseDouble(stringMap.get("z"));

        final float pitch = Float.parseFloat(stringMap.get("pitch"));
        final float yaw = Float.parseFloat(stringMap.get("yaw"));

        return new BaseLocation(world, x, y, z, pitch, yaw);
    }
}