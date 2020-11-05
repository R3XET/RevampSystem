package eu.revamp.system.tasks;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.api.player.PlayerData;

public class InventoryUpdateTask implements Runnable {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Override
    public void run() {
        PlayerUtils.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData != null && playerData.isFullJoined()) {
                playerData.getOfflineInventory().update(player);
                playerData.getOfflineInventory().save(player);
            }
        });
    }
}
