package dev.rob2.simplebackplus;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class BackCommand implements CommandExecutor {

    private final SimpleBackPlus plugin;

    public BackCommand(SimpleBackPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /back.");
            return true;
        }

        if (!player.hasPermission("simplebackplus.use")) {
            player.sendMessage("You don't have permission to use /back.");
            return true;
        }

        UUID uuid = player.getUniqueId();

        // --- OPTIONAL COMBATLOGGER INTEGRATION ---
        if (plugin.getServer().getPluginManager().getPlugin("CombatLogger") != null) {
            try {
                Class<?> apiClass = Class.forName("dev.rob2.combatlogger.CombatAPI");

                boolean inCombat = (boolean) apiClass
                        .getMethod("isInCombat", UUID.class)
                        .invoke(null, uuid);

                if (inCombat) {
                    long remaining = (long) apiClass
                            .getMethod("getRemainingSeconds", UUID.class)
                            .invoke(null, uuid);

                    player.sendMessage("§cYou cannot use /back while in combat. §e" + remaining + "s §cremaining.");
                    return true;
                }

            } catch (Exception ignored) {
                // CombatLogger not available or API changed — fail silently
            }
        }

        // --- DEATH BLOCK ---
        if (plugin.isDeathBlocked(uuid)) {
            long remaining = plugin.getDeathRemaining(uuid);
            player.sendMessage("§cYou cannot use /back right after dying. Wait §e" + remaining + "s§c.");
            return true;
        }

        // --- COOLDOWN CHECK ---
        Map<UUID, Long> cooldowns = plugin.getBackCooldowns();
        int cooldownSeconds = plugin.getCooldownSeconds();
        long now = System.currentTimeMillis();

        if (!player.hasPermission("simplebackplus.bypasscooldown")) {
            if (cooldowns.containsKey(uuid)) {
                long lastUsed = cooldowns.get(uuid);
                long remaining = (lastUsed + (cooldownSeconds * 1000L)) - now;

                if (remaining > 0) {
                    long seconds = remaining / 1000;
                    player.sendMessage("§cYou must wait §e" + seconds + "s §cbefore using /back again.");
                    return true;
                }
            }
        }

        // --- HISTORY CHECK ---
        if (!plugin.hasHistory(uuid)) {
            player.sendMessage("No previous locations saved.");
            return true;
        }

        Location target = plugin.popLocation(uuid);

        if (target == null || target.getWorld() == null) {
            player.sendMessage("Your previous location is no longer available.");
            return true;
        }

        // --- TELEPORT ---
        player.teleport(target);
        player.sendMessage("Teleported to your previous location.");

        // --- RECORD COOLDOWN ---
        cooldowns.put(uuid, now);

        return true;
    }
}

