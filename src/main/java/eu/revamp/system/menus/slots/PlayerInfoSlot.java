package eu.revamp.system.menus.slots;

import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.system.plugin.RevampSystem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PlayerInfoSlot extends Slot {
    private final PlayerData playerData;
    private final int slot;

    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
        item.setDurability((short) 3);
        item.setSkullOwner(playerData.getPlayerName());
        plugin.getCoreConfig().getStringList("player-info-gui-format").forEach(message -> {
            Replacement replacement = new Replacement(message);
            replacement.add("%name%", playerData.getPlayerName());
            replacement.add("%uuid%", playerData.getUniqueId().toString());
            replacement.add("%lastSeen%", (Bukkit.getPlayer(playerData.getPlayerName()) != null ? "Now" : DateUtils.getDate(playerData.getLastSeen())));
            replacement.add("%lastSeenAgo%", playerData.getLastSeenAgo());
            replacement.add("%rank%", playerData.getHighestRank().getDisplayName());

            item.addLoreLine(replacement.toString());
        });
        return item.toItemStack();
    }

    @Override
    public int getSlot() {
        return slot;
    }
}
