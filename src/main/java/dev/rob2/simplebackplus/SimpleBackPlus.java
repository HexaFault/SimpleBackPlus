package dev.rob2.simplebackplus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SimpleBackPlus extends JavaPlugin {

    private final Map<UUID, Deque<Location>> history = new HashMap<>();
    private final Map<UUID, Long> backCooldowns = new HashMap<>();
    private final Map<UUID, Long> deathTimestamps = new HashMap<>();

    private int cooldownSeconds;
    private int deathBlockSeconds;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        loadSettings();

        // Register command
        getCommand("back").setExecutor(new BackCommand(this));

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new TeleportListener(this), this);

        getLogger().info("SimpleBackPlus enabled.");
    }

    private void loadSettings() {
        FileConfiguration config = getConfig();

        cooldownSeconds = config.getInt("cooldown-seconds", 10);
        deathBlockSeconds = config.getInt("death-block-seconds", 5);
    }

    // --- HISTORY MANAGEMENT ---

    public void pushLocation(UUID uuid, Location loc) {
        history.computeIfAbsent(uuid, k -> new ArrayDeque<>()).push(loc);
    }

    public Location popLocation(UUID uuid) {
        Deque<Location> stack = history.get(uuid);
        if (stack == null || stack.isEmpty()) return null;
        return stack.pop();
    }

    public boolean hasHistory(UUID uuid) {
        return history.containsKey(uuid) && !history.get(uuid).isEmpty();
    }

    // --- COOLDOWN MANAGEMENT ---

    public Map<UUID, Long> getBackCooldowns() {
        return backCooldowns;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    // --- DEATH BLOCK SYSTEM ---

    public void recordDeath(UUID uuid) {
        deathTimestamps.put(uuid, System.currentTimeMillis());
    }

    public boolean isDeathBlocked(UUID uuid) {
        if (!deathTimestamps.containsKey(uuid)) return false;

        long lastDeath = deathTimestamps.get(uuid);
        long now = System.currentTimeMillis();
        long remaining = (lastDeath + (deathBlockSeconds * 1000L)) - now;

        return remaining > 0;
    }

    public long getDeathRemaining(UUID uuid) {
        if (!deathTimestamps.containsKey(uuid)) return 0;

        long lastDeath = deathTimestamps.get(uuid);
        long now = System.currentTimeMillis();
        long remaining = (lastDeath + (deathBlockSeconds * 1000L)) - now;

        return Math.max(remaining / 1000, 0);
    }
}

