package eu.revamp.system.tasks;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.api.player.PlayerData;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TipsTask implements Runnable {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    private AtomicInteger size = new AtomicInteger(0);

    @Override
    public void run() {
        List<String> tips = plugin.getCoreConfig().getStringList("tips.messages");

        PlayerUtils.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData != null && playerData.isTipsAlerts()) {
                player.sendMessage(Color.translate(tips.get(size.get()).replace("{0}", "\n")));
            }
        });
        size.getAndIncrement();
        if (size.get() > tips.size() - 1) {
            size.set(0);
        }
    }
}
