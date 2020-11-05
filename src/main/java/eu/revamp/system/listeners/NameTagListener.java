package eu.revamp.system.listeners;

import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NameTagListener implements Listener {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {
        if (!plugin.getCoreConfig().getBoolean("use-nametags")) return;
        if (!plugin.isRevampHCFEnabled() && plugin.getServer().getPluginManager().getPlugin("RevampUHC") == null && plugin.getServer().getPluginManager().getPlugin("RevampUHCGames") == null && plugin.getServer().getPluginManager().getPlugin("RevampHub") == null && plugin.getServer().getPluginManager().getPlugin("RevampPractice") == null) {
            Player player = event.getPlayer();
            Tasks.runLater(plugin, () -> plugin.getNameTagManagement().createScoreboard(player), 60L);
        }
    }
}
