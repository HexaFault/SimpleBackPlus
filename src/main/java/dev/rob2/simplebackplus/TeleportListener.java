package dev.rob2.simplebackplus;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    private final SimpleBackPlus plugin;

    public TeleportListener(SimpleBackPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        plugin.pushLocation(player.getUniqueId(), event.getFrom());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // REQUIRED for death-block
        plugin.recordDeath(player.getUniqueId());

        if (!player.hasPermission("simplebackplus.death")) return;

        plugin.pushLocation(player.getUniqueId(), player.getLocation());
    }
}

