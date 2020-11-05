package eu.revamp.system.menus.tags;

import eu.revamp.spigot.utils.block.wool.WoolUtils;
import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class TagsColorMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Tags";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        Stream.of(ChatColor.values()).filter(ChatColor::isColor).forEach(chatColor -> {
            slots.add(new TagColorSlot(chatColor));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.ARROW).setName("&c&lGo Back!").toItemStack();
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                new TagsMainMenu().open(player);
            }

            @Override
            public int getSlot() {
                return 41;
            }

            @Override
            public int[] getSlots() {
                return new int[]{39};
            }
        });

        return slots;
    }


    @AllArgsConstructor
    private class TagColorSlot extends Slot {
        private ChatColor chatColor;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(chatColor + CCUtils.convertChatColor(chatColor, true));
            item.setDurability((short) WoolUtils.convertChatColorToWoolData(chatColor));
            item.addLoreLine("&8&m--------------------------------");
            item.addLoreLine("&7Click to set your tag color to " + chatColor + CCUtils.convertChatColor(chatColor, true) + "&7!");
            item.addLoreLine("&8&m--------------------------------");
            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setTagColor(CCUtils.convertChatColor(chatColor));

            player.closeInventory();
            PlayerUtils.playSound(player, Sound.LEVEL_UP);

            player.sendMessage(Language.TAGS_COLOR_APPLIED.toString().replace("%color%", chatColor + CCUtils.convertChatColor(chatColor, true)));
            Tasks.runAsync(plugin, playerData::saveData);
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
