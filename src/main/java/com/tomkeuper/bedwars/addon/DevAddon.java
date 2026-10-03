package com.tomkeuper.bedwars.addon;

import com.tomkeuper.bedwars.addon.command.BedWarsDevSubCommand;
import com.tomkeuper.bedwars.addon.command.DevCommandExecutor;
import com.tomkeuper.bedwars.addon.integrations.BedWars2023;
import com.tomkeuper.bedwars.addon.integrations.IIntegration;
import com.tomkeuper.bedwars.addon.timer.TimerManager;
import com.tomkeuper.bedwars.api.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class DevAddon extends JavaPlugin {
    public static BedWars bedWars;
    public static DevAddon plugin;

    private TimerManager timerManager;
    private DevCommandExecutor devCommandExecutor;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        timerManager = new TimerManager(this);
        populateIntegrations(new BedWars2023(this, bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider()));

        devCommandExecutor = new DevCommandExecutor(this, bedWars);

        PluginCommand bwdevCmd = getCommand("bwdev");
        if (bwdevCmd != null) {
            bwdevCmd.setExecutor(devCommandExecutor);
            bwdevCmd.setTabCompleter(devCommandExecutor);
        }

        if (bedWars != null && bedWars.getBedWarsCommand() != null) {
            new BedWarsDevSubCommand(bedWars.getBedWarsCommand(), "dev", devCommandExecutor);
        }

        getLogger().info("BedWars2023-DevAddon loaded successfully!");
    }

    private void populateIntegrations(IIntegration... integrations) {
        for (IIntegration integration : integrations) {
            if (!integration.enable()) {
                throw new RuntimeException("Plugin could not be enabled as one or more of the dependencies could not be hooked.");
            }
        }
    }

    public boolean isBedWarsInstalled() {
        return Bukkit.getPluginManager().getPlugin("BedWars2023") != null;
    }

    public static void registerEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(l -> plugin.getServer().getPluginManager().registerEvents(l, plugin));
    }

    public static BedWars getBedWars() {
        return bedWars;
    }

    public static DevAddon getInstance() {
        return plugin;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public DevCommandExecutor getDevCommandExecutor() {
        return devCommandExecutor;
    }

    public static void debug(String msg) {
        plugin.getLogger().info("[DEBUG] - " + msg);
    }
}
