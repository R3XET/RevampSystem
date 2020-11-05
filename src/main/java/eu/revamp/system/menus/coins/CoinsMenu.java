package eu.revamp.system.menus.coins;

import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menu.menu.AquaMenu;
import eu.revamp.system.menu.slots.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CoinsMenu extends AquaMenu {

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new InfoSlot());
        slots.add(new BuySlot());

        for (int i = 0; i < 27; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    @Override
    public String getName(Player player) {
        return "&bCoins &7[&a$$&7]";
    }

    private class InfoSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
            item.setDurability((short) 3);
            item.setName("&bAbout " + playerData.getNameWithColor());
            item.setSkullOwner(player.getName());
            item.addLoreLine(" ");
            item.addLoreLine("&bCurrent coins&7: &3" + playerData.getCoins());
            item.addLoreLine("&bYou can purchase a total of &3" +
                    playerData.getPurchasableRanks().size() + " ranks&b.");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public int[] getSlots() {
            return new int[]{15};
        }
    }

    private class BuySlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.INK_SACK).setDurability((short) 1);
            item.setName("&bPurchase Ranks");
            item.addLoreLine(" ");
            item.addLoreLine("&7Click to purchase temporary");
            item.addLoreLine("&7ranks using your coins!");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (plugin.getRankManagement().getRanks().stream().noneMatch(RankData::isPurchasable)) {
                player.sendMessage(Language.COINS_NO_RANKS_TO_PURCHASE.toString());
                return;
            }
            if (playerData.getPurchasableRanks().size() == 0) {
                player.sendMessage(Language.COINS_DONT_HAVE_ENOUGH.toString());
                return;
            }
            new CoinsRankMenu().open(player);
        }
    }
}
