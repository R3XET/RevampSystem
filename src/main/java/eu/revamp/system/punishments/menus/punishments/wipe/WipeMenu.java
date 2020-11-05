package eu.revamp.system.punishments.menus.punishments.wipe;

import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.menu.menu.AquaMenu;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.system.utilities.chat.Color;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class WipeMenu extends AquaMenu {

    private final PlayerData playerData;

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();



        for (int i = 0; i < 36; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    @Override
    public String getName(Player player) {
        return Color.translate("&7Wipe punishments");
    }


    @AllArgsConstructor
    public class BansSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability((short) 5);
            item.setNameWithArrows("&aBans");
            item.addLoreLine("");
            item.addLoreLine("&7Click to wipe");
            item.addLoreLine("&7all bans");
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            // TODO FINISH
        }
    }

}
