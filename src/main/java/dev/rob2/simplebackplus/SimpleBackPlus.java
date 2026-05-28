package dev.rob2.simplebackplus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SimpleBackPlus extends JavaPlugin {

    private static SimpleBackPlus instance;

    private final Map<UUID, Deque<Location>> history = new HashMap<>();
    private static final int MAX_HISTORY = 10;

    private File historyFile;
    private YamlConfiguration historyConfig;

    @Override
    public void onEnable() {
        instance = this;

        createHistoryFile();
        loadHistory();

        Bukkit.getPluginManager().registerEvents(new TeleportListener(this), this);
        getCommand("back").setExecutor(new BackCommand(this));

        getLogger().info("SimpleBackPlus enabled with persistent history.");
    }

    public static SimpleBackPlus getInstance() {
        return instance;
    }

    private void createHistoryFile() {
        historyFile = new File(getDataFolder(), "history.yml");
        if (!historyFile.exists()) {
            historyFile.getParentFile().mkdirs();
            saveResource("history.yml", false);
        }
        historyConfig = YamlConfiguration.loadConfiguration(historyFile);
    }

    private void loadHistory() {
        for (String uuidString : historyConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            List<Map<?, ?>> list = historyConfig.getMapList(uuidString);

            Deque<Location> stack = new ArrayDeque<>();

            for (Map<?, ?> map : list) {
                String world = (String) map.get("world");
                double x = (double) map.get("x");
                double y = (double) map.get("y");
                double z = (double) map.get("z");

                Location loc = new Location(
                        Bukkit.getWorld(world), x, y, z
                );

                stack.push(loc);
            }

            history.put(uuid, stack);
        }
    }

    public void saveHistory() {
        for (UUID uuid : history.keySet()) {
            Deque<Location> stack = history.get(uuid);
            List<Map<String, Object>> list = new ArrayList<>();

            for (Location loc : stack) {
                Map<String, Object> map = new HashMap<>();
                map.put("world", loc.getWorld().getName());
                map.put("x", loc.getX());
                map.put("y", loc.getY());
                map.put("z", loc.getZ());
                list.add(map);
            }

            historyConfig.set(uuid.toString(), list);
        }

        try {
            historyConfig.save(historyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pushLocation(UUID uuid, Location loc) {
        if (loc == null || loc.getWorld() == null) return;

        Deque<Location> stack = history.computeIfAbsent(uuid, k -> new ArrayDeque<>());

        if (stack.size() >= MAX_HISTORY) {
            stack.removeLast();
        }

        stack.push(loc.clone());
        saveHistory();
    }

    public Location popLocation(UUID uuid) {
        Deque<Location> stack = history.get(uuid);
        if (stack == null || stack.isEmpty()) return null;

        Location loc = stack.pop();
        saveHistory();
        return loc;
    }

    public boolean hasHistory(UUID uuid) {
        return history.containsKey(uuid) && !history.get(uuid).isEmpty();
    }
}

