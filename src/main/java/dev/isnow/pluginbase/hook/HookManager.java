package dev.isnow.pluginbase.hook;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.util.logger.BaseLogger;
import io.github.mqzen.menus.Lotus;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.ServerBuildInfo;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// i know this class is horrible but i couldnt care less
@Getter
public class HookManager {

    private PacketListenerCommon packetListener;
    private Lotus menuAPI;
    private Economy economy;

    private boolean placeholerAPIHook;
    private boolean kingdomsHook;
    private boolean vehiclesHook, vehiclesWASDHook;
    private boolean hologramHook;
    private boolean vaultHook;
    private boolean isUniverseSpigot;

    public void onLoad() {
        //packetListener = new RekusPacketListener().asAbstract(PacketListenerPriority.NORMAL);

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(PluginBase.getInstance()));

        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(packetListener);
    }

    public void onEnable() {
        Bukkit.broadcastMessage(ServerBuildInfo.buildInfo().brandName());

        isUniverseSpigot = ServerBuildInfo.buildInfo().brandName().contains("UniverseSpigot");

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

        menuAPI = Lotus.load(PluginBase.getInstance());
    }

    public void onDisable() {
        PacketEvents.getAPI().getEventManager().unregisterListener(packetListener);
    }

    public void addMoney(final Player player, final int amount) {
        economy.depositPlayer(player, amount);
    }
}
