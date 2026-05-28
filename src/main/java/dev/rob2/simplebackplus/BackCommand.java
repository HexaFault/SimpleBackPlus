package dev.rob2.simplebackplus;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (!plugin.hasHistory(player.getUniqueId())) {
            player.sendMessage("No previous locations saved.");
            return true;
        }

        Location target = plugin.popLocation(player.getUniqueId());

        if (target == null || target.getWorld() == null) {
            player.sendMessage("Your previous location is no longer available.");
            return true;
        }

        player.teleport(target);
        player.sendMessage("Teleported to your previous location.");
        return true;
    }
}

