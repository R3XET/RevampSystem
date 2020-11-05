package eu.revamp.system.tasks;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.menu.menu.AquaMenu;

public class MenuTask implements Runnable {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Override
    public void run() {
        PlayerUtils.getOnlinePlayers().forEach(player -> {
            AquaMenu aquaMenu = plugin.getMenuManager().getOpenedMenus().get(player.getUniqueId());
            if (aquaMenu != null && aquaMenu.isUpdateInTask()) {
                aquaMenu.update(player);
            }
        });
    }
}
