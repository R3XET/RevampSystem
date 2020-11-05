package eu.revamp.system.menus.rank;

import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankListMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Ranks";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        plugin.getRankManagement().getRanks().stream().sorted(Comparator.comparingInt(RankData::getWeight)).forEach(rankData -> {
            slots.add(new RankSlot(rankData));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        return null;
    }

    @AllArgsConstructor
    private class RankSlot extends Slot {
        private RankData rankData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.NETHER_STAR);
            item.setName(rankData.getDisplayName());
            plugin.getCoreConfig().getStringList("rank-list-item-lore").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("%color%", rankData.getColor().toString());
                replacement.add("%name%", rankData.getName());
                replacement.add("%prefix%", !rankData.getPrefix().equals("") ? rankData.getPrefix().replace("§", "#mMk2X2") : "None");
                replacement.add("%suffix%", !rankData.getSuffix().equals("") ? rankData.getSuffix() : "None");
                replacement.add("%inheritance%", rankData.getInheritance().size() == 0 ? "None" :
                        eu.revamp.spigot.utils.string.StringUtils.getStringFromList(rankData.getInheritance()));
                replacement.add("%totalPerms%", rankData.getPermissions().size());
                replacement.add("%weight%", rankData.getWeight());
                item.addLoreLine(replacement.toString().replace("#mMk2X2", "&"));
            });
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
