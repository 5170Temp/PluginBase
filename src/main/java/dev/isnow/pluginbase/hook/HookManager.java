package dev.isnow.pluginbase.hook;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.util.BaseLogger;
import io.github.mqzen.menus.Lotus;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// i know this class is horrible but i couldnt care less
@Getter
public class HookManager {
    private Lotus menuAPI;
    private Economy economy;

    private boolean placeholerAPIHook;
    private boolean kingdomsHook;
    private boolean vehiclesHook, vehiclesWASDHook;
    private boolean hologramHook;
    private boolean vaultHook;
    private boolean packetEventsHook;

    public void onLoad(final PluginBase plugin) {
        packetEventsHook = Bukkit.getPluginManager().getPlugin("packetevents") != null;
        if (packetEventsHook) {
            BaseLogger.info("Hooking into PacketEvents");

            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));

            PacketEvents.getAPI().load();
            //PacketEvents.getAPI().getEventManager().registerListener(packetListener);
        }
    }

    public void onEnable(final PluginBase plugin) {
        placeholerAPIHook = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholerAPIHook) {
            BaseLogger.info("Hooking into PlaceholderAPI");
        }

        kingdomsHook = Bukkit.getPluginManager().getPlugin("Kingdoms") != null;
        if (kingdomsHook) {
            BaseLogger.info("Hooking into KingdomsX");
        }

        vehiclesHook = Bukkit.getPluginManager().getPlugin("Vehicles") != null;
        if (vehiclesHook) {
            BaseLogger.info("Hooking into Vehicles");
        }

        vehiclesWASDHook = Bukkit.getPluginManager().getPlugin("VehiclesWASD") != null;
        if (vehiclesWASDHook) {
            BaseLogger.info("Hooking into VehiclesWASD");
        }

        hologramHook = Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;
        if (hologramHook) {
            BaseLogger.info("Hooking into DecentHolograms");
        }

        vaultHook = Bukkit.getPluginManager().getPlugin("Vault") != null;
        if (vaultHook) {
            BaseLogger.info("Hooking into Vault");
            economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
        }

        menuAPI = Lotus.load(plugin);
    }

    public void onDisable() {
        if (packetEventsHook) {
            //PacketEvents.getAPI().getEventManager().unregisterListener(packetListener);
        }
    }
}