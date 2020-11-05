package eu.revamp.system.menus.staffmode;

import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.system.utilities.chat.Color;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OnlineStaffMenu extends SwitchableMenu {

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        PlayerUtils.getOnlinePlayers().stream().filter(online -> online.hasPermission(plugin.getStaffModeManagement().getStaffPermission())).forEach(online -> {
            slots.add(new PlayerSlot(online));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {

        return null;
    }

    @Override
    public String getPagesTitle(Player player) {
        return Color.translate("&7Staff Online");
    }

    @AllArgsConstructor
    private class PlayerSlot extends Slot {
        private Player target;

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
            item.setName(playerData.getNameWithColor());
            item.setDurability((short) 3);
            item.setSkullOwner(target.getName());
            plugin.getStaffModeFile().getStringList("online-staff-gui.lore").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("%isVanished%", playerData.isVanished() ? "&aYes" : "&cNo");
                replacement.add("%isStaffMode%", playerData.isInStaffMode() ? "&aYes" : "&cNo");
                replacement.add("%rank%", playerData.getHighestRank().getDisplayName());
                replacement.add("%target%", target.getName());
                item.addLoreLine(replacement.toString());
            });
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            Player to = Bukkit.getPlayer(target.getName());

            if (to == null) {
                update(player);
                return;
            }
            player.teleport(to);
            player.sendMessage(Language.TELEPORTED_TO_PLAYER.toString()
                    .replace("%target%", to.getName()));
        }
    }
}
