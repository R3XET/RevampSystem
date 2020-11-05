package eu.revamp.system.tasks;

import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.plugin.RevampSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUIFreezeTask implements Runnable {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Override
    public void run() {
        PlayerUtils.getOnlinePlayers().stream()
                .map(Player::getUniqueId).map(plugin.getPlayerManagement()::getPlayerData)
                .filter(PlayerData::isGuiFrozen).map(PlayerData::getUniqueId)
                .map(Bukkit::getPlayer).forEach(player -> {
            player.closeInventory();
            player.openInventory(this.freezeGUI());
        });
    }


    private Inventory freezeGUI() {
        Inventory inventory = Bukkit.createInventory(null, 45, "Frozen");

        inventory.setItem(40, new ItemBuilder(Material.BOOK)
                .setName("&4&lFrozen")
                .addLoreLine("&cYou've been frozen by a staff member.")
                .addLoreLine("&cPlease don't leave server otherwise")
                .addLoreLine("&cyou will be banned.")
                .addLoreLine(" ")
                .addLoreLine("&cPress ALT + TAB to decrease Minecraft. ")
                .addLoreLine(" ")
                .addLoreLine("&cJoin our teamspeak server: &4" + plugin.getEssentialsManagement().getTeamspeak())
                .addLoreLine(" ").toItemStack());

        return inventory;
    }
}
