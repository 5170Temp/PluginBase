package dev.isnow.pluginbase.util.serializer.database;

import dev.isnow.pluginbase.util.cuboid.BaseLocation;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bukkit.Bukkit;
import org.bukkit.World;

@Converter
public final class RekusLocationSerializer implements AttributeConverter<BaseLocation, String> {

    @Override
    public String convertToDatabaseColumn(final BaseLocation baseLocation) {
        if(baseLocation == null) {
            return "";
        }

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(baseLocation.getWorld()).append(";")
                .append(baseLocation.getX()).append(";")
                .append(baseLocation.getY()).append(";")
                .append(baseLocation.getZ()).append(";")
                .append(baseLocation.getPitch()).append(";")
                .append(baseLocation.getYaw()).append(";");

        return stringBuilder.toString();
    }

    @Override
    public BaseLocation convertToEntityAttribute(final String s) {
        if(s.isEmpty()) {
            return null;
        }

        final String[] split = s.split(";");
        final World world = Bukkit.getWorld(split[0]);
        final double x = Double.parseDouble(split[1]);
        final double y = Double.parseDouble(split[2]);
        final double z = Double.parseDouble(split[3]);
        final float pitch = Float.parseFloat(split[4]);
        final float yaw = Float.parseFloat(split[5]);

        return new BaseLocation(world != null ? world : Bukkit.getWorlds().getFirst(), x, y, z, pitch, yaw);
    }


}