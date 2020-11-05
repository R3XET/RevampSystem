package eu.revamp.system.tasks;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreezeTask implements Runnable {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Override
    public void run() {
        PlayerUtils.getOnlinePlayers().stream()
                .map(Player::getUniqueId).map(plugin.getPlayerManagement()::getPlayerData)
                .filter(PlayerData::isFrozen).map(PlayerData::getUniqueId)
                .map(Bukkit::getPlayer).forEach(player -> plugin.getCoreConfig().getStringList("FREEZE_MESSAGE").forEach(player::sendMessage));
        PlayerUtils.getOnlinePlayers().stream()
                .map(Player::getUniqueId).map(plugin.getPlayerManagement()::getPlayerData)
                .filter(playerData -> playerData.getPanicSystem().isInPanic()).map(PlayerData::getUniqueId)
                .map(Bukkit::getPlayer).forEach(player -> plugin.getCoreConfig().getStringList("PANIC_MESSAGE").forEach(player::sendMessage));
    }
}
