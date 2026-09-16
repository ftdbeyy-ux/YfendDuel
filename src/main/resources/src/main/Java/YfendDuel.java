package com.yfend.yfendduel;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class YfendDuel extends JavaPlugin {

    private static YfendDuel instance;
    private final Map<String, Location[]> arenas = new HashMap<>();
    private final Map<UUID, DuelRequest> activeRequests = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        if (getCommand("duel") != null) {
            getCommand("duel").setExecutor(new DuelCommand());
        }
        getServer().getPluginManager().registerEvents(new DuelListener(), this);
        getLogger().info("YfendDuel basariyla yuklendi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("YfendDuel kapatildi.");
    }

    public static YfendDuel getInstance() {
        return instance;
    }

    public Map<String, Location[]> getArenas() {
        return arenas;
    }

    public Map<UUID, DuelRequest> getActiveRequests() {
        return activeRequests;
    }

    public static class DuelRequest {
        public UUID challenger;
        public UUID target;
        public String arenaName;
        public boolean keepInventory = true;
        public boolean allowBlockPlace = true;
        public boolean allowWebPlace = true;

        public DuelRequest(UUID challenger, UUID target) {
            this.challenger = challenger;
            this.target = target;
        }
    }
}
