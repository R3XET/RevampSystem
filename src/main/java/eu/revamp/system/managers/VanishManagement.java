package eu.revamp.system.managers;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.Manager;
import eu.revamp.system.api.player.PlayerData;
import org.bukkit.entity.Player;

public class VanishManagement extends Manager {

    public VanishManagement(RevampSystem plugin) {
        super(plugin);
    }

    public int getVanishPriority(Player player) {
        for (int i = 50; i > 0; i--) {
            if (player.hasPermission("revampsystem.vanish.priority." + i)) return i;
        }
        return 1;
    }

    public void vanishPlayerFor(Player player, Player target) {
        if (this.getVanishPriority(target) >= this.getVanishPriority(player)) return;

        target.hidePlayer(player);
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        playerData.setVanished(true);
    }

    public void vanishPlayer(Player player) {
        PlayerUtils.getOnlinePlayers().forEach(online -> {
            if (this.getVanishPriority(online) >= this.getVanishPriority(player)) return;

            online.hidePlayer(player);
        });
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        playerData.setVanished(true);
    }

    public void unvanishPlayer(Player player) {
        PlayerUtils.getOnlinePlayers().forEach(online -> {
            if (!online.canSee(player)) {
                online.showPlayer(player);
            }
        });
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        playerData.setVanished(false);
    }
}
